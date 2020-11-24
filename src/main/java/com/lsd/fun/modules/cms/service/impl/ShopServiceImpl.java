package com.lsd.fun.modules.cms.service.impl;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.modules.cms.query.ShopQuery;
import com.lsd.fun.modules.cms.dao.ShopDao;
import com.lsd.fun.modules.cms.dto.ShopExcelDTO;
import com.lsd.fun.modules.cms.vo.ShopVO;
import com.lsd.fun.modules.cms.entity.CategoryEntity;
import com.lsd.fun.modules.cms.entity.SellerEntity;
import com.lsd.fun.modules.cms.entity.ShopEntity;
import com.lsd.fun.modules.cms.service.CategoryService;
import com.lsd.fun.modules.cms.service.SellerService;
import com.lsd.fun.modules.cms.service.ShopService;
import com.lsd.fun.modules.cos.config.QiNiuProperties;
import com.lsd.fun.modules.cos.service.TFileService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchExecutorException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopDao, ShopEntity> implements ShopService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SellerService sellerService;
    @Value("#{funConfig.qiniu}")
    private QiNiuProperties qiNiuProperties;
    @Autowired
    private TFileService tFileService;

    @Override
    public PageUtils queryPage(ShopQuery query) {
        IPage page = new Query<>().getPage(query);
        Wrapper wrapper = new QueryWrapper<>()
                .like(StringUtils.isNotBlank(query.getKeyword()), "shop.title", query.getKeyword());
        IPage<ShopVO> voPage = this.baseMapper.queryPage(page, wrapper);
        addCoverUrlPrefix(voPage.getRecords());
        return new PageUtils(voPage);
    }

    @Transactional
    @Override
    public void saveFromExcelParsedResult(List<ShopExcelDTO> parsedResult, Integer isUpdate) {
        // 查出所有列表数据map，用于把关联字段的name转换为id
        Map<String, Integer> category2IdMap = categoryService.list().stream().collect(Collectors.toMap(CategoryEntity::getName, CategoryEntity::getId, (oldKey, newKey) -> newKey));
        Map<String, Integer> seller2IdMap = sellerService.lambdaQuery().eq(SellerEntity::getDisabledFlag, 0).list().stream().collect(Collectors.toMap(SellerEntity::getName, SellerEntity::getId, (oldKey, newKey) -> newKey));
        // 并行校验，并转换成实体对象
        List<ShopEntity> shopList = parsedResult.parallelStream().map(r -> {
            // 校验必填数据
            if (isUpdate == 1) {
                if (r.getId() == null) {
                    throw new RRException("第" + r.getRow() + "行数据错误，ID不能为空");
                }
                if (StringUtils.isBlank(r.getTitle()) || StringUtils.isBlank(r.getAddress())) {
                    throw new RRException("第" + r.getRow() + "行数据错误，标题/地址不能为空");
                }
            }
            ShopEntity shop = new ShopEntity();
            BeanUtils.copyProperties(r, shop);
            try {
                return shop.setRemarkScore(new BigDecimal(r.getRemarkScore()))
                        .setCategoryId(r.getCategory() == null ? null : category2IdMap.get(r.getCategory()))
                        .setSellerId(r.getSeller() == null ? null : seller2IdMap.get(r.getSeller()));
            } catch (Exception e) {
                log.error("第" + r.getRow() + "行数据导入出错，请稍候再试", e);
                throw new RRException("第" + r.getRow() + "行数据导入出错，请稍候再试");
            }
        }).collect(Collectors.toList());
        try {
            if (isUpdate == 1) {
                this.updateBatchById(shopList, shopList.size());
            } else {
                this.saveBatch(shopList, shopList.size());
            }
        } catch (PersistenceException e) {
            // 对批量操作异常信息友好处理
            if (e.getCause() instanceof BatchExecutorException) {
                BatchUpdateException bue = ((BatchExecutorException) e.getCause()).getBatchUpdateException();
                // 收集出错的行数
                int[] updateCounts = bue.getUpdateCounts();
                final ArrayList<Object> errorRows = Lists.newArrayList();
                for (int i = 0; i < updateCounts.length; i++) {
                    if (updateCounts[i] < 0) {
                        errorRows.add(i + 1); // +1转为自然行数
                    }
                }
                throw new RRException("第" + errorRows + "行数据ID重复");
            }
        }
    }

    @Override
    public List<ShopVO> queryList(Wrapper wrapper) {
        List<ShopVO> vos = this.baseMapper.queryPage(wrapper);
        addCoverUrlPrefix(vos);
        return vos;
    }


    @Override
    public ShopVO queryById(Integer id) {
        Wrapper wrapper = Wrappers.query()
                .eq("shop.id", id);
        List<ShopVO> list = this.baseMapper.queryPage(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            throw new RRException("商铺不存在", HttpStatus.HTTP_BAD_REQUEST);
        }
        addCoverUrlPrefix(list);
        return list.get(0);
    }

    @Transactional
    @Override
    public void update(ShopEntity shop) {
        this.lambdaQuery()
                .select(ShopEntity::getCoverFileId)
                .eq(ShopEntity::getId, shop.getId())
                .oneOpt()
                .ifPresent(o -> {
                    Integer oldFileId = o.getCoverFileId();
                    tFileService.deleteById(oldFileId);
                });
        this.updateById(shop);
    }

    @Transactional
    @Override
    public void removeLogic(List<Integer> idList) {
        this.lambdaQuery()
                .select(ShopEntity::getCoverFileId)
                .in(ShopEntity::getId, idList)
                .list()
                .stream()
                .map(ShopEntity::getCoverFileId)
                .filter(Objects::nonNull)
                .forEach(fid -> tFileService.deleteById(fid));
        this.removeByIds(idList);
    }

    @Override
    public List<ShopVO> listOrderByField(Collection<Integer> keySet) {
        List<ShopVO> shopVOS = this.baseMapper.listOrderByField(keySet);
        addCoverUrlPrefix(shopVOS);
        return shopVOS;
    }

    /**
     * 非爬取得到的图片需要添加七牛域名
     */
    private void addCoverUrlPrefix(List<ShopVO> vos) {
        for (ShopVO vo : vos) {
            if (vo.getCoverFileId() != null && vo.getIsCrawl() == 0) {
                vo.setCoverUrl(qiNiuProperties.getHostPrefix() + vo.getCoverUrl());
            }
        }
    }

}
