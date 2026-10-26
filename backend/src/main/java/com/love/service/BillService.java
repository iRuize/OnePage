package com.love.service;

import com.love.dto.BillSaveRequest;
import com.love.dto.BillVO;

import java.util.List;

/**
 * 账本服务（服务层接口）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
public interface BillService {

    /**
     * 查询当前用户的账单列表（按账单日期倒序）
     */
    List<BillVO> list(Long userId);

    /**
     * 新建账单
     */
    BillVO create(Long userId, BillSaveRequest request);

    /**
     * 更新账单
     */
    BillVO update(Long userId, Long id, BillSaveRequest request);

    /**
     * 删除账单
     */
    void delete(Long userId, Long id);
}
