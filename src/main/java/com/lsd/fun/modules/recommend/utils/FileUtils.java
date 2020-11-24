package com.lsd.fun.modules.recommend.utils;

import com.lsd.fun.modules.recommend.dto.RatingMatrix;
import org.apache.commons.lang3.RandomUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by lsd
 * 2020-03-22 17:44
 */
public class FileUtils {

    /**
     * 模拟生成用户数据文件：
     * 1. 基于已有用户行为所得的用户-商铺打分矩阵(behavior.csv)
     * 格式："{userId}","{shopId}","{rating}"
     * 约定： 1. 0≤rating≤5(无用户行为记录则为0)  2. 换行符为"\n"
     * 2. 用户特征矩阵(featurevalue.csv)
     * 3. 特征处理后的用户特征矩阵(feature.csv)
     * 特征文件是对 featurevalue.csv 去除了userId和shopId两个没有意义的列，特征处理后得到的
     * ABCD列是年龄的bucket编码，EF列是性别，G列是评分的max-min标准化，HIJK列是人均价格的bucket编码，L列是用户是否点击
     */
    public static void geneTestCSV(HttpServletResponse response) {
        List<RatingMatrix> behaviorDataList = new ArrayList<>();
        // 500个用户，每个用户随机浏览400个商铺
        Set<Integer> userIdSet = new LinkedHashSet<>();
        while (userIdSet.size() < 500) {
            int userId = RandomUtils.nextInt(1, 501);
            if (userIdSet.contains(userId)) {  //已被计算过,随机换一个用户
                continue;
            }
            //该用户未被计算过,则标记为已访问并开始计算
            userIdSet.add(userId);
            Set<Integer> shopIdByUserSet = new HashSet<>();
            while (shopIdByUserSet.size() < 400) {
                int shopId = RandomUtils.nextInt(1, 5001);
                if (shopIdByUserSet.contains(shopId)) { //已被访问过,随机换一个商铺
                    continue;
                }
                //该商铺未被该用户访问过,则标记为已访问并加入数据集
                shopIdByUserSet.add(shopId);
                // 生成数据行
                int rating = RandomUtils.nextInt(0, 6);
                behaviorDataList.add(new RatingMatrix().setUserId(userId).setShopId(shopId).setRating(rating));
            }
        }
        String behaviorContent = FileUtils.toTxtFileContent(behaviorDataList);
        try {
            FileUtils.saveFile(behaviorContent, "C:/Users/Administrator/Desktop/behavior.csv");
            // TODO 待实现
//            FileUtils.saveFile(featurevalueContent, "C:/Users/Administrator/Desktop/featurevalue.csv");
//            FileUtils.saveFile(featureContent, "C:/Users/Administrator/Desktop/feature.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 结果转为文本，把 List<RatingMatrix> -> String
     */
    public static String toTxtFileContent(List<RatingMatrix> ratingMatrixList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ratingMatrixList.size(); i++) {
            RatingMatrix matrix = ratingMatrixList.get(i);
            sb.append("\"")
                    .append(matrix.getUserId())
                    .append("\",\"")
                    .append(matrix.getShopId())
                    .append("\",\"")
                    .append(matrix.getRating())
                    .append("\"");
            if (i != ratingMatrixList.size() - 1) {
                sb.append("\r\n");
            }
        }
        return sb.toString();
    }


    /**
     * 保存文件
     *
     * @param content  文件内容
     * @param filePath 文件绝对路径
     */
    public static void saveFile(String content, String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fileWriter);
        bw.write(content);
        bw.close();
    }
}
