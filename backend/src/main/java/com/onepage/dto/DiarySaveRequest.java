package com.onepage.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 日记保存请求
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
public class DiarySaveRequest {

    @NotBlank(message = "请输入日记标题")
    @Size(max = 100, message = "标题最多 100 个字符")
    private String title;

    @Size(max = 20000, message = "正文最多 20000 个字符")
    private String content;

    @Size(max = 20, message = "心情标识过长")
    private String mood;

    @Size(max = 9, message = "最多上传 9 张图片")
    private List<@Size(max = 255, message = "图片地址过长") String> images;
}
