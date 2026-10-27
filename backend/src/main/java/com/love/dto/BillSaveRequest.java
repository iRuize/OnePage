package com.love.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 账单保存请求
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
public class BillSaveRequest {

    @NotNull(message = "请选择收支类型")
    @Min(value = 1, message = "收支类型不正确")
    @Max(value = 2, message = "收支类型不正确")
    private Integer type;

    @NotNull(message = "请输入金额")
    @DecimalMin(value = "0.01", message = "金额必须大于 0")
    @Digits(integer = 10, fraction = 2, message = "金额最多保留两位小数")
    private BigDecimal amount;

    @Size(max = 32, message = "分类最多 32 个字符")
    private String category;

    @Size(max = 255, message = "备注最多 255 个字符")
    private String remark;

    @NotNull(message = "请选择日期")
    private Long billDate;
}
