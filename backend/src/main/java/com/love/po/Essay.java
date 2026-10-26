package com.love.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 随笔持久化对象（对应表 sys_essay）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
@TableName("sys_essay")
public class Essay {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户 ID
     */
    private Long userId;

    /**
     * 随笔标题
     */
    private String title;

    /**
     * 随笔正文
     */
    private String content;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createdAt;

    /**
     * 更新时间（毫秒时间戳）
     */
    private Long updatedAt;
}
