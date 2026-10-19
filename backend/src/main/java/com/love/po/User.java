package com.love.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户持久化对象（对应表 sys_user）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    /**
     * PBKDF2 密码摘要，格式：迭代次数:salt:hash
     */
    private String passwordHash;

    private Long createdAt;

    private Long lastLoginAt;
}
