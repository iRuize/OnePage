package com.onepage.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 日记持久化对象（对应表 sys_diary）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
@TableName("sys_diary")
public class Diary {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户 ID
     */
    private Long userId;

    /**
     * 日记标题
     */
    private String title;

    /**
     * 日记正文
     */
    private String content;

    /**
     * 心情标识
     */
    private String mood;

    /**
     * 图片地址，多个以英文逗号分隔
     */
    private String images;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createdAt;

    /**
     * 更新时间（毫秒时间戳）
     */
    private Long updatedAt;
}
