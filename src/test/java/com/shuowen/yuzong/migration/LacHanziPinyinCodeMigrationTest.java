package com.shuowen.yuzong.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shuowen.yuzong.util.core.Dialect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class LacHanziPinyinCodeMigrationTest
{
    /**
     * 确认预览结果无误后改成 true，再单独运行这个测试。
     */
    private static final boolean ENABLE_WRITE = false;

    /**
     * SQL 迁移校验得到的待补数量。数量变化时停止写入，避免更新错误的数据集。
     */
    private static final int EXPECTED_MISSING_CODE_COUNT = 37;

    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private JdbcTemplate jdbc;

    private record HanziRow(int id, String sc, String tc, String pinyinJson)
    {
    }

    private record HanziUpdate(int id, String sc, String tc, String oldJson, String newJson, int changedCount)
    {
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    @Commit
    void fillMissingPinyinCodes() throws Exception
    {
        List<HanziRow> rows = jdbc.query(
                "SELECT id, sc, tc, CAST(pinyin AS CHAR) AS pinyin_json FROM lac_hanzi ORDER BY id",
                (rs, rowNum) -> new HanziRow(
                        rs.getInt("id"),
                        rs.getString("sc"),
                        rs.getString("tc"),
                        rs.getString("pinyin_json")
                )
        );

        List<HanziUpdate> updates = new ArrayList<>();
        int missingCodeCount = 0;

        /* 先在内存中完成全部解析和计算。任何一项失败时，数据库不会发生更新。 */
        for (HanziRow row : rows)
        {
            JsonNode parsed = JSON.readTree(row.pinyinJson());
            if (!(parsed instanceof ArrayNode pinyins))
                throw new IllegalStateException("lac_hanzi.id=" + row.id() + " 的 pinyin 不是数组");

            ArrayNode updatedPinyins = pinyins.deepCopy();
            int changedInRow = 0;

            for (int index = 0; index < updatedPinyins.size(); index++)
            {
                JsonNode item = updatedPinyins.get(index);
                if (!(item instanceof ObjectNode pinyinItem))
                    throw new IllegalStateException(
                            "lac_hanzi.id=" + row.id() + " 的第 " + index + " 个拼音不是对象"
                    );

                JsonNode code = pinyinItem.get("code");
                if (code != null && !code.isNull()) continue;

                String pinyin = pinyinItem.path("pinyin").asText("");
                if (pinyin.isBlank())
                    throw new IllegalStateException(
                            "lac_hanzi.id=" + row.id() + " 的第 " + index + " 个拼音为空"
                    );

                String weight;
                try
                {
                    weight = Dialect.LAC.trustedCreatePinyin(pinyin).getWeight();
                }
                catch (RuntimeException exception)
                {
                    throw new IllegalStateException(
                            "无法计算 lac_hanzi.id=" + row.id() + "（" + row.sc() + "/" + row.tc()
                                    + "）拼音 " + pinyin + " 的排序码",
                            exception
                    );
                }

                if (weight == null || weight.isBlank())
                    throw new IllegalStateException(
                            "lac_hanzi.id=" + row.id() + " 的拼音 " + pinyin + " 生成了空排序码"
                    );

                pinyinItem.put("code", weight);
                changedInRow++;
                missingCodeCount++;

                System.out.printf(
                        "待回填：id=%d，汉字=%s/%s，拼音=%s，code=%s%n",
                        row.id(), row.sc(), row.tc(), pinyin, weight
                );
            }

            if (changedInRow > 0)
                updates.add(new HanziUpdate(
                        row.id(), row.sc(), row.tc(), row.pinyinJson(),
                        JSON.writeValueAsString(updatedPinyins), changedInRow
                ));
        }

        System.out.printf(
                "扫描完成：汉字 %d 条，待更新汉字 %d 条，待回填 code %d 个。%n",
                rows.size(), updates.size(), missingCodeCount
        );

        if (missingCodeCount == 0)
        {
            System.out.println("没有需要回填的 code，数据库未修改。");
            return;
        }

        if (missingCodeCount != EXPECTED_MISSING_CODE_COUNT)
            throw new IllegalStateException(
                    "待回填数量应为 " + EXPECTED_MISSING_CODE_COUNT + "，实际为 " + missingCodeCount
                            + "；数据库未修改，请先检查迁移结果。"
            );

        if (!ENABLE_WRITE)
        {
            System.out.println(
                    "当前为预览模式，数据库未修改。确认上面的 37 条结果后，将 ENABLE_WRITE 改为 true 再运行。"
            );
            return;
        }

        int updatedRows = 0;
        for (HanziUpdate update : updates)
        {
            int affected = jdbc.update(
                    "UPDATE lac_hanzi SET pinyin = ? WHERE id = ? AND CAST(pinyin AS CHAR) = ?",
                    update.newJson(), update.id(), update.oldJson()
            );
            if (affected != 1)
                throw new IllegalStateException(
                        "更新 lac_hanzi.id=" + update.id() + " 时原数据已变化，全部操作将回滚"
                );
            updatedRows += affected;
        }

        System.out.printf(
                "回填完成：更新汉字 %d 条，补全 code %d 个。事务将在测试结束时提交。%n",
                updatedRows, missingCodeCount
        );
    }
}
