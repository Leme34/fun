package com.lsd.fun.modules.recommend.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 用户-商品打分矩阵结构体(.csv文件)
 */
@Accessors(chain = true)
@Data
public class RatingMatrix implements Serializable {

    private static final long serialVersionUID = -3602558071482652277L;

    private Integer userId;
    private Integer shopId;
    private Integer rating; //评分

    /**
     * 读取一行数据
     * 行数据的格式为: "{userId}","{shopId}","{rating}"
     *
     * @param line csv文件的一行数据
     */
    public static RatingMatrix parseLine(String line) {
        String[] fields = StringUtils.split(
                StringUtils.replace(line, "\"", ""), //去除引号
                ",");
        return new RatingMatrix()
                .setUserId(new Integer(fields[0]))
                .setShopId(new Integer(fields[1]))
                .setRating(new Integer(fields[2]));
    }

}
