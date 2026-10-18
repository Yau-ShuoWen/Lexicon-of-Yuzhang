package com.shuowen.yuzong.reference.tsv;

import com.shuowen.yuzong.util.tuple.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reference-tsv")
@RequiredArgsConstructor
public class ReferenceTsvController
{
    private final ReferenceTsvService service;
    private final ReferenceTsvDiffService diffService;

    @PostMapping("/versions")
    public APIResponse<ReferenceTsvVersion> upload(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name) throws IOException
    {
        return APIResponse.success(service.upload(file, name));
    }

    @GetMapping("/versions")
    public APIResponse<List<ReferenceTsvVersion>> versions() throws IOException
    {
        return APIResponse.success(service.versions());
    }

    @PatchMapping("/versions/{id}/name")
    public APIResponse<ReferenceTsvVersion> rename(@PathVariable String id, @RequestBody NameRequest body) throws IOException
    {
        return APIResponse.success(service.rename(id, body.name()));
    }

    @PatchMapping("/versions/{id}/lock")
    public APIResponse<ReferenceTsvVersion> lock(@PathVariable String id, @RequestBody LockRequest body) throws IOException
    {
        return APIResponse.success(service.setLocked(id, body.locked()));
    }

    @DeleteMapping("/versions/{id}")
    public APIResponse<Void> delete(@PathVariable String id) throws IOException
    {
        service.delete(id);
        return APIResponse.success();
    }

    public record NameRequest(String name) {}
    public record LockRequest(boolean locked) {}

    @GetMapping("/compare")
    public APIResponse<ReferenceTsvDiffService.DiffPage> compare(
            @RequestParam String from, @RequestParam String to,
            @RequestParam(defaultValue = "changes") String type,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) throws IOException
    {
        return APIResponse.success(diffService.compare(from, to, type, query, page, size));
    }

    @GetMapping("/versions/{id}/rows")
    public APIResponse<ReferenceTsvService.Page> rows(@PathVariable String id,
            @RequestParam(defaultValue = "all") String column,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) throws IOException
    {
        return APIResponse.success(service.rows(id, column, query, page, size));
    }
}
