package com.lsd.fun.modules.recommend.dto;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lsd
 * 2020-04-14 23:13
 */
public class RecommendationsForeachPartitionFunction implements ForeachPartitionFunction<Row>, Serializable {
    public static final long serialVersionUID = 1069997744909372812L;

    @Override
    public void call(Iterator<Row> rowIterator) throws Exception {
        // 此分片是分布式环境的，因此不能直接使用应用中Bean的数据库连接，而是要对此分片开一个数据库连接
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:4406/fun?useUnicode=true&characterEncoding=UTF-8",
                "root", "123456");
        PreparedStatement pstmt = connection.prepareStatement(
                "insert into recommend(member_id,shop_ids)values (?,?)"
        );
        // 遍历此分片分配到的数据Row
        List<Map<String, Object>> rowDataMaps = new ArrayList<>();
        rowIterator.forEachRemaining(action -> {
            int userId = action.getInt(0);
            // 已经按照打分排序了的列表
            List<GenericRowWithSchema> recommendations = action.getList(1);
            List<Integer> shopIds = recommendations.stream()
                    .map(row -> {
                        int shopId = row.getInt(0); //商铺id
                        float score = row.getFloat(1);  //打分
                        return shopId;
                    }).collect(Collectors.toList());
            String shopIdsStr = StringUtils.join(shopIds, ",");
            Map<String, Object> userId2ShopIdMap = Maps.newHashMapWithExpectedSize(1);
            userId2ShopIdMap.put("userId", userId);
            userId2ShopIdMap.put("shopIdsStr", shopIdsStr);
            rowDataMaps.add(userId2ShopIdMap);
        });
        for (Map<String, Object> rowDataMap : rowDataMaps) {
            pstmt.setInt(1, (Integer) rowDataMap.get("userId"));
            pstmt.setString(2, (String) rowDataMap.get("shopIdsStr"));
            pstmt.addBatch();  //拼装到批量sql中
        }
        pstmt.executeBatch();  //执行批量sql
        connection.close();
    }
}
