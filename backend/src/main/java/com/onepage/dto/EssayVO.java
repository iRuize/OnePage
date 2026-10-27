package com.onepage.dto;

import com.onepage.po.Essay;
import lombok.Data;

/**
 * 随笔视图对象
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
public class EssayVO {

    private Long id;

    private String title;

    private String content;

    private Long createdAt;

    private Long updatedAt;

    public static EssayVO from(Essay essay) {
        EssayVO vo = new EssayVO();
        vo.id = essay.getId();
        vo.title = essay.getTitle();
        vo.content = essay.getContent();
        vo.createdAt = essay.getCreatedAt();
        vo.updatedAt = essay.getUpdatedAt();
        return vo;
    }
}
