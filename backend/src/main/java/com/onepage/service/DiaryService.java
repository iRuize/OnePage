package com.onepage.service;

import com.onepage.dto.DiarySaveRequest;
import com.onepage.dto.DiaryVO;

import java.util.List;

/**
 * 日记服务（服务层接口）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
public interface DiaryService {

    /**
     * 查询当前用户的日记列表（按创建时间倒序）
     */
    List<DiaryVO> list(Long userId);

    /**
     * 查询日记详情
     */
    DiaryVO get(Long userId, Long id);

    /**
     * 新建日记
     */
    DiaryVO create(Long userId, DiarySaveRequest request);

    /**
     * 更新日记
     */
    DiaryVO update(Long userId, Long id, DiarySaveRequest request);

    /**
     * 删除日记
     */
    void delete(Long userId, Long id);
}
