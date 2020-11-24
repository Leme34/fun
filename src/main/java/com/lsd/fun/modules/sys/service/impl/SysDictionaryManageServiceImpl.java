package com.lsd.fun.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.reflect.TypeToken;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.validator.ValidatorUtils;
import com.lsd.fun.config.FunConfig;
import com.lsd.fun.modules.sys.dao.SysDictionaryManageDao;
import com.lsd.fun.modules.sys.entity.SysDictionaryManageEntity;
import com.lsd.fun.modules.sys.query.TDictionaryQuery;
import com.lsd.fun.modules.sys.service.SysDictionaryManageService;
import com.lsd.fun.modules.sys.service.cache.CacheTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SysDictionaryManageServiceImpl extends ServiceImpl<SysDictionaryManageDao, SysDictionaryManageEntity> implements SysDictionaryManageService {

    private final StringRedisTemplate redisTemplate;
    private final FunConfig config;
    private final CacheTemplateService cacheTemplateService;

    public SysDictionaryManageServiceImpl(StringRedisTemplate redisTemplate, FunConfig config, CacheTemplateService cacheTemplateService) {
        this.redisTemplate = redisTemplate;
        this.config = config;
        this.cacheTemplateService = cacheTemplateService;
    }

    @Override
    public PageUtils queryPage(TDictionaryQuery query) {
        ValidatorUtils.validateEntity(query);
        IPage<SysDictionaryManageEntity> page = this.page(
                new Query<SysDictionaryManageEntity>().getPage(query),
                new QueryWrapper<SysDictionaryManageEntity>()
                        .lambda()
                        .eq(SysDictionaryManageEntity::getDid, query.getDataDictionaryId())
                        .like(StringUtils.isNotBlank(query.getKeyword()), SysDictionaryManageEntity::getName, query.getKeyword())
                        .isNull(SysDictionaryManageEntity::getDeletedAt)   //未被逻辑删除
        );
        return new PageUtils(page);
    }

    private void update(SysDictionaryManageEntity sysDictionaryManage) {
        //Cache Aside Pattern
        redisTemplate.delete(config.getRedis().getKeyPrefix().getDictionary() + ":" + sysDictionaryManage.getId());
        this.updateById(sysDictionaryManage);
    }

    @Override
    public SysDictionaryManageEntity queryById(Integer id) {
        final String cacheKey = config.getRedis().getKeyPrefix().getDictionary() + ":" + id;
        // 若数据库也查不到则返回空串的字典对象
        SysDictionaryManageEntity sysDictionaryManage = Optional.ofNullable(
                cacheTemplateService.queryByParams(cacheKey, id,
                        new TypeToken<SysDictionaryManageEntity>() {
                        }, params -> this.getById(id)
                )
        ).orElse(new SysDictionaryManageEntity().setId(id).setName(""));
        return sysDictionaryManage;
    }

    @Transactional
    @Override
    public void remove(Integer[] ids) {
        final List<String> keys = Arrays.stream(ids).map(id -> config.getRedis().getKeyPrefix().getDictionary() + ":" + id).collect(Collectors.toList());
        redisTemplate.delete(keys);
        // 逻辑删除
        this.lambdaUpdate()
                .set(SysDictionaryManageEntity::getDeletedAt, LocalDateTime.now())
                .in(SysDictionaryManageEntity::getId, Arrays.asList(ids))
                .update();
    }

    @Override
    public void saveOrUpdateWithCheck(SysDictionaryManageEntity sysDictionaryManage) {
        final boolean isUpdate = sysDictionaryManage.getId() != null;
        // 根据did和name查询数据库
        final Optional<SysDictionaryManageEntity> oneOpt = this.lambdaQuery()
                .eq(SysDictionaryManageEntity::getDid, sysDictionaryManage.getDid())
                .eq(SysDictionaryManageEntity::getName, sysDictionaryManage.getName())
                .oneOpt();
        if (oneOpt.isPresent()) {  //数据库存在
            final SysDictionaryManageEntity entity = oneOpt.get();
            if (isUpdate) {
                if (!entity.getId().equals(sysDictionaryManage.getId())) {
                    throw new RRException("此字典目录下已存在同名字典项", HttpStatus.HTTP_BAD_REQUEST);
                }
            } else {
                if (entity.getDeletedAt() == null) {  //未被逻辑删除，则返回已存在
                    throw new RRException("此字典目录下已存在同名字典项", HttpStatus.HTTP_BAD_REQUEST);
                }
            }
            // 被逻辑删除过的也走更新逻辑（从而实现保持原字典项id不变，旧的历史数据不会被影响）
            this.update(sysDictionaryManage.setId(entity.getId()).setDeletedAt(null));
        } else {  //数据库不存在
            if (isUpdate) {
                this.update(sysDictionaryManage.setId(sysDictionaryManage.getId()));
            } else {
                this.save(sysDictionaryManage);
            }
        }
    }

}
