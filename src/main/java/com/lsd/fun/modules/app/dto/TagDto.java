package com.lsd.fun.modules.app.dto;

import lombok.Data;

/**
 * 前端页面的标签结构
 */
@Data
public class TagDto {

    private String name;   //ES索引mapping的fieldName
    private String value;  //索引值
    private String type;   //ES的query类型

}
