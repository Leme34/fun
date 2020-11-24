package com.lsd.fun.common.utils;

import com.google.common.collect.Sets;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * 排序生成器
 */
public class ShopSortUtil {
    public static final String DEFAULT_SORT_KEY = "id";


    private static final Set<String> SORT_KEYS = Sets.newHashSet(
            DEFAULT_SORT_KEY,
            "price_per_man",
            "area"
    );

    public static Sort getSort(String key, String directionKey) {
        String sortKey = getSortKey(key);
        Sort.Direction direction = Sort.Direction.fromOptionalString(directionKey)
                .orElse(Sort.Direction.DESC);
        return Sort.by(direction, sortKey);
    }

    public static String getSortKey(String key) {
        if (!SORT_KEYS.contains(key)) {
            key = DEFAULT_SORT_KEY;
        }
        return key;
    }

}
