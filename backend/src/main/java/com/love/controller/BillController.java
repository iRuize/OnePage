package com.love.controller;

import com.love.common.ApiResult;
import com.love.dto.BillSaveRequest;
import com.love.dto.BillVO;
import com.love.service.BillService;
import com.love.util.UserContext;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 账本接口
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    public ApiResult<List<BillVO>> list() {
        return ApiResult.ok(billService.list(UserContext.get()));
    }

    @PostMapping
    public ApiResult<BillVO> create(@Valid @RequestBody BillSaveRequest request) {
        return ApiResult.ok(billService.create(UserContext.get(), request));
    }

    @PutMapping("/{id}")
    public ApiResult<BillVO> update(@PathVariable("id") Long id,
                                    @Valid @RequestBody BillSaveRequest request) {
        return ApiResult.ok(billService.update(UserContext.get(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable("id") Long id) {
        billService.delete(UserContext.get(), id);
        return ApiResult.ok();
    }
}
