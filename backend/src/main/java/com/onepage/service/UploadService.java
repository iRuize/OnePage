package com.onepage.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务（服务层接口）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
public interface UploadService {

    /**
     * 保存图片并返回可访问的相对路径
     */
    String storeImage(MultipartFile file);
}
