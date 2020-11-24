package com.lsd.fun.common.validator.group;

import javax.validation.GroupSequence;

/**
 * 自定义校验顺序的校验 Group
 * 其中 @GroupSequence 提供的组序列顺序执行 以及 短路能力
 * <p>
 * Created by lsd
 * 2019-11-21 15:31
 */
//如果AddGroup组失败，则UpdateGroup组不会再校验
@GroupSequence({AddGroup.class, UpdateGroup.class})
public interface Group {

}
