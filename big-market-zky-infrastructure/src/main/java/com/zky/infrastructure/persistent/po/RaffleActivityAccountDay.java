package com.zky.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: ZhangKaiYuan
 * @description: 抽奖活动账户表-日次数
 * @create: 2025/3/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleActivityAccountDay {

    private final static SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");


    /** 自增ID */
    private Long id;
    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;
    /** 日期（yyyy-mm-dd） */
    private String day;
    /** 日次数 */
    private Integer dayCount;
    /** 日次数-剩余 */
    private Integer dayCountSurplus;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

    public static String currentDay() {
        return dateFormatDay.format(new Date());
    }
}
