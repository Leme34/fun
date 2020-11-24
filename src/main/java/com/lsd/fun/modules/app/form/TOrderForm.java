package com.lsd.fun.modules.app.form;

import com.lsd.fun.modules.cms.entity.TOrderCommodityEntity;
import com.lsd.fun.modules.cms.entity.TOrderEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by lsd
 * 2020-04-08 12:32
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class TOrderForm extends TOrderEntity {

    @ApiModelProperty("订单商品详情列表")
    private List<TOrderCommodityEntity> orderDetails;

}
