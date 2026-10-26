package com.love.dto;

import com.love.po.Bill;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 账单视图对象
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
public class BillVO {

    private Long id;

    private Integer type;

    private BigDecimal amount;

    private String category;

    private String remark;

    private Long billDate;

    private Long createdAt;

    private Long updatedAt;

    public static BillVO from(Bill bill) {
        BillVO vo = new BillVO();
        vo.id = bill.getId();
        vo.type = bill.getType();
        vo.amount = bill.getAmount();
        vo.category = bill.getCategory();
        vo.remark = bill.getRemark();
        vo.billDate = bill.getBillDate();
        vo.createdAt = bill.getCreatedAt();
        vo.updatedAt = bill.getUpdatedAt();
        return vo;
    }
}
