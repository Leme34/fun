package com.lsd.fun.modules.cms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.validator.ValidatorUtils;
import com.lsd.fun.common.validator.group.AddGroup;
import com.lsd.fun.modules.app.dto.CartDto;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import com.lsd.fun.modules.app.form.TOrderForm;
import com.lsd.fun.modules.app.service.CartService;
import com.lsd.fun.modules.cms.entity.TOrderCommodityEntity;
import com.lsd.fun.modules.cms.service.TOrderCommodityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.TOrderDao;
import com.lsd.fun.modules.cms.entity.TOrderEntity;
import com.lsd.fun.modules.cms.service.TOrderService;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderDao, TOrderEntity> implements TOrderService {

    @Autowired
    private CartService cartService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private Gson gson;
    @Autowired
    private TOrderCommodityService tOrderCommodityService;

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<TOrderEntity> page = this.page(
                new Query<TOrderEntity>().getPage(query),
                new QueryWrapper<TOrderEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void creatOrder(UserRoleDto userRoleDto, TOrderForm form) {
        ValidatorUtils.validateEntity(form, AddGroup.class);
        final Long orderId = IdWorker.getId();
        final Long userId = userRoleDto.getUserId().longValue();
        final String cartKey = cartService.getUserKey(userId);
        List<String> boughtGoods = Lists.newArrayListWithCapacity(form.getOrderDetails().size());
        BoundHashOperations<String, String, Object> cartObjMap = redisTemplate.boundHashOps(cartKey);
        // 遍历全部商品详情，根据用户选中购物车的商品进行下单
        final List<String> goodsKeys = form.getOrderDetails()
                .stream()
                .map(od -> {
                    final String goodKey = od.getShopId().toString();
                    if (!Optional.ofNullable(cartObjMap.hasKey(goodKey)).orElse(false)) {
                        throw new RRException("购物车不存在该商品:" + od.getShopName(), HttpStatus.SC_BAD_REQUEST);
                    }
                    boughtGoods.add(goodKey);
                    return goodKey;
                })
                .collect(Collectors.toList());
        final List<TOrderCommodityEntity> orderDetails = Lists.newArrayListWithCapacity(goodsKeys.size());
        // 计算购物车中选中商品的总金额
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        if (!Optional.ofNullable(redisTemplate.hasKey(cartKey)).orElse(false)) {
            throw new RRException("购物车为空，请先添加商品", HttpStatus.SC_BAD_REQUEST);
        }
        final List<Object> cartDTOJsonStrs = cartObjMap.multiGet(goodsKeys);
        if (CollectionUtils.isEmpty(cartDTOJsonStrs)) {
            throw new RRException("购物车为空，请先添加商品", HttpStatus.SC_BAD_REQUEST);
        }
        for (Object jsonStr : cartDTOJsonStrs) {
            if (jsonStr == null) {
                throw new RRException("购物车异常", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            final CartDto dto = gson.fromJson(jsonStr.toString(), CartDto.class);
            totalPrice = totalPrice.add(dto.getPrice().multiply(BigDecimal.valueOf(dto.getAmount())));
            TOrderCommodityEntity od = new TOrderCommodityEntity()
                    .setOrderId(orderId)
                    .setShopName(dto.getName())
                    .setShopId(dto.getGoodsId().intValue())
                    .setNum(dto.getAmount())
                    .setPrice(dto.getPrice());
            orderDetails.add(od);
        }
        // 全部商品详情入库
        tOrderCommodityService.saveBatch(orderDetails);
        // 订单入库
        final TOrderEntity tOrder = new TOrderEntity();
        BeanUtils.copyProperties(form, tOrder);
        tOrder.setId(orderId)
                .setMemberId(userRoleDto.getUserId())
                .setPayPrice(totalPrice)
                .setOriginPrice(totalPrice)  //简化了优惠券逻辑
                .setStatus(2);  //简化为直接订单已完成
        this.save(tOrder);
        // 删除购物车中的已结算商品
        cartObjMap.delete(boughtGoods.toArray());
        // TODO 通知管理后台有新订单
    }

    @Override
    public List<Integer> listBoughtShopByUserId(Integer userId) {
        return this.baseMapper.listBoughtShopByUserId(userId);
    }

}
