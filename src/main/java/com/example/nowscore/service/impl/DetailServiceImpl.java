package com.example.nowscore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nowscore.domain.Detail;
import com.example.nowscore.domain.DetailStatistic;
import com.example.nowscore.mapper.DetailMapper;
import com.example.nowscore.service.DetailService;
import org.springframework.stereotype.Service;

@Service
public class DetailServiceImpl extends ServiceImpl<DetailMapper, Detail> implements DetailService {
    @Override
    public DetailStatistic selectDetailStatistic(String detailUrl) {
        return baseMapper.selectDetailStatistic(detailUrl);
    }
}
