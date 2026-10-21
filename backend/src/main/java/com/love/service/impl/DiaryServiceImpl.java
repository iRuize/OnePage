package com.love.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.love.common.BizException;
import com.love.dto.DiarySaveRequest;
import com.love.dto.DiaryVO;
import com.love.mapper.DiaryMapper;
import com.love.po.Diary;
import com.love.service.DiaryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 日记服务实现（服务层）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Service
public class DiaryServiceImpl implements DiaryService {

    private final DiaryMapper diaryMapper;

    public DiaryServiceImpl(DiaryMapper diaryMapper) {
        this.diaryMapper = diaryMapper;
    }

    @Override
    public List<DiaryVO> list(Long userId) {
        LambdaQueryWrapper<Diary> wrapper = new LambdaQueryWrapper<Diary>()
                .eq(Diary::getUserId, userId)
                .orderByDesc(Diary::getCreatedAt);
        return diaryMapper.selectList(wrapper).stream()
                .map(DiaryVO::from)
                .collect(Collectors.toList());
    }

    @Override
    public DiaryVO get(Long userId, Long id) {
        return DiaryVO.from(requireOwned(userId, id));
    }

    @Override
    public DiaryVO create(Long userId, DiarySaveRequest request) {
        long now = System.currentTimeMillis();
        Diary diary = new Diary();
        diary.setUserId(userId);
        applyRequest(diary, request);
        diary.setCreatedAt(now);
        diary.setUpdatedAt(now);
        diaryMapper.insert(diary);
        return DiaryVO.from(diary);
    }

    @Override
    public DiaryVO update(Long userId, Long id, DiarySaveRequest request) {
        Diary diary = requireOwned(userId, id);
        applyRequest(diary, request);
        diary.setUpdatedAt(System.currentTimeMillis());
        diaryMapper.updateById(diary);
        return DiaryVO.from(diary);
    }

    @Override
    public void delete(Long userId, Long id) {
        requireOwned(userId, id);
        diaryMapper.deleteById(id);
    }

    private void applyRequest(Diary diary, DiarySaveRequest request) {
        diary.setTitle(request.getTitle().trim());
        diary.setContent(request.getContent());
        diary.setMood(request.getMood());
        diary.setImages(joinImages(request.getImages()));
    }

    private Diary requireOwned(Long userId, Long id) {
        Diary diary = diaryMapper.selectById(id);
        if (diary == null || !diary.getUserId().equals(userId)) {
            throw new BizException(404, "日记不存在或无权访问");
        }
        return diary;
    }

    private String joinImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        String joined = images.stream()
                .filter(item -> item != null && !item.trim().isEmpty())
                .collect(Collectors.joining(","));
        return joined.isEmpty() ? null : joined;
    }
}
