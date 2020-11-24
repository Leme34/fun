package com.lsd.fun.modules.cms.service;

import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.cms.dto.BaiduMapLocation;

/**
 * 百度地图LBS.云服务
 * <p>
 * 介绍：http://lbsyun.baidu.com/index.php?title=lbscloud/guide
 * API文档：http://lbsyun.baidu.com/index.php?title=lbscloud/api
 * <p>
 * Created by lsd
 * 2020-02-14 11:35
 */
public interface BaiduLBSService {

    /**
     * 请求百度API，根据城市以及具体位置获取百度地图的经纬度
     */
    BaiduMapLocation parseAddress2Location(String address);

    /**
     * 上传百度LBS数据
     *
     * @param location 百度经纬度
     * @param title 标题
     * @param address 完整地址
     * @param houseId 房源id
     * @param price 价格
     * @param area 面积
     */
    void upload(BaiduMapLocation location, String title, String address,
                Integer houseId, int price, int area);

    /**
     * 移除百度LBS数据
     */
    void remove(Integer houseId);

}
