package com.love.controller;

import com.love.common.ApiResult;
import com.love.dto.EssaySaveRequest;
import com.love.dto.EssayVO;
import com.love.service.EssayService;
import com.love.util.UserContext;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 随笔接口
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/essays")
public class EssayController {

    private final EssayService essayService;

    public EssayController(EssayService essayService) {
        this.essayService = essayService;
    }

    @GetMapping
    public ApiResult<List<EssayVO>> list() {
        return ApiResult.ok(essayService.list(UserContext.get()));
    }

    @PostMapping
    public ApiResult<EssayVO> create(@Valid @RequestBody EssaySaveRequest request) {
        return ApiResult.ok(essayService.create(UserContext.get(), request));
    }

    @PutMapping("/{id}")
    public ApiResult<EssayVO> update(@PathVariable("id") Long id,
                                     @Valid @RequestBody EssaySaveRequest request) {
        return ApiResult.ok(essayService.update(UserContext.get(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable("id") Long id) {
        essayService.delete(UserContext.get(), id);
        return ApiResult.ok();
    }
}
