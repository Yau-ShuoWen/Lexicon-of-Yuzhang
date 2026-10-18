package com.shuowen.yuzong.reference.tsv;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ObjectListing;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ReferenceTsvService
{
    private static final String PREFIX = "reference-tsv/jingon/chars/";
    private static final String[] COLUMNS = {"本词", "国际音标", "方言拼音", "汉语拼音", "词汇属性",
            "对应官话词", "官话拼音", "词频", "同义词", "新旧标记"};
    private static final int MAX_BYTES = 20 * 1024 * 1024;

    private final OSS oss;
    private final ObjectMapper objectMapper;
    private final String bucket;

    public ReferenceTsvService(OSS oss, ObjectMapper objectMapper,
                               @Value("${aliyun.oss.bucketName}") String bucket)
    {
        this.oss = oss;
        this.objectMapper = objectMapper;
        this.bucket = bucket;
    }

    public ReferenceTsvVersion upload(MultipartFile file, String displayName) throws IOException
    {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("请选择 TSV 文件");
        if (file.getSize() > MAX_BYTES) throw new IllegalArgumentException("文件不能超过 20 MB");
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase(java.util.Locale.ROOT).endsWith(".tsv"))
            throw new IllegalArgumentException("只支持 .tsv 文件");
        name = name.replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1);
        String title = validatedName(displayName == null || displayName.isBlank()
                ? name.substring(0, name.length() - 4) : displayName);
        byte[] bytes = file.getBytes();
        List<String[]> rows = parse(bytes);
        String id = UUID.randomUUID().toString();
        ReferenceTsvVersion version = new ReferenceTsvVersion(id, name, Instant.now(),
                rows.size(), bytes.length, sha256(bytes), title, false);
        String sourceKey = sourceKey(id);
        oss.putObject(bucket, sourceKey, new ByteArrayInputStream(bytes));
        try
        {
            save(version);
        }
        catch (RuntimeException | IOException error)
        {
            try { oss.deleteObject(bucket, sourceKey); }
            catch (RuntimeException cleanupError) { error.addSuppressed(cleanupError); }
            throw error;
        }
        return version;
    }

    public List<ReferenceTsvVersion> versions() throws IOException
    {
        List<ReferenceTsvVersion> result = new ArrayList<>();
        String marker = null;
        do
        {
            ListObjectsRequest request = new ListObjectsRequest(bucket);
            request.setPrefix(PREFIX);
            request.setMarker(marker);
            request.setMaxKeys(1000);
            ObjectListing listing = oss.listObjects(request);
            for (var object : listing.getObjectSummaries())
            {
                String key = object.getKey();
                if (!key.matches(PREFIX + "[0-9a-f-]{36}/meta\\.json")) continue;
                try (var input = oss.getObject(bucket, key).getObjectContent())
                {
                    result.add(objectMapper.readValue(input, ReferenceTsvVersion.class));
                }
            }
            marker = listing.isTruncated() ? listing.getNextMarker() : null;
        }
        while (marker != null);
        result.sort((a, b) -> b.uploadedAt().compareTo(a.uploadedAt()));
        return result;
    }

    public ReferenceTsvVersion version(String id) throws IOException
    {
        checkId(id);
        if (!oss.doesObjectExist(bucket, metaKey(id))) throw new NoSuchElementException("版本不存在");
        try (var input = oss.getObject(bucket, metaKey(id)).getObjectContent())
        {
            return objectMapper.readValue(input, ReferenceTsvVersion.class);
        }
    }

    public synchronized ReferenceTsvVersion rename(String id, String name) throws IOException
    {
        ReferenceTsvVersion current = version(id);
        ReferenceTsvVersion updated = new ReferenceTsvVersion(current.id(), current.originalName(),
                current.uploadedAt(), current.rowCount(), current.size(), current.sha256(),
                validatedName(name), current.locked());
        save(updated);
        return updated;
    }

    public synchronized ReferenceTsvVersion setLocked(String id, boolean locked) throws IOException
    {
        ReferenceTsvVersion current = version(id);
        ReferenceTsvVersion updated = new ReferenceTsvVersion(current.id(), current.originalName(),
                current.uploadedAt(), current.rowCount(), current.size(), current.sha256(),
                current.name(), locked);
        save(updated);
        return updated;
    }

    public synchronized void delete(String id) throws IOException
    {
        ReferenceTsvVersion current = version(id);
        if (current.locked()) throw new IllegalArgumentException("请先解锁这个版本，再删除");
        // 原件删除失败时保留版本入口，管理员可以重试删除。
        oss.deleteObject(bucket, sourceKey(id));
        oss.deleteObject(bucket, metaKey(id));
    }

    private void save(ReferenceTsvVersion version) throws IOException
    {
        byte[] meta = objectMapper.writeValueAsBytes(version);
        oss.putObject(bucket, metaKey(version.id()), new ByteArrayInputStream(meta));
    }

    private static String validatedName(String name)
    {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("请输入版本名称");
        String value = name.trim();
        if (value.length() > 80 || value.chars().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("版本名称不能超过 80 个字符，也不能包含控制字符");
        return value;
    }

    public Page rows(String id, String column, String query, int page, int size) throws IOException
    {
        version(id);
        if (page < 1 || size < 1 || size > 200) throw new IllegalArgumentException("分页参数无效");
        int columnIndex = -1;
        if (column != null && !column.isBlank() && !column.equals("all"))
        {
            columnIndex = Arrays.asList(COLUMNS).indexOf(column);
            if (columnIndex < 0) throw new IllegalArgumentException("查询列无效");
        }
        List<String[]> all;
        try (var input = oss.getObject(bucket, sourceKey(id)).getObjectContent())
        {
            all = parse(input.readAllBytes());
        }
        String term = query == null ? "" : query.trim();
        List<Row> matches = new ArrayList<>();
        for (int i = 0; i < all.size(); i++)
        {
            String[] values = all.get(i);
            if (term.isEmpty() || matches(values, columnIndex, term))
                matches.add(new Row(i + 2, values));
        }
        long start = (long) (page - 1) * size;
        int from = (int) Math.min(start, matches.size());
        int to = (int) Math.min(start + size, matches.size());
        return new Page(List.of(COLUMNS), matches.subList(from, to), matches.size(), page, size);
    }

    private static boolean matches(String[] values, int column, String term)
    {
        if (column >= 0) return values[column].contains(term);
        for (String value : values) if (value.contains(term)) return true;
        return false;
    }

    private static List<String[]> parse(byte[] bytes)
    {
        if (bytes.length > MAX_BYTES) throw new IllegalArgumentException("文件不能超过 20 MB");
        String text;
        try
        {
            text = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes)).toString();
        }
        catch (CharacterCodingException error)
        {
            throw new IllegalArgumentException("文件必须使用 UTF-8 编码", error);
        }
        if (text.startsWith("\uFEFF")) text = text.substring(1);
        String[] lines = text.split("\\r?\\n", -1);
        if (lines.length < 2 || !Arrays.equals(lines[0].split("\\t", -1), COLUMNS))
            throw new IllegalArgumentException("表头与字表格式不一致");
        List<String[]> rows = new ArrayList<>();
        for (int i = 1; i < lines.length; i++)
        {
            if (i == lines.length - 1 && lines[i].isEmpty()) break;
            String[] cells = lines[i].split("\\t", -1);
            if (cells.length != COLUMNS.length)
                throw new IllegalArgumentException("第 " + (i + 1) + " 行不是 10 列");
            rows.add(cells);
        }
        if (rows.isEmpty()) throw new IllegalArgumentException("文件没有数据行");
        return rows;
    }

    private static String sha256(byte[] bytes)
    {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)); }
        catch (NoSuchAlgorithmException error) { throw new IllegalStateException(error); }
    }

    private static void checkId(String id)
    {
        if (id == null || !id.matches("[0-9a-f-]{36}")) throw new IllegalArgumentException("版本标识无效");
    }

    private static String sourceKey(String id) { return PREFIX + id + "/source.tsv"; }
    private static String metaKey(String id) { return PREFIX + id + "/meta.json"; }

    public record Row(int line, String[] values) {}
    public record Page(List<String> columns, List<Row> rows, int total, int page, int size) {}
}
