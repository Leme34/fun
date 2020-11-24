package com.lsd.fun.modules.app.vo;

import com.lsd.fun.modules.cms.vo.ShopVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * Created by lsd
 * 2020-04-06 13:19
 */
@Accessors(chain = true)
@Data
public class ShopSearchResult {

    private long total;

    @ApiModelProperty("商铺列表")
    private List<ShopVO> shopList;

    @ApiModelProperty("聚合结果")
    private List<Map<String, Object>> tagsAggs;

}
