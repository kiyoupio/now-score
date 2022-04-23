package com.example.nowscore.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Trending {
    /**
     * 主键
     */
    private int id;
    /**
     * 详情页主键
     */
    private Integer detailItemId;
    /**
     * 主胜
     */
    private Double hostWin;
    /**
     * 和局
     */
    private Double draw;
    /**
     * 客胜
     */
    private Double guestWin;
    /**
     * 变化时间
     */
    private Date changeTime;
    /**
     * 凯利指数-低
     */
    private Double kaliLow;
    /**
     * 凯利指数-中
     */
    private Double kaliMid;
    /**
     * 凯利指数-高
     */
    private Double kaliHigh;
}
