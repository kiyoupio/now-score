package com.example.nowscore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nowscore.domain.Trending;
import com.example.nowscore.mapper.TrendingMapper;
import com.example.nowscore.service.TrendingService;
import org.springframework.stereotype.Service;

@Service
public class TrendingServiceImpl extends ServiceImpl<TrendingMapper, Trending> implements TrendingService {
}
