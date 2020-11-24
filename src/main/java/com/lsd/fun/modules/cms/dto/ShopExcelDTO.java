package com.lsd.fun.modules.cms.dto;

import lombok.Data;

/**
 * 读取Excel时，封装读取的每一行的数据
 * Created by lsd
 * 2019-08-12 16:49
 */
@Data
public class ShopExcelDTO {
    // ID
    private Integer id;
    // 标题
    private String title;
    // 商铺介绍
    private String description;
    // 商铺评分
    private String remarkScore;
    // 省份/直辖市
    private String province = "广东省";
    // 市级单位
    private String city;
    // 区级单位
    private String region;
    // 详细地址
    private String address;
    // 商铺类别名称
    private String category;
    // 商铺以空格分隔的标签
    private String tags;
    // 商家名称
    private String seller;
    // 商家名称
    private Integer pricePerMan;

    // Excel所在行数，用于校验出错提示用户
    private Integer row;
}
