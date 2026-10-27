package com.onepage.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 账单持久化对象（对应表 sys_bill）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
@TableName("sys_bill")
public class Bill {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户 ID
     */
    private Long userId;

    /**
     * 类型：1 支出，2 收入
     */
    private Integer type;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 分类
     */
    private String category;

    /**
     * 备注
     */
    private String remark;

    /**
     * 账单日期（毫秒时间戳）
     */
    private Long billDate;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createdAt;

    /**
     * 更新时间（毫秒时间戳）
     */
    private Long updatedAt;
}
