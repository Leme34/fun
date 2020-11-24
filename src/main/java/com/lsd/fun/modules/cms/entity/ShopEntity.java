package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

/**
 * 店铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-26 01:29:43
 */
@Accessors(chain = true)
@Data
@TableName("shop")
public class ShopEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Integer id;
    /**
     * 商铺标题
     */
    private String title;
    /**
     * 商铺介绍
     */
    private String description;
    /**
     * 商铺评分
     */
    @DecimalMax("5")
    @DecimalMin("0")
    private BigDecimal remarkScore;
    /**
     * 人均消费
     */
    private Integer pricePerMan;
    /**
     * 省份/直辖市
     */
    private String province;
    /**
     * 市级单位
     */
    private String city;
    /**
     * 区级单位
     */
    private String region;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 商铺类别id
     */
    private Integer categoryId;
    /**
     * 以" "分隔的标签
     */
    private String tags;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    /**
     * 商家id
     */
    private Integer sellerId;
    /**
     * 封面
     */
    private Integer coverFileId;
    /**
     * 是否禁用
     */
    private Integer disabledFlag;

}
