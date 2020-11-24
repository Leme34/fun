package com.lsd.fun.modules.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 索引中存储的自动补全关键词对象
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShopSuggest {
    private String input;
    private int weight = 10; // 默认权重

    public ShopSuggest(String input){
        this.input = input;
    }
}
