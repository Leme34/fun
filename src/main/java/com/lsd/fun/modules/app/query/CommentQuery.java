package com.lsd.fun.modules.app.query;

import com.lsd.fun.common.utils.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by lsd
 * 2020-04-15 19:45
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentQuery extends BaseQuery {

    private Integer userId;
    private Integer shopId;

}
