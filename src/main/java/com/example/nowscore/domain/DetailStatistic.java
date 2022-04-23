package com.example.nowscore.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 小表实体类
 * @author kuangyoupeng1
 * @date 2022.4.1
 */
@Getter
@Setter
public class DetailStatistic implements Serializable {

    /**
     * 详情页链接
     */
    private String detailUrl;

    /**
     * 初指-主胜
     */
    private double initialMaxHostWin;

    /**
     * 初指-和局
     */
    private double initialMaxDraw;

    /**
     * 初指-客胜
     */
    private double initialMaxGuestWin;

    /**
     * 即时-主胜
     */
    private double instantMaxHostWin;

    /**
     * 即时-和局
     */
    private double instantMaxDraw;

    /**
     * 即时-客胜
     */
    private double instantMaxGuestWin;

    /**
     * 初指-主胜
     */
    private double initialMinHostWin;

    /**
     * 初指-和局
     */
    private double initialMinDraw;

    /**
     * 初指-客胜
     */
    private double initialMinGuestWin;

    /**
     * 即时-主胜
     */
    private double instantMinHostWin;

    /**
     * 即时-和局
     */
    private double instantMinDraw;

    /**
     * 即时-客胜
     */
    private double instantMinGuestWin;

    /**
     * 初指平均值-主胜
     */
    private double initialAvgHostWin;

    /**
     * 初指平均值-和局
     */
    private double initialAvgDraw;

    /**
     * 初指平均值-客胜
     */
    private double initialAvgGuestWin;

    /**
     * 即时平均值-主胜
     */
    private double instantAvgHostWin;

    /**
     * 即时平均值-和局
     */
    private double instantAvgDraw;

    /**
     * 即时平均值-客胜
     */
    private double instantAvgGuestWin;

    /**
     * 初指-最大返还率
     */
    private double initialMaxReturnRate;

    /**
     * 即时-最大返还率
     */
    private double instantMaxReturnRate;

    /**
     * 初指-最小返还率
     */
    private double initialMinReturnRate;

    /**
     * 即时-最小返回率
     */
    private double instantMinReturnRate;

    /**
     * 初指平均值-返回率
     */
    private double initialAvgReturnRate;

    /**
     * 即时平均值-返回率
     */
    private double instantAvgReturnRate;

    /**
     * 最大凯利指数-低
     */
    private double maxKaliLow;

    /**
     * 最小凯利指数-低
     */
    private double minKaliLow;

    /**
     * 平均凯利指数-低
     */
    private double avgKaliLow;

    /**
     * 最大凯利指数-中
     */
    private double maxKaliMid;

    /**
     * 最小凯利指数-中
     */
    private double minKaliMid;

    /**
     * 平均凯利指数-中
     */
    private double avgKaliMid;

    /**
     * 最大凯利指数-高
     */
    private double maxKaliHigh;

    /**
     * 最小凯利指数-高
     */
    private double minKaliHigh;

    /**
     * 平均凯利指数-高
     */
    private double avgKaliHigh;
}
