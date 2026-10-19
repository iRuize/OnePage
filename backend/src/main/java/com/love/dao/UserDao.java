package com.love.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.love.entity.User;

/**
 * 用户数据访问层（数据库连接层）
 *
 * <p>继承 MyBatis-Plus 的 {@link BaseMapper}，由框架生成基础 CRUD SQL，
 * 复杂查询可在此扩展自定义方法。</p>
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
public interface UserDao extends BaseMapper<User> {
}
