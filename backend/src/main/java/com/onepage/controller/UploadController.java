package com.onepage.controller;

import com.onepage.common.ApiResult;
import com.onepage.service.UploadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/image")
    public ApiResult<String> image(@RequestParam("file") MultipartFile file) {
        return ApiResult.ok(uploadService.storeImage(file));
    }
}
