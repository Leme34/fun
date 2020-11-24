
package com.lsd.fun.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsd.fun.common.xss.SQLFilter;
import org.apache.commons.lang.StringUtils;

import java.util.Optional;

/**
 * 查询参数
 * <p>
 * Created by lsd
 * 2019-08-07 16:18
 */
public class Query<T> {

    /**
     * 不使用默认排序规则
     *
     * @param query
     * @return
     */
    public IPage<T> getPage(BaseQuery query) {
        return this.getPage(query, null, false);
    }

    public IPage<T> getPage(BaseQuery query, String defaultOrderField, boolean isAsc) {
        //分页参数
        final Integer curPage = Optional.ofNullable(query.getPage()).orElse(1);
        final Integer limit = Optional.ofNullable(query.getLimit()).orElse(10);
        //创建分页对象
        Page<T> page = new Page<>(curPage, limit);
        //排序字段参数
        //防止SQL注入（因为orderField、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String orderField = SQLFilter.sqlInject(query.getOrder_field());
        String order = query.getOrder();
        //前端字段指定的排序规则
        if (StringUtils.isNotBlank(orderField) && StringUtils.isNotBlank(order)) {
            if (Constant.ASC.equalsIgnoreCase(order)) {
                return page.addOrder(OrderItem.asc(orderField));
            } else {
                return page.addOrder(OrderItem.desc(orderField));
            }
        }
        if (StringUtils.isBlank(defaultOrderField)) {
            return page;
        }
        //使用默认排序规则
        if (isAsc) {
            page.addOrder(OrderItem.asc(defaultOrderField));
        } else {
            page.addOrder(OrderItem.desc(defaultOrderField));
        }
        return page;
    }
}
