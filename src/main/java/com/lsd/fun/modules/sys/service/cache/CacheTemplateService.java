package com.lsd.fun.modules.sys.service.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 基于模板方法设计的缓存模板
 * <p>
 * Created by lsd
 * 2019-12-23 15:22
 */
@Service
public class CacheTemplateService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private Gson gson;

    /**
     * 缓存有则返回，无则查询数据库
     *
     * @param key         缓存key
     * @param params      查询数据库的参数
     * @param clazz       Json序列化的对象类型
     * @param cacheLoader 用户自己实现的数据库查询方法
     * @param <T>
     * @return
     */
    public <T> T queryByParams(String key, Object params, TypeToken<T> clazz, CacheLoader<T> cacheLoader) {
        String dicJsonStr = redisTemplate.opsForValue().get(key);
        // 若缓存不存在则查询数据库
        if (StringUtils.isBlank(dicJsonStr) || StringUtils.equalsIgnoreCase(dicJsonStr, "null")) {
            synchronized (this) {
                dicJsonStr = redisTemplate.opsForValue().get(key);
                // DCL
                if (StringUtils.isBlank(dicJsonStr) || StringUtils.equalsIgnoreCase(dicJsonStr, "null")) {
                    final T result = cacheLoader.queryByParams(params);
                    redisTemplate.opsForValue().set(key, gson.toJson(result));
                    return result;
                }
            }
        }
        return gson.fromJson(dicJsonStr, clazz.getType());
    }

}
