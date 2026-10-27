package com.love.controller;

import com.love.common.ApiResult;
import com.love.dto.DiarySaveRequest;
import com.love.dto.DiaryVO;
import com.love.service.DiaryService;
import com.love.util.UserContext;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 日记接口
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @GetMapping
    public ApiResult<List<DiaryVO>> list() {
        return ApiResult.ok(diaryService.list(UserContext.get()));
    }

    @GetMapping("/{id}")
    public ApiResult<DiaryVO> get(@PathVariable("id") Long id) {
        return ApiResult.ok(diaryService.get(UserContext.get(), id));
    }

    @PostMapping
    public ApiResult<DiaryVO> create(@Valid @RequestBody DiarySaveRequest request) {
        return ApiResult.ok(diaryService.create(UserContext.get(), request));
    }

    @PutMapping("/{id}")
    public ApiResult<DiaryVO> update(@PathVariable("id") Long id,
                                     @Valid @RequestBody DiarySaveRequest request) {
        return ApiResult.ok(diaryService.update(UserContext.get(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable("id") Long id) {
        diaryService.delete(UserContext.get(), id);
        return ApiResult.ok();
    }
}
