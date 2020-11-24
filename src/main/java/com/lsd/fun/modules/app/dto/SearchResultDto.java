package com.lsd.fun.modules.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by lsd
 * 2020-04-04 17:07
 */
@AllArgsConstructor
@Data
public class SearchResultDto {

    private long total;

    // Map<ID, 位置信息>
    private Map<Integer, LocationDto> resultMap;

}
