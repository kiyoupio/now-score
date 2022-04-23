package com.example.nowscore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.nowscore.domain.Detail;
import com.example.nowscore.domain.DetailStatistic;

public interface DetailService extends IService<Detail> {
    DetailStatistic selectDetailStatistic(String detailUrl);
}
