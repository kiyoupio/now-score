package com.example.nowscore.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class ExcelEntity {
    /**
     * 联赛名称
     */
    @ExcelProperty(value = "联赛名称", index = 0)
    private String leagueName;
    /**
     * 联赛时间
     */
    @ExcelProperty(value = "联赛时间", index = 1)
    private Date leagueTime;
    /**
     * 主队名
     */
    @ExcelProperty(value = "主队名", index = 2)
    private String homeTeam;
    /**
     * 客队名
     */
    @ExcelProperty(value = "客队名", index = 3)
    private String guestTeam;
    /**
     * 初指-主胜
     */
    @ExcelProperty(value = "主赔率", index = 4)
    private Double initialHostWin;
    /**
     * 初指-和局
     */
    @ExcelProperty(value = "和赔率", index = 5)
    private Double initialDraw;
    /**
     * 初指-客胜
     */
    @ExcelProperty(value = "客赔率", index = 6)
    private Double initialGuestWin;
    /**
     * 总体变化趋势
     * 总计12种
     */
    @ExcelProperty(value = "总体变化趋势", index = 7)
    private String trending;
    /**
     * 详情页链接
     */
    @ExcelProperty(value = "链接", index = 8)
    private String detailUrl;
    /**
     * 赛果 win:0:0,0:win:0,0:0:win
     * 根据score判断
     */
    @ExcelProperty(value = "赛果 主:和:客", index = 9)
    private String gameResult;
    /**
     * 第一排特征
     */
    @ExcelProperty(value = "第一排特征", index = 10)
    private String firstLine;
    /**
     * 第一排最高值
     */
    @ExcelProperty(value = "第一排最高值", index = 11)
    private double firstLineMax;
    /**
     * 第二排特征
     */
    @ExcelProperty(value = "第二排特征", index = 12)
    private String secondLine;
    /**
     * 第二排最低值
     */
    @ExcelProperty(value = "第二排最低值", index = 13)
    private double secondLineMin;
    /**
     * 第三排特征
     */
    @ExcelProperty(value = "第三排特征", index = 14)
    private String thirdLine;
    /**
     * 第三排最高值
     */
    @ExcelProperty(value = "第三排最高值", index = 15)
    private double thirdLineMax;
    /**
     * 365与初指平均值关系
     */
    @ExcelProperty(value = "365与初指平均值关系", index = 16)
    private String relationToInitialAvg;
    /**
     * 365与即时平均值关系
     */
    @ExcelProperty(value = "365与即时平均值关系", index = 17)
    private String relationToInstantAvg;
    /**
     * 返还率
     */
    @ExcelProperty(value = "返还率", index = 18)
    private String returnRate;
    /**
     * 主流、交易所极端情况
     */
    @ExcelProperty(value = "主流、交易所极端情况", index = 19)
    private String amazing;
}
