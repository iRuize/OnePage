package com.onepage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.onepage.common.BizException;
import com.onepage.dto.EssaySaveRequest;
import com.onepage.dto.EssayVO;
import com.onepage.mapper.EssayMapper;
import com.onepage.po.Essay;
import com.onepage.service.EssayService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 随笔服务实现（服务层）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Service
public class EssayServiceImpl implements EssayService {

    private final EssayMapper essayMapper;

    public EssayServiceImpl(EssayMapper essayMapper) {
        this.essayMapper = essayMapper;
    }

    @Override
    public List<EssayVO> list(Long userId) {
        LambdaQueryWrapper<Essay> wrapper = new LambdaQueryWrapper<Essay>()
                .eq(Essay::getUserId, userId)
                .orderByDesc(Essay::getCreatedAt);
        return essayMapper.selectList(wrapper).stream()
                .map(EssayVO::from)
                .collect(Collectors.toList());
    }

    @Override
    public EssayVO create(Long userId, EssaySaveRequest request) {
        long now = System.currentTimeMillis();
        Essay essay = new Essay();
        essay.setUserId(userId);
        essay.setTitle(request.getTitle().trim());
        essay.setContent(request.getContent());
        essay.setCreatedAt(now);
        essay.setUpdatedAt(now);
        essayMapper.insert(essay);
        return EssayVO.from(essay);
    }

    @Override
    public EssayVO update(Long userId, Long id, EssaySaveRequest request) {
        Essay essay = requireOwned(userId, id);
        essay.setTitle(request.getTitle().trim());
        essay.setContent(request.getContent());
        essay.setUpdatedAt(System.currentTimeMillis());
        essayMapper.updateById(essay);
        return EssayVO.from(essay);
    }

    @Override
    public void delete(Long userId, Long id) {
        requireOwned(userId, id);
        essayMapper.deleteById(id);
    }

    private Essay requireOwned(Long userId, Long id) {
        Essay essay = essayMapper.selectById(id);
        if (essay == null || !essay.getUserId().equals(userId)) {
            throw new BizException(404, "随笔不存在或无权访问");
        }
        return essay;
    }
}
