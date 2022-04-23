package com.example.nowscore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.nowscore.domain.Meta;
import com.example.nowscore.mapper.MetaMapper;
import com.example.nowscore.service.MetaService;
import org.springframework.stereotype.Service;

@Service
public class MetaServiceImpl extends ServiceImpl<MetaMapper, Meta> implements MetaService {
}
