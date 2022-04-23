package com.example.nowscore.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Detail {
    /**
     * itemId
     */
    private int itemId;
    /**
     * 详情页链接
     */
    private String detailUrl;
    /**
     * 公司英文名
     */
    private String companyNameEn;
    /**
     * 公司中文名
     */
    private String companyNameZh;
    /**
     * 初指-主胜
     */
    private Double initialHostWin;
    /**
     * 初指-和局
     */
    private Double initialDraw;
    /**
     * 初指-客胜
     */
    private Double initialGuestWin;
    /**
     * 初指-返还率
     */
    private Double initialReturnRate;
    /**
     * 即时-主胜
     */
    private Double instantHostWin;
    /**
     * 即时-和局
     */
    private Double instantDraw;
    /**
     * 即时-客胜
     */
    private Double instantGuestWin;
    /**
     * 即时-返还率
     */
    private Double instantReturnRate;
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
    /**
     * 是否主流公司
     */
    private String isMainCompany;
    /**
     * 是否交易所
     */
    private String isExchange;
}
