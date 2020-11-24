package com.lsd.fun.modules.itag.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 异步执行结果
 *
 * Created by lsd
 * 2020-04-14 11:31
 */
@Accessors(chain = true)
@Data
public class ETLTaskResult {

    private boolean success;
    private String taskName;

}
