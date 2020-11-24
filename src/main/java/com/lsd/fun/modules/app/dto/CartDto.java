package com.lsd.fun.modules.app.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by lsd
 * 2019-12-12 16:51
 */
@ApiModel("订单详情")
@Accessors(chain = true)
@Data
public class CartDto {

    @NotNull(message = "商品id不能为空")
    @ApiModelProperty("商品id")
    private Long goodsId;   //此系统为了简化直接使用商铺当作商品，人均价格当作商品价格

    @NotNull(message = "购买数量不能为空")
    @ApiModelProperty("购买数量，增加传正数，减少传负数")
    private Integer amount;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "购物车预览图片地址", hidden = true)
    private String imageUrl;
    @ApiModelProperty(value = "加入购物车时的价格", hidden = true)
    private BigDecimal price;

}
