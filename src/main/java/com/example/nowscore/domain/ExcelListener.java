package com.example.nowscore.domain;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ExcelListener extends AnalysisEventListener<ExcelEntity> {
    public List<ExcelEntity> getDatas() {
        return datas;
    }

    public void setDatas(List<ExcelEntity> datas) {
        this.datas = datas;
    }

    private List<ExcelEntity> datas = new ArrayList<>();

    @Override
    public void invoke(ExcelEntity excelEntity, AnalysisContext analysisContext) {
        log.info("读取到数据：{}" ,excelEntity.toString());
        datas.add(excelEntity);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
