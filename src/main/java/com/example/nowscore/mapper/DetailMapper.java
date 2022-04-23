package com.example.nowscore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.nowscore.domain.Detail;
import com.example.nowscore.domain.DetailStatistic;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DetailMapper extends BaseMapper<Detail> {
    DetailStatistic selectDetailStatistic(String detailUrl);
}
