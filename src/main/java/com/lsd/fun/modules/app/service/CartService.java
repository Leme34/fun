package com.lsd.fun.modules.app.service;

import cn.hutool.json.JSONArray;
import com.google.gson.Gson;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.validator.ValidatorUtils;
import com.lsd.fun.config.FunConfig;
import com.lsd.fun.modules.app.dto.CartDto;
import com.lsd.fun.modules.cms.entity.ShopEntity;
import com.lsd.fun.modules.cms.service.ShopService;
import com.lsd.fun.modules.cms.vo.ShopVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lsd
 * 2019-12-12 16:10
 */
@Slf4j
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private FunConfig funConfig;
    @Autowired
    private Gson gson;
    @Autowired
    private ShopService shopService;


    public List<CartDto> list(Long userId) {
        String key = getUserKey(userId);
        // 购物车不存在
        if (!redisTemplate.hasKey(key)) {
            return Collections.emptyList();
        }
        // 查询购物车
        final BoundHashOperations<String, Object, Object> cartObjMap = redisTemplate.boundHashOps(key);
        // 取出购物车中的所有商品
        final List<Object> cartDtoJsonStrs = cartObjMap.values();
        if (CollectionUtils.isEmpty(cartDtoJsonStrs)) {
            return Collections.emptyList();
        }
        return correctPrice(cartDtoJsonStrs);
    }

    /**
     * 校正最新食物/食谱类别金额
     */
    private List<CartDto> correctPrice(List<Object> cartDtoJsonStrs) {
        Map<Long, CartDto> goodsId2CartDtoMap = new HashMap<>();
        // 先查询订单中所有商品的最新信息
        final List<CartDto> cartDtos = cartDtoJsonStrs.stream()
                .map(jsonStr -> {
                    final CartDto dto = gson.fromJson(jsonStr.toString(), CartDto.class);
                    goodsId2CartDtoMap.put(dto.getGoodsId(), dto);
                    return dto;
                }).collect(Collectors.toList());
        // 批量校正最新食物金额
        if (!goodsId2CartDtoMap.isEmpty()) {
            shopService.lambdaQuery()
                    .select(ShopEntity::getId, ShopEntity::getPricePerMan)
                    .in(ShopEntity::getId, goodsId2CartDtoMap.keySet())
                    .list()
                    .forEach(shop ->
                            goodsId2CartDtoMap.get(shop.getId().longValue()).setPrice(BigDecimal.valueOf(shop.getPricePerMan()))
                    );
        }
        return cartDtos;
    }


    @Transactional
    public void save(CartDto dto, Long userId) {
        ValidatorUtils.validateEntity(dto);
        final Long goodsId = dto.getGoodsId();
        final Integer addNum = dto.getAmount();
        String imagePath;
        String name;
        BigDecimal price;
        // 若是点食物，需要减库存
        // 1 查商品信息
        ShopVO shop = Optional.ofNullable(shopService.queryById(goodsId.intValue()))
                .orElseThrow(() -> new RRException("要加入购物车的商品：" + dto.getName() + "已下架或不存在", HttpStatus.SC_BAD_REQUEST));
        // 2 减库存
//        final var affectedRow = tFoodStockService.decreaseStock(goodsId, addNum);
//        if (affectedRow < 1) {
//            throw new RRException(tFood.getName() + "库存不足", HttpStatus.SC_BAD_REQUEST);
//        }
        imagePath = shop.getCoverUrl();
        price = BigDecimal.valueOf(shop.getPricePerMan());
        name = shop.getTitle();
        CartDto cartDto;
        // 加入购物车
        final String goodsKey = goodsId.toString();
        final BoundHashOperations<String, Object, Object> cartObjMap = redisTemplate.boundHashOps(getUserKey(userId));
        if (cartObjMap.hasKey(goodsKey)) {  // 若购物车中存在此商品则直接修改商品数量
            String cartJsonStr = cartObjMap.get(goodsKey).toString();
            cartDto = gson.fromJson(cartJsonStr, CartDto.class);
            final int num = cartDto.getAmount() + addNum;
            if (num <= 0) {
                this.deleteByGoodsId(userId, goodsId.toString());
                return;
            }
            cartDto.setAmount(num)
                    .setName(name)
                    .setImageUrl(imagePath)
                    .setPrice(price);
        } else {
            if (addNum <= 0) {
                return;
            }
            cartDto = new CartDto()
                    .setGoodsId(goodsId)
                    .setAmount(addNum)
                    .setName(name)
                    .setImageUrl(imagePath)
                    .setPrice(price);
        }
        cartObjMap.put(goodsKey, gson.toJson(cartDto));
    }


    /**
     * 返回购物车key
     */
    public String getUserKey(Long userId) {
        return funConfig.getRedis().getKeyPrefix().getCart() + ":" + userId;
    }


    /**
     * 删除购物车的商品
     */
    @Transactional
    public void deleteByGoodsId(Long userId, String... goodsIds) {
        // 查询购物车
        final BoundHashOperations<String, Object, Object> cartObjMap = redisTemplate.boundHashOps(getUserKey(userId));
        cartObjMap.delete(goodsIds);
        // 需要释放库存
    }

    /**
     * 删除整个购物车
     */
    public void deleteByUserId(Long userId) {
        redisTemplate.delete(getUserKey(userId));
    }

}
