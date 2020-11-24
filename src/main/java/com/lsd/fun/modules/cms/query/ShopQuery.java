package com.lsd.fun.modules.cms.query;

import com.lsd.fun.common.utils.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by lsd
 * 2020-03-27 20:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShopQuery extends BaseQuery {

    @ApiModelProperty("标题关键字")
    private String keyword;

}
