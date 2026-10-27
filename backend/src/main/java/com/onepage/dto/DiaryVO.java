package com.onepage.dto;

import com.onepage.po.Diary;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日记视图对象
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
public class DiaryVO {

    private Long id;

    private String title;

    private String content;

    private String mood;

    private List<String> images;

    private Long createdAt;

    private Long updatedAt;

    public static DiaryVO from(Diary diary) {
        DiaryVO vo = new DiaryVO();
        vo.id = diary.getId();
        vo.title = diary.getTitle();
        vo.content = diary.getContent();
        vo.mood = diary.getMood();
        vo.images = splitImages(diary.getImages());
        vo.createdAt = diary.getCreatedAt();
        vo.updatedAt = diary.getUpdatedAt();
        return vo;
    }

    private static List<String> splitImages(String images) {
        if (images == null || images.trim().isEmpty()) {
            return new ArrayList<String>();
        }
        return Arrays.stream(images.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }
}
