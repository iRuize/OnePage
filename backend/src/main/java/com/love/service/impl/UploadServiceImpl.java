package com.love.service.impl;

import com.love.common.BizException;
import com.love.service.UploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件上传服务实现（服务层）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Service
public class UploadServiceImpl implements UploadService {

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<String>(
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp")
    );

    private static final long MAX_SIZE = 10L * 1024 * 1024;

    private final Path root;

    public UploadServiceImpl(@Value("${love.storage.upload-dir:./data/uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(400, "请选择要上传的图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BizException(400, "图片大小不能超过 10MB");
        }

        String extension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BizException(400, "仅支持 jpg、jpeg、png、gif、webp 格式");
        }

        String month = new SimpleDateFormat("yyyyMM").format(new Date());
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path directory = root.resolve(month);

        try {
            Files.createDirectories(directory);
            file.transferTo(directory.resolve(fileName).toFile());
        } catch (IOException e) {
            throw new BizException(500, "图片保存失败，请稍后再试");
        }

        return "/uploads/" + month + "/" + fileName;
    }

    private String extensionOf(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int dot = originalFilename.lastIndexOf('.');
        if (dot < 0 || dot == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(dot + 1).toLowerCase();
    }
}
