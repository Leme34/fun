package com.lsd.fun.modules.itag.data_graphics_etl.impl;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.lsd.fun.modules.itag.SparkETLUtils;
import com.lsd.fun.modules.itag.data_graphics_etl.ETLTask;
import com.lsd.fun.modules.itag.dto.ETLTaskResult;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spark操作Hive实现全部用户热度数据的ETL
 * <p>
 * Created by lsd
 * 2020-03-03 21:26
 */
@Slf4j
@Component
public class UserHeatETL implements ETLTask {

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
     * 按性别分组统计
     * -1:未知 1:男 2:女
     */
    private List<MemberSex> memberSex() {
        String sql = "select sex as memberSex,count(id) as sexCount from fun.member group by sex";
        return sparkETLUtils.execAndCollectAsList(session, sql, MemberSex.class);
    }

    /**
     * 按会员注册渠道分组统计
     * 1:IOS 2:android 3:微信小程序 4:微信公众号 5:h5
     */
    private List<MemberChannel> memberRegChannel() {
        String sql = "select member_channel as memberChannel, count(id) as channelCount from fun.member group by member_channel";
        return sparkETLUtils.execAndCollectAsList(session, sql, MemberChannel.class);
    }

    /**
     * 按 是否关注了微信公众号（mp_open_id是否为空） 分组统计
     * 注意默认情况下Sqoop导入的null会变为"null"
     */
    private List<MemberMpSub> memberMpSubscribe() {
        //已关注微信公众号
        //未关注微信公众号
        String sql = "select count(if(mp_open_id !='null',id,null)) as subCount,count(if(mp_open_id ='null',id,null)) as unSubCount from fun.member";
        return sparkETLUtils.execAndCollectAsList(session, sql, MemberMpSub.class);
    }

    /**
     * 用户热度信息聚合查询
     * <p>
     * reg:        已注册的用户，目标表：i_member.t_member，条件：phone = 'null'
     * complete:   已完善信息的用户，目标表：i_member.t_member，条件：phone != 'null'
     * order:      已下过单的用户，目标表：i_order.t_order，条件：t.orderCount = 1
     * orderAgain: 有回购记录的用户，目标表：i_order.t_order，条件：t.orderCount >= 2
     * coupon:     有领券记录的用户，目标表：i_marketing.t_coupon_member，条件：count(distinct member_id)
     */
    private MemberHeat memberHeat() {
        Dataset<Row> reg_complete = session.sql(
                "select count(if(phone='null',id,null)) as reg,count(if(phone !='null',id,null)) as complete from fun.member"
        );
        Dataset<Row> order_again = session.sql(
                "select count(if(t.orderCount = 1,t.member_id,null)) as order," +
                        "count(if(t.orderCount >= 2,t.member_id,null)) as orderAgain from" +
                        " (select count(id) as orderCount,member_id from fun.t_order group by member_id) as t"
        );
        Dataset<Row> coupon = session.sql(
                "select count(distinct member_id) as coupon from fun.coupon_member"
        );

        // 生产环境慎用笛卡尔积，效率非常低下，此处用于测试偷懒
        Dataset<Row> resultDataset = coupon.crossJoin(reg_complete).crossJoin(order_again);
        List<MemberHeat> resultJsons = resultDataset.toJSON().collectAsList()
                .stream()
                .map(str -> gson.fromJson(str, MemberHeat.class))
                .collect(Collectors.toList());
        return resultJsons.get(0);
    }


    @Override
    public ETLTaskResult cache() {
        ETLTaskResult result = new ETLTaskResult().setTaskName("用户热度数据ETL");
        try {
            List<MemberSex> memberSexes = this.memberSex();
            List<MemberChannel> memberChannels = this.memberRegChannel();
            List<MemberMpSub> memberMpSubs = this.memberMpSubscribe();
            MemberHeat memberHeat = this.memberHeat();
            // store cache
            redisTemplate.opsForValue().set(keyPrefix + ":memberSexs", gson.toJson(memberSexes));
            redisTemplate.opsForValue().set(keyPrefix + ":memberChannels", gson.toJson(memberChannels));
            redisTemplate.opsForValue().set(keyPrefix + ":memberMpSubs", gson.toJson(memberMpSubs));
            redisTemplate.opsForValue().set(keyPrefix + ":memberHeat", gson.toJson(memberHeat));
        } catch (Exception e) {
            log.error("用户热度数据ETL出错", e);
            return result.setSuccess(false);
        }
        return result.setSuccess(true);
    }

    @SuppressWarnings("all")
    public Map<String, Object> query() {
        Map<String, Object> userHeatETLResultMap = Maps.newHashMapWithExpectedSize(4);
        String memberSexs = redisTemplate.opsForValue().get(keyPrefix + ":memberSexs");
        String memberChannels = redisTemplate.opsForValue().get(keyPrefix + ":memberChannels");
        String memberMpSubs = redisTemplate.opsForValue().get(keyPrefix + ":memberMpSubs");
        String memberHeat = redisTemplate.opsForValue().get(keyPrefix + ":memberHeat");
        userHeatETLResultMap.put("memberSexs", gson.fromJson(memberSexs, new TypeToken<List<MemberSex>>() {
        }.getType()));
        userHeatETLResultMap.put("memberChannels", gson.fromJson(memberChannels, new TypeToken<List<MemberChannel>>() {
        }.getType()));
        userHeatETLResultMap.put("memberMpSubs", gson.fromJson(memberMpSubs, new TypeToken<List<MemberMpSub>>() {
        }.getType()));
        userHeatETLResultMap.put("memberHeat", gson.fromJson(memberHeat, MemberHeat.class));
        return userHeatETLResultMap;
    }

    @Data
    private static class MemberSex {
        private Integer memberSex;
        private Integer sexCount;
    }

    @Data
    private static class MemberChannel {
        private Integer memberChannel;
        private Integer channelCount;
    }

    @Data
    private static class MemberMpSub {
        private Integer subCount;
        private Integer unSubCount;
    }


    /**
     * 用户热度聚合信息
     */
    @Data
    private static class MemberHeat {
        private Integer reg;        //已注册的用户数量
        private Integer complete;   //已完善信息的用户数量
        private Integer order;      //已下过单的用户数量
        private Integer orderAgain; //有回购记录的用户数量
        private Integer coupon;     //有领券记录的用户数量
    }

    /**
     * 用户ETL结果Vo
     */
    @Accessors(chain = true)
    @Data
    private static class MemberVo {
        private List<MemberSex> memberSexes;
        private List<MemberChannel> memberChannels;
        private List<MemberMpSub> memberMpSubs;
        private MemberHeat memberHeat;
    }

}
