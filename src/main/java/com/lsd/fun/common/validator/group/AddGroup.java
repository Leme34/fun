package com.lsd.fun.common.validator.group;

import javax.validation.groups.Default;

/**
 * 新增数据校验 Group,继承默认的校验组 Default
 * 以防止没有声明 groups = {}的那些校验注解不去校验
 * <p>
 * Created by lsd
 * 2019-11-21 15:31
 */
public interface AddGroup extends Default {
}
