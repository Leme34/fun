package com.lsd.fun.modules.recommend.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 用户特征矩阵(featurevalue.csv)
 */
@Accessors(chain = true)
@Data
public class FeatureValueMatrix implements Serializable {
    private static final long serialVersionUID = 8911161411416189758L;

    // 用户特征
    private Integer userId;
    private Integer age;
    private String gender; //M or F

    // 商铺特征
    private Integer shopId;
    private Double remarkScore; // 评分,0≤remarkScore≤5
    private Integer pricePerMan; //人均价格

    private Integer isClick; //用户是否点击

    /**
     * 读取一行数据
     * 行数据的格式为: "{userId}","{shopId}","{rating}"
     *
     * @param line csv文件的一行数据
     */
    public static FeatureValueMatrix parseLine(String line) {
        String[] fields = StringUtils.split(
                StringUtils.replace(line, "\"", ""), //去除引号
                ",");
        return new FeatureValueMatrix()
                .setUserId(new Integer(fields[0]))
                .setAge(new Integer(fields[1]))
                .setGender(fields[2])
                .setShopId(new Integer(fields[3]))
                .setRemarkScore(Double.valueOf(fields[4]))
                .setPricePerMan(new Integer(fields[5]))
                .setIsClick(new Integer(fields[6]));
    }

}
