package com.lsd.fun.modules.app.query;

import com.lsd.fun.common.utils.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;

/**
 * 地图页面视野范围内房源列表查询参数
 * Created by lsd
 * 2020-04-04 13:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MapSearchQuery extends BaseQuery {

    @NotBlank(message = "暂未支持该城市")
    @ApiModelProperty("当前城市名称")
    private String city;

    @ApiModelProperty("地图缩放级别")
    private int level = 12;

    @ApiModelProperty("用户当前经度")
    private double lng;
    @ApiModelProperty("用户当前纬度")
    private double lat;

    @ApiModelProperty("地图左上角经度，用于确定地图显区域")
    private Double leftLongitude;
    @ApiModelProperty("地图左上角纬度，用于确定地图显区域")
    private Double leftLatitude;
    @ApiModelProperty("地图右下角经度，用于确定地图显区域")
    private Double rightLongitude;
    @ApiModelProperty("地图右下角纬度，用于确定地图显区域")
    private Double rightLatitude;

}
