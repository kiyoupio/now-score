package com.example.nowscore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Meta {
    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 详情页链接
     */
    private String detailUrl;
    /**
     * 联赛名称
     */
    private String leagueName;
    /**
     * 联赛时间
     */
    private Date leagueTime;
    /**
     * 主队名
     */
    private String homeTeam;
    /**
     * 客队名
     */
    private String guestTeam;
    /**
     * 比分
     */
    private String score;
    /**
     * 公司数量
     */
    private Integer companyNum;
    /**
     * 主胜上
     */
    private Double hostWin1;
    /**
     * 主胜下
     */
    private Double hostWin2;
    /**
     * 和局上
     */
    private Double draw1;
    /**
     * 和局下
     */
    private Double draw2;
    /**
     * 客胜上
     */
    private Double guestWin1;
    /**
     * 客胜下
     */
    private Double guestWin2;
}
