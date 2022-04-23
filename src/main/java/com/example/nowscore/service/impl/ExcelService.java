package com.example.nowscore.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.nowscore.domain.*;
import com.example.nowscore.service.DetailService;
import com.example.nowscore.service.MetaService;
import com.example.nowscore.service.TrendingService;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Excel服务类
 * @author kuangyoupeng1
 * @date 2022.4.1
 */
@Service
@Log4j2
public class ExcelService {
    @Resource
    DetailService detailService;
    @Resource
    MetaService metaService;
    @Resource
    TrendingService trendingService;

    /**
     * 导出excel
     * @param response
     */
    public void export(HttpServletResponse response) throws IOException {
        // 获取所有数据
        List<ExcelEntity> excelEntities = dataFilter();
        try {
            FileOutputStream fos = new FileOutputStream("D:\\export\\Data\\abc.xlsx");
            log.info("开始导出excel");
            EasyExcel.write(fos).head(ExcelEntity.class).excelType(ExcelTypeEnum.XLSX).sheet("球球").doWrite(excelEntities);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("导出用户信息列表Excel 失败");
        }
        log.info("导出结束");
//
//
//        response.setContentType("application/vnd.ms-excel");
//        response.setCharacterEncoding("utf-8");
//        response.setHeader("Content-disposition", "attachment;filename=demo.xlsx");
//        EasyExcel.write(response.getOutputStream(), ExcelEntity.class).sheet("now_score").doWrite(excelEntities);
    }

    private List<ExcelEntity> dataFilter() {
        List<ExcelEntity> result = new ArrayList<>(60000);
        int end = 100;
        int start = 1;
        while(true){
            try {
                List<Meta> metaList = metaService.list(new LambdaQueryWrapper<Meta>().between(Meta::getId, start, end));
                if (metaList == null || metaList.size() == 0){
                    log.info("程序结束");
                    break;
                }
                for (Meta meta : metaList) {
                    ExcelEntity excelEntity = new ExcelEntity();
                    excelEntity.setLeagueName(meta.getLeagueName());
                    excelEntity.setDetailUrl("http://live.nowscore.com/1x2/" + meta.getDetailUrl());
                    excelEntity.setLeagueTime(meta.getLeagueTime());
                    excelEntity.setHomeTeam(meta.getHomeTeam());
                    excelEntity.setGuestTeam(meta.getGuestTeam());
                    // 总体变化趋势
                    excelEntity.setTrending(terndingFilter(meta));
                    List<Detail> details = detailService.list(new LambdaQueryWrapper<Detail>().eq(Detail::getDetailUrl, meta.getDetailUrl()));
                    int caseType = 0;
                    double returnRate365 = 0;
                    // 遍历详情页每一行数据
                    for (Detail detail : details) {
                        boolean isExsit365 = false;
                        // 主赔率、和赔率、客赔率:即时
                        if ("Bet 365".equals(detail.getCompanyNameEn())){
                            excelEntity.setInitialHostWin(detail.getInstantHostWin());
                            excelEntity.setInitialDraw(detail.getInstantDraw());
                            excelEntity.setInitialGuestWin(detail.getInstantGuestWin());
                            excelEntity.setGameResult(getResult(meta.getScore(), detail));
                            returnRate365 = detail.getInstantReturnRate();
                            isExsit365 = true;
                        }
                        // 既不是主流也不是交易所则过滤
                        if (!"1".equals(detail.getIsMainCompany()) && !"1".equals(detail.getIsExchange())){
                            continue;
                        }
                        List<Trending> trendings = trendingService.list(new LambdaQueryWrapper<Trending>().eq(Trending::getDetailItemId, detail.getItemId()).last("limit 2"));
                        // 小于2不判断 且 前面没出现不一样的caseType
                        if (trendings.size()>=2 && caseType != -1){
                            double hostWin1 = trendings.get(0).getHostWin();
                            double draw1 = trendings.get(0).getDraw();
                            double guestWin1 = trendings.get(0).getGuestWin();
                            double hostWin2 = trendings.get(1).getHostWin();
                            double draw2 = trendings.get(1).getDraw();
                            double guestWin2 = trendings.get(1).getGuestWin();
                            if (caseType == 0){
                                caseType = check(hostWin1, hostWin2, draw1, draw2, guestWin1, guestWin2);
                                continue;
                            }
                            if (caseType != check(hostWin1,hostWin2,draw1,draw2,guestWin1,guestWin2)){
                                caseType = -1;
                            }
                        }
                        // trending 判断有不一致的 && 365公司的已经获取到
                        if (caseType == -1 && isExsit365){
                            break;
                        }
                    }
                    // 根据caseType的值做出判断
                    String trendingResult = TrendingEnum.getTypeName(caseType);
                    // 主流、交易所极端情况
                    excelEntity.setAmazing(trendingResult);
                    // statics处理
                    DetailStatistic detailStatistic = detailService.selectDetailStatistic(meta.getDetailUrl());
                    excelEntity.setFirstLine(firstLineFilter(detailStatistic));
                    excelEntity.setFirstLineMax(getMax(detailStatistic.getMaxKaliLow(), detailStatistic.getMaxKaliMid(), detailStatistic.getMaxKaliHigh()));
                    excelEntity.setSecondLine(secondLineFilter(detailStatistic));
                    excelEntity.setSecondLineMin(getMin(detailStatistic.getMinKaliLow(), detailStatistic.getMinKaliMid(), detailStatistic.getMinKaliHigh()));
                    excelEntity.setThirdLine(thirdLineFilter(detailStatistic));
                    excelEntity.setThirdLineMax(getMax(detailStatistic.getAvgKaliLow(), detailStatistic.getAvgKaliMid(), detailStatistic.getAvgKaliHigh()));
                    if (excelEntity.getInitialHostWin() != null && excelEntity.getInitialDraw() != null && excelEntity.getInitialGuestWin() != null){
                        excelEntity.setRelationToInitialAvg("和位看多");
                        excelEntity.setRelationToInstantAvg("和位看多");
                        // 与即时平均值的关系
                        if (excelEntity.getInitialHostWin()<detailStatistic.getInstantAvgHostWin() && excelEntity.getInitialDraw()>detailStatistic.getInstantAvgDraw() && excelEntity.getInitialGuestWin()>detailStatistic.getInstantAvgGuestWin()){
                            excelEntity.setRelationToInitialAvg("初指主位全面看多");
                        }
                        if (excelEntity.getInitialHostWin()==detailStatistic.getInstantAvgHostWin() && excelEntity.getInitialDraw()>detailStatistic.getInstantAvgDraw() && excelEntity.getInitialGuestWin()>detailStatistic.getInstantAvgGuestWin()){
                            excelEntity.setRelationToInitialAvg("初指主位部分看多");
                        }
                        if (excelEntity.getInitialHostWin()<detailStatistic.getInstantAvgHostWin() && excelEntity.getInitialDraw()==detailStatistic.getInstantAvgDraw() && excelEntity.getInitialGuestWin()>detailStatistic.getInstantAvgGuestWin()){
                            excelEntity.setRelationToInitialAvg("初指主位部分看多");
                        }
                        if (excelEntity.getInitialHostWin()<detailStatistic.getInstantAvgHostWin() && excelEntity.getInitialDraw()>detailStatistic.getInstantAvgDraw() && excelEntity.getInitialGuestWin()==detailStatistic.getInstantAvgGuestWin()){
                            excelEntity.setRelationToInitialAvg("初指主位部分看多");
                        }
                        if (excelEntity.getInitialHostWin()>detailStatistic.getInstantAvgHostWin() && excelEntity.getInitialDraw()<detailStatistic.getInstantAvgDraw() && excelEntity.getInitialGuestWin()<detailStatistic.getInstantAvgGuestWin()){
                            excelEntity.setRelationToInitialAvg("初指和客看多");
                        }
                        if (excelEntity.getInitialHostWin()>detailStatistic.getInstantAvgHostWin() && excelEntity.getInitialDraw()>detailStatistic.getInstantAvgDraw() && excelEntity.getInitialGuestWin()<detailStatistic.getInstantAvgGuestWin()){
                            excelEntity.setRelationToInitialAvg("初指和客看多");
                        }
                        // 与初指平均值关系
                        if (excelEntity.getInitialHostWin()<detailStatistic.getInitialAvgHostWin() && excelEntity.getInitialDraw()>detailStatistic.getInitialAvgDraw() && excelEntity.getInitialGuestWin()>detailStatistic.getInitialAvgGuestWin()){
                            excelEntity.setRelationToInstantAvg("初指主位全面看多");
                        }
                        if (excelEntity.getInitialHostWin()==detailStatistic.getInitialAvgHostWin() && excelEntity.getInitialDraw()>detailStatistic.getInitialAvgDraw() && excelEntity.getInitialGuestWin()>detailStatistic.getInitialAvgGuestWin()){
                            excelEntity.setRelationToInstantAvg("初指主位部分看多");
                        }
                        if (excelEntity.getInitialHostWin()<detailStatistic.getInitialAvgHostWin() && excelEntity.getInitialDraw()==detailStatistic.getInitialAvgDraw() && excelEntity.getInitialGuestWin()>detailStatistic.getInitialAvgGuestWin()){
                            excelEntity.setRelationToInstantAvg("初指主位部分看多");
                        }
                        if (excelEntity.getInitialHostWin()<detailStatistic.getInitialAvgHostWin() && excelEntity.getInitialDraw()>detailStatistic.getInitialAvgDraw() && excelEntity.getInitialGuestWin()==detailStatistic.getInitialAvgGuestWin()){
                            excelEntity.setRelationToInstantAvg("初指主位部分看多");
                        }
                        if (excelEntity.getInitialHostWin()>detailStatistic.getInitialAvgHostWin() && excelEntity.getInitialDraw()<detailStatistic.getInitialAvgDraw() && excelEntity.getInitialGuestWin()<detailStatistic.getInitialAvgGuestWin()){
                            excelEntity.setRelationToInstantAvg("初指和客看多");
                        }
                        if (excelEntity.getInitialHostWin()>detailStatistic.getInitialAvgHostWin() && excelEntity.getInitialDraw()>detailStatistic.getInitialAvgDraw() && excelEntity.getInitialGuestWin()<detailStatistic.getInitialAvgGuestWin()){
                            excelEntity.setRelationToInstantAvg("初指和客看多");
                        }
                    }
                    if (returnRate365!=0){
                        if (returnRate365<detailStatistic.getInitialAvgReturnRate() && returnRate365<detailStatistic.getInstantAvgReturnRate()){
                            excelEntity.setReturnRate("主位看多");
                        }
                        if (returnRate365>detailStatistic.getInitialAvgReturnRate() && returnRate365>detailStatistic.getInstantAvgReturnRate()){
                            excelEntity.setReturnRate("和客看多");
                        }
                    }
                    result.add(excelEntity);
                    log.info("excel数据单元:[{}]", excelEntity);
                }
                end = end + 100;
                start = start + 100;
            }catch (Exception e){
                log.error("dataFilter error:", e);

            }
        }
        log.info("程序结束");
        return result;
    }

    private String thirdLineFilter(DetailStatistic detailStatistic) {
        double low = detailStatistic.getAvgKaliLow();
        double mid = detailStatistic.getAvgKaliMid();
        double high = detailStatistic.getAvgKaliHigh();

        if (low == mid && mid == high){
            return "凯指相同";
        }else {
            return "凯指不同";
        }
    }

    private double getMin(double minKaliLow, double minKaliMid, double minKaliHigh) {
        return Math.min(Math.min(minKaliLow, minKaliMid), minKaliHigh);
    }

    private String secondLineFilter(DetailStatistic detailStatistic) {
        double low = detailStatistic.getMinKaliLow();
        double mid = detailStatistic.getMinKaliMid();
        double high = detailStatistic.getMinKaliHigh();

        if (low>mid && mid>high){
            return "321";
        }
        if (low<mid && mid<high){
            return "123";
        }
        if (low == mid && mid<high){
            return "113";
        }
        if (low<mid && mid == high){
            return "133";
        }
        if (low == mid && mid > high){
            return "331";
        }
        if (low>mid && mid == high){
            return "311";
        }
        return "杂";
    }

    private double getMax(double low, double mid, double high) {
        return Math.max(Math.max(low, mid), high);
    }

    private String firstLineFilter(DetailStatistic detailStatistic) {
        double low = detailStatistic.getMaxKaliLow();
        double mid = detailStatistic.getMaxKaliMid();
        double high = detailStatistic.getMaxKaliHigh();
        if (low<1 && mid<1 && low<mid && mid<high){
            return "098099100";
        }
        if (low<1 && mid==1 && low<mid && mid<high){
            return "099100101";
        }
        if (low<1 && mid>1 && low<mid && mid<high){
            return "099101102";
        }
        if (low>=1 && mid>1 && low<mid && mid<high){
            return "100101102";
        }
        if (low>mid && mid>high){
            return "987";
        }
        if (low == mid && mid < high){
            return "779";
        }
        if (low<mid && mid == high){
            return "799";
        }
        if (low == mid && mid>high){
            return "997";
        }
        return "杂";
    }

    private String getResult(String score, Detail detail) {
        String[] s = score.split("-");
        if (Integer.parseInt(s[0]) > Integer.parseInt(s[1])){
            return detail.getInstantHostWin().toString() + ":0" + ":0";
        }
        if (Integer.parseInt(s[0]) < Integer.parseInt(s[1])){
            return "0:" + "0:" + detail.getInstantGuestWin().toString();
        }
        return "0:" + detail.getInstantDraw() + ":0";
    }

    private String terndingFilter(Meta meta) {
        Set set = new HashSet();
        double a = meta.getHostWin1();
        double b = meta.getDraw1();
        double c = meta.getGuestWin1();
        double d = meta.getHostWin2();
        double e = meta.getDraw2();
        double f = meta.getGuestWin2();
        set.add(a);
        set.add(b);
        set.add(c);
        set.add(d);
        set.add(e);
        set.add(f);
        // 存在重复元素
        if (set.size() != 6){
            return "倾向于和";
        }
        if (d<a && a<b && b<e && e<c && c<f){
            return "标准主看多";
        }
        if (d<a && a<e && e<b && b<c && c<f){
            return "主和看多";
        }
        if ((d+a)<(b+e) && (b+e)<(c+f)){
            return "倾向主看多";
        }
        if (d>a && a>b && b>e && e>c && c>f){
            return "和客看多";
        }
        if ((d+a)>(b+e) && (b+e)>(c+f)){
            return "倾向客看多";
        }
        if (f<a && a<c && c<d){
            return "主反转为客";
        }
        if (d<c && c<a && a<f){
            return "客反为主";
        }
        if (a<d && d<b && b<e && e<f && f<c){
            return "客爆冷";
        }
        if (a<d && d<e && e<b && b<f && f<c){
            return "和客爆冷";
        }
        if (a>d && d>e && e>b && b>f && f>c){
            return "主爆冷";
        }
        if (a>d && d>b && b>e && e>f && f>c){
            return "主和爆冷";
        }
        return "倾向于和";
    }

    private int check(double hostWin1, double hostWin2, double draw1, double draw2, double guestWin1, double guestWin2){
        if (hostWin1<hostWin2 && draw1>draw2 && guestWin1>guestWin2) {
            return 1;
        }
        if (hostWin1<hostWin2 && draw1<draw2 && guestWin1>guestWin2){
            return 2;
        }
        if (hostWin1>hostWin2 && draw1>draw2 && guestWin1<guestWin2){
            return 3;
        }
        if (hostWin1>hostWin2 && draw1<draw2 && guestWin1<guestWin2){
            return 4;
        }
        if (hostWin1>hostWin2 && draw1<draw2 && guestWin1>guestWin2){
            return 5;
        }
        return -1;
    }

    public void load() throws FileNotFoundException {
        String fileName = "D:\\export\\Data\\abc.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        ExcelListener excelListener = new ExcelListener();
        EasyExcel.read(inputStream, ExcelEntity.class, excelListener).sheet().doRead();
        List<ExcelEntity> excelEntities = excelListener.getDatas();
        List<ReExcelEntity> reExcelEntities = new ArrayList<>(60000);
        for (ExcelEntity e :
                excelEntities) {
            ReExcelEntity reExcelEntity = new ReExcelEntity();
            String url = "http://live.nowscore.com/1x2/" + e.getDetailUrl();
            List<Detail> details = detailService.list(new LambdaQueryWrapper<Detail>().eq(Detail::getDetailUrl, e.getDetailUrl()));
            DetailStatistic detailStatistic = detailService.selectDetailStatistic(e.getDetailUrl());
            for (Detail detail : details) {
                if (e.getInitialHostWin() != null && e.getInitialDraw() != null && e.getInitialGuestWin() != null){
                    e.setRelationToInitialAvg("和位看多");
                    e.setRelationToInstantAvg("和位看多");
                    // 与初指平均值的关系
                    if (e.getInitialHostWin()<detailStatistic.getInstantAvgHostWin() && e.getInitialDraw()>detailStatistic.getInstantAvgDraw() && e.getInitialGuestWin()>detailStatistic.getInstantAvgGuestWin()){
                        e.setRelationToInitialAvg("初指主位全面看多");
                    }
                    if (e.getInitialHostWin()==detailStatistic.getInstantAvgHostWin() && e.getInitialDraw()>detailStatistic.getInstantAvgDraw() && e.getInitialGuestWin()>detailStatistic.getInstantAvgGuestWin()){
                        e.setRelationToInitialAvg("初指主位部分看多");
                    }
                    if (e.getInitialHostWin()<detailStatistic.getInstantAvgHostWin() && e.getInitialDraw()==detailStatistic.getInstantAvgDraw() && e.getInitialGuestWin()>detailStatistic.getInstantAvgGuestWin()){
                        e.setRelationToInitialAvg("初指主位部分看多");
                    }
                    if (e.getInitialHostWin()<detailStatistic.getInstantAvgHostWin() && e.getInitialDraw()>detailStatistic.getInstantAvgDraw() && e.getInitialGuestWin()==detailStatistic.getInstantAvgGuestWin()){
                        e.setRelationToInitialAvg("初指主位部分看多");
                    }
                    if (e.getInitialHostWin()>detailStatistic.getInstantAvgHostWin() && e.getInitialDraw()<detailStatistic.getInstantAvgDraw() && e.getInitialGuestWin()<detailStatistic.getInstantAvgGuestWin()){
                        e.setRelationToInitialAvg("初指和客看多");
                    }
                    if (e.getInitialHostWin()>detailStatistic.getInstantAvgHostWin() && e.getInitialDraw()>detailStatistic.getInstantAvgDraw() && e.getInitialGuestWin()<detailStatistic.getInstantAvgGuestWin()){
                        e.setRelationToInitialAvg("初指和客看多");
                    }
                    // 与即时平均值关系
                    if (e.getInitialHostWin()<detailStatistic.getInitialAvgHostWin() && e.getInitialDraw()>detailStatistic.getInitialAvgDraw() && e.getInitialGuestWin()>detailStatistic.getInitialAvgGuestWin()){
                        e.setRelationToInstantAvg("初指主位全面看多");
                    }
                    if (e.getInitialHostWin()==detailStatistic.getInitialAvgHostWin() && e.getInitialDraw()>detailStatistic.getInitialAvgDraw() && e.getInitialGuestWin()>detailStatistic.getInitialAvgGuestWin()){
                        e.setRelationToInstantAvg("初指主位部分看多");
                    }
                    if (e.getInitialHostWin()<detailStatistic.getInitialAvgHostWin() && e.getInitialDraw()==detailStatistic.getInitialAvgDraw() && e.getInitialGuestWin()>detailStatistic.getInitialAvgGuestWin()){
                        e.setRelationToInstantAvg("初指主位部分看多");
                    }
                    if (e.getInitialHostWin()<detailStatistic.getInitialAvgHostWin() && e.getInitialDraw()>detailStatistic.getInitialAvgDraw() && e.getInitialGuestWin()==detailStatistic.getInitialAvgGuestWin()){
                        e.setRelationToInstantAvg("初指主位部分看多");
                    }
                    if (e.getInitialHostWin()>detailStatistic.getInitialAvgHostWin() && e.getInitialDraw()<detailStatistic.getInitialAvgDraw() && e.getInitialGuestWin()<detailStatistic.getInitialAvgGuestWin()){
                        e.setRelationToInstantAvg("初指和客看多");
                    }
                    if (e.getInitialHostWin()>detailStatistic.getInitialAvgHostWin() && e.getInitialDraw()>detailStatistic.getInitialAvgDraw() && e.getInitialGuestWin()<detailStatistic.getInitialAvgGuestWin()){
                        e.setRelationToInstantAvg("初指和客看多");
                    }
                }
            }

            e.setDetailUrl(url);
            reExcelEntity.setInitialDraw(e.getInitialDraw());
            reExcelEntity.setLeagueTime(e.getLeagueTime());
            reExcelEntity.setLeagueName(e.getLeagueName());
            reExcelEntity.setInitialGuestWin(e.getInitialGuestWin());
            reExcelEntity.setInitialHostWin(e.getInitialHostWin());
            reExcelEntity.setSecondLine(e.getSecondLine());
            reExcelEntity.setSecondLineMin(e.getSecondLineMin());
            reExcelEntity.setThirdLine(e.getThirdLine());
            reExcelEntity.setThirdLineMax(e.getThirdLineMax());
            reExcelEntity.setTrending(e.getTrending());
            reExcelEntity.setAmazing(e.getAmazing());
            reExcelEntity.setDetailUrl(e.getDetailUrl());
            reExcelEntity.setFirstLine(e.getFirstLine());
            reExcelEntity.setGuestTeam(e.getGuestTeam());
            reExcelEntity.setFirstLineMax(e.getFirstLineMax());
            reExcelEntity.setRelationToInitialAvg(e.getRelationToInitialAvg());
            reExcelEntity.setRelationToInstantAvg(e.getRelationToInstantAvg());
            reExcelEntity.setHomeTeam(e.getHomeTeam());
            reExcelEntity.setReturnRate(e.getReturnRate());
            String s = e.getGameResult();
            String[] sArray = s.split(":");
            reExcelEntity.setHostGameResult(sArray[0]);
            reExcelEntity.setDrawGameResult(sArray[1]);
            reExcelEntity.setGuestGameResult(sArray[2]);
            reExcelEntities.add(reExcelEntity);
            log.info("ReExcel单元数据[{}]", reExcelEntity.toString());
        }
        try {
            FileOutputStream fos = new FileOutputStream("D:\\export\\Data\\now.xlsx");
            log.info("开始导出excel");
            EasyExcel.write(fos).head(ReExcelEntity.class).excelType(ExcelTypeEnum.XLSX).sheet("球球").doWrite(reExcelEntities);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("导出用户信息列表Excel 失败");
        }
        log.info("导出结束");
    }
}
