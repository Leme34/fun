package com.lsd.fun.modules.app.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 针对单个会员的标签信息
 */
@Data
public class MemberTag implements Serializable {
    private static final long serialVersionUID = 1453737779006704311L;

    // i_member.t_member
    private String memberId;
    private String phone;
    private String sex;
    private String channel;
    private String subOpenId;           //关注后的微信openId
    private String address;
    private String regTime;             //注册时间

    // i_order.t_order
    private Long orderCount;            //总订单数
    private String orderTime;           //最后下单时间 max(create_time)
    private Double orderMoney;          //订单消费总金额
    private List<String> favGoods;      //商品喜好（所有曾经购买过的商品id列表）

    // i_marketing
    private String freeCouponTime;      //领取"首单免费"优惠券的时间
    private List<String> couponTimes;   //所有曾经购领取优惠券的时间列表
    private Double chargeMoney;         //充值总金额

    private Integer overTime;           //最大送餐超时
    private Integer feedBack;           //最后一条反馈的反馈类型

}
