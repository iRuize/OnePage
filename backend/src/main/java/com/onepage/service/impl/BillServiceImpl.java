package com.onepage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.onepage.common.BizException;
import com.onepage.dto.BillSaveRequest;
import com.onepage.dto.BillVO;
import com.onepage.mapper.BillMapper;
import com.onepage.po.Bill;
import com.onepage.service.BillService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 账本服务实现（服务层）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Service
public class BillServiceImpl implements BillService {

    private final BillMapper billMapper;

    public BillServiceImpl(BillMapper billMapper) {
        this.billMapper = billMapper;
    }

    @Override
    public List<BillVO> list(Long userId) {
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<Bill>()
                .eq(Bill::getUserId, userId)
                .orderByDesc(Bill::getBillDate)
                .orderByDesc(Bill::getCreatedAt);
        return billMapper.selectList(wrapper).stream()
                .map(BillVO::from)
                .collect(Collectors.toList());
    }

    @Override
    public BillVO create(Long userId, BillSaveRequest request) {
        long now = System.currentTimeMillis();
        Bill bill = new Bill();
        bill.setUserId(userId);
        applyRequest(bill, request);
        bill.setCreatedAt(now);
        bill.setUpdatedAt(now);
        billMapper.insert(bill);
        return BillVO.from(bill);
    }

    @Override
    public BillVO update(Long userId, Long id, BillSaveRequest request) {
        Bill bill = requireOwned(userId, id);
        applyRequest(bill, request);
        bill.setUpdatedAt(System.currentTimeMillis());
        billMapper.updateById(bill);
        return BillVO.from(bill);
    }

    @Override
    public void delete(Long userId, Long id) {
        requireOwned(userId, id);
        billMapper.deleteById(id);
    }

    private void applyRequest(Bill bill, BillSaveRequest request) {
        bill.setType(request.getType());
        bill.setAmount(request.getAmount());
        bill.setCategory(request.getCategory());
        bill.setRemark(request.getRemark());
        bill.setBillDate(request.getBillDate());
    }

    private Bill requireOwned(Long userId, Long id) {
        Bill bill = billMapper.selectById(id);
        if (bill == null || !bill.getUserId().equals(userId)) {
            throw new BizException(404, "账单不存在或无权访问");
        }
        return bill;
    }
}
