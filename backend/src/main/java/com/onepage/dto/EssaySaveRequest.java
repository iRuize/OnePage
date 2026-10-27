package com.onepage.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 随笔保存请求
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
public class EssaySaveRequest {

    @NotBlank(message = "请输入随笔标题")
    @Size(max = 100, message = "标题最多 100 个字符")
    private String title;

    @Size(max = 20000, message = "正文最多 20000 个字符")
    private String content;
}
