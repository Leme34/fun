package com.lsd.fun.modules.itag.data_graphics_etl.impl;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lsd.fun.common.utils.DateUtils;
import com.lsd.fun.modules.itag.SparkETLUtils;
import com.lsd.fun.modules.itag.data_graphics_etl.ETLTask;
import com.lsd.fun.modules.itag.dto.ETLTaskResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户抵扣券到期前 1 天未使用提醒功能
 * 约定系统的优惠券是 8 天失效的
 * <p>
 * Created by lsd
 * 2020-03-04 17:28
 */
@Slf4j
@Component
public class RemindETL implements ETLTask {

    @Autowired
    private SparkSession session;
    @Autowired
    private SparkETLUtils sparkETLUtils;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Value("#{funConfig.redis.keyPrefix.etl}")
    private String keyPrefix;
    @Autowired
    private Gson gson;


    /**
     * 最近一周"首单免费优惠券"的领券情况
     *
     * @return 前n天(n < = 7)当天领取"首单免费优惠券"的人数
     */
    private List<FreeReminder> freeReminderList() {
        // 测试数据的日期最新时间是2020-04-08
        LocalDate now = LocalDate.of(2020, Month.APRIL, 8);
        Date nowDate = DateUtils.localDate2Date(now);
        // 约定的系统优惠券是 8 天失效的，明天失效的券领券日期是7天前
        Date pickDate = DateUtils.addDateDays(nowDate, -7);
        String sql = String.format(
                "select date_format(created_at,'yyyy-MM-dd') as day,count(member_id) as freeCount" +
                        " from fun.coupon_member" +
                        " where coupon_id = 1" +       //coupon_id = 1的是系统预先设定好的首单优惠券
                        " and coupon_channel = 1" +    //领券渠道为公司发放
                        " and created_at >= '%s'" +   //领取日期是7天前
                        " group by date_format(created_at,'yyyy-MM-dd')",
                DateUtils.format(pickDate, DateUtils.DATE_TIME_PATTERN)
        );
        return sparkETLUtils.execAndCollectAsList(session, sql, FreeReminder.class);
    }

    /**
     * 最近一周"普通优惠券"的领券情况
     *
     * @return 前n天(n < = 7)当天领取"非首单免费优惠券"的人数
     */
    private List<CouponReminder> couponReminders() {
        // 测试数据的日期最新时间是2020-04-08
        LocalDate now = LocalDate.of(2020, Month.APRIL, 8);
        Date nowDate = DateUtils.localDate2Date(now);
        // 约定的系统优惠券是 8 天失效的，明天失效的券领券日期是7天前
        Date pickDate = DateUtils.addDateDays(nowDate, -7);
        String sql = String.format(
                "select date_format(created_at,'yyyy-MM-dd') as day,count(member_id) as couponCount" +
                        " from fun.coupon_member" +
                        " where coupon_id != 1" +       //除系统预先设定好的"首单优惠券"外的优惠券
                        " and created_at >= '%s'" +    //领取日期是7天前
                        " group by date_format(created_at,'yyyy-MM-dd')",
                DateUtils.format(pickDate, DateUtils.DATE_TIME_PATTERN)
        );
        return sparkETLUtils.execAndCollectAsList(session, sql, CouponReminder.class);
    }

    @Override
    public ETLTaskResult cache() {
        ETLTaskResult result = new ETLTaskResult().setTaskName("用户抵扣券领券情况ETL");
        try {
            List<CouponReminder> couponReminders = this.couponReminders();
            List<FreeReminder> freeReminders = this.freeReminderList();
            redisTemplate.opsForValue().set(keyPrefix + ":couponReminders", gson.toJson(couponReminders));
            redisTemplate.opsForValue().set(keyPrefix + ":freeReminders", gson.toJson(freeReminders));
        } catch (Exception e) {
            log.error("用户抵扣券领券情况ETL出错", e);
            return result.setSuccess(false);
        }
        return result.setSuccess(true);
    }

    public Map<String, Object> query() {
        Map<String, Object> resultMap = Maps.newHashMapWithExpectedSize(2);
        String couponReminders = redisTemplate.opsForValue().get(keyPrefix + ":couponReminders");
        String freeReminders = redisTemplate.opsForValue().get(keyPrefix + ":freeReminders");
        resultMap.put("couponReminders", gson.fromJson(couponReminders, new TypeToken<List<CouponReminder>>() {
        }.getType()));
        resultMap.put("freeReminders", gson.fromJson(freeReminders, new TypeToken<List<FreeReminder>>() {
        }.getType()));
        return resultMap;
    }


    /**
     * 最近一周"首单免费优惠券"的领券情况
     */
    @Data
    private static class FreeReminder {
        private String day;             //日期
        private Integer freeCount;      //当天领取"首单免费优惠券"的用户数量
    }

    /**
     * 最近一周"普通优惠券"的领券情况
     */
    @Data
    private static class CouponReminder {
        private String day;             //日期
        private Integer couponCount;    //当天领取"优惠券"的用户数量
    }

}
