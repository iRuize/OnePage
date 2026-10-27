package com.onepage.dto;

import com.onepage.po.User;
import lombok.Data;

/**
 * 用户视图对象（脱敏，不暴露密码摘要）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private Long createdAt;

    private Long lastLoginAt;

    public static UserVO from(User user) {
        UserVO vo = new UserVO();
        vo.id = user.getId();
        vo.username = user.getUsername();
        vo.nickname = user.getNickname();
        vo.avatar = user.getAvatar();
        vo.createdAt = user.getCreatedAt();
        vo.lastLoginAt = user.getLastLoginAt();
        return vo;
    }
}
