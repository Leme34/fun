package com.lsd.fun.modules.sys.service.cache;

/**
 * 需要使用者自己实现的数据库查询接口
 *
 * Created by lsd
 * 2019-12-23 15:37
 */
public interface CacheLoader<T> {

    /**
     * 根据参数去数据库查询
     * @param params 参数对象
     * @return
     */
    T queryByParams(Object params);

}
