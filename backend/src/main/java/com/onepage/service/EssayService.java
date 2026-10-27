package com.onepage.service;

import com.onepage.dto.EssaySaveRequest;
import com.onepage.dto.EssayVO;

import java.util.List;

/**
 * 随笔服务（服务层接口）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
public interface EssayService {

    /**
     * 查询当前用户的随笔列表（按创建时间倒序）
     */
    List<EssayVO> list(Long userId);

    /**
     * 新建随笔
     */
    EssayVO create(Long userId, EssaySaveRequest request);

    /**
     * 更新随笔
     */
    EssayVO update(Long userId, Long id, EssaySaveRequest request);

    /**
     * 删除随笔
     */
    void delete(Long userId, Long id);
}
