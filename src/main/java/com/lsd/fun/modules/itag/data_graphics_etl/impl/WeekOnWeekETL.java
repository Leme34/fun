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
 * 本周与上周用户热度数据ETL
 * <p>
 * Created by lsd
 * 2020-03-04 14:20
 */
@Slf4j
@Component
public class WeekOnWeekETL implements ETLTask {

    @Autowired
    private Gson gson;
    @Autowired
    private SparkSession session;
    @Autowired
    private SparkETLUtils sparkETLUtils;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Value("#{funConfig.redis.keyPrefix.etl}")
    private String keyPrefix;


    /**
     * 最近一周（前7天到前14天）注册量ETL
     *
     * @return 每天的注册量
     */
    private List<Reg> registerCount() {
        // 测试数据的日期最新时间是2020-04-08
        LocalDate now = LocalDate.of(2020, Month.APRIL, 8);
        Date nowDate = DateUtils.localDate2Date(now);
        Date lastWeek = DateUtils.addDateDays(nowDate, -7);
        Date twoWeeksAgo = DateUtils.addDateDays(nowDate, -14);
        String sql = String.format(
                "select date_format(created_at,'yyyy-MM-dd') as day," +
                        " count(id) as regCount" +
                        " from fun.member" +
                        " where created_at >='%s'" +
                        " and created_at < '%s'" +
                        " group by date_format(created_at,'yyyy-MM-dd')",
                DateUtils.format(twoWeeksAgo, DateUtils.DATE_TIME_PATTERN),
                DateUtils.format(lastWeek, DateUtils.DATE_TIME_PATTERN)
        );
        return sparkETLUtils.execAndCollectAsList(session, sql, Reg.class);
    }

    /**
     * 最近一周（前7天到前14天）订单量ETL
     *
     * @return 每天的订单量
     */
    private List<Order> orderCount() {
        // 测试数据的日期最新时间是2020-04-08
        LocalDate now = LocalDate.of(2020, Month.APRIL, 8);
        Date nowDate = DateUtils.localDate2Date(now);
        Date lastWeek = DateUtils.addDateDays(nowDate, -7);
        Date twoWeeksAgo = DateUtils.addDateDays(nowDate, -14);
        String sql = String.format(
                "select date_format(created_at,'yyyy-MM-dd') as day," +
                        " count(id) as orderCount" +
                        " from fun.t_order where created_at >='%s'" +
                        " and created_at < '%s' " +
                        " group by date_format(created_at,'yyyy-MM-dd')",
                DateUtils.format(twoWeeksAgo, DateUtils.DATE_TIME_PATTERN),
                DateUtils.format(lastWeek, DateUtils.DATE_TIME_PATTERN)
        );
        return sparkETLUtils.execAndCollectAsList(session, sql, Order.class);
    }

    @Override
    public ETLTaskResult cache() {
        ETLTaskResult result = new ETLTaskResult().setTaskName("本周与上周用户热度数据ETL");
        try {
            List<Order> orderCount = this.orderCount();
            List<Reg> registerCount = this.registerCount();
            redisTemplate.opsForValue().set(keyPrefix + ":orderCount", gson.toJson(orderCount));
            redisTemplate.opsForValue().set(keyPrefix + ":registerCount", gson.toJson(registerCount));
        } catch (Exception e) {
            log.error("本周与上周用户热度数据ETL出错", e);
            return result.setSuccess(false);
        }
        return result.setSuccess(true);
    }

    public Map<String, Object> query() {
        Map<String, Object> resultMap = Maps.newHashMapWithExpectedSize(2);
        String orderCount = redisTemplate.opsForValue().get(keyPrefix + ":orderCount");
        String registerCount = redisTemplate.opsForValue().get(keyPrefix + ":registerCount");
        resultMap.put("orderCount", gson.fromJson(orderCount, new TypeToken<List<Order>>() {
        }.getType()));
        resultMap.put("registerCount", gson.fromJson(registerCount, new TypeToken<List<Reg>>() {
        }.getType()));
        return resultMap;
    }

    /**
     * 周注册量结构体
     */
    @Data
    private static class Reg {
        private String day;         //日期
        private Integer regCount;   //当天注册量
    }


    /**
     * 周订单量结构体
     */
    @Data
    private static class Order {
        private String day;         //日期
        private Integer orderCount; //当天订单量
    }

}
