package com.lsd.fun.modules.recommend.controller;

import com.google.common.collect.Maps;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.cms.entity.MemberEntity;
import com.lsd.fun.modules.cms.entity.TOrderEntity;
import com.lsd.fun.modules.cms.service.MemberService;
import com.lsd.fun.modules.cms.service.TOrderService;
import com.lsd.fun.modules.recommend.dto.RatingMatrix;
import com.lsd.fun.modules.recommend.dto.RatingMatrixParseLineFunction;
import com.lsd.fun.modules.recommend.dto.RecommendationsForeachPartitionFunction;
import com.lsd.fun.modules.recommend.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 个性化召回算法之ALS算法
 * 基于已有用户行为所得的用户-商铺打分矩阵，根据 用户特征矩阵 和 商铺特征矩阵 预测计算空缺的值
 * <p>
 * Created by lsd
 * 2020-03-21 15:50
 */
@Slf4j
@Api(tags = "ALS召回算法模型")
@RequestMapping("/als")
@RestController
public class AlsRecallController {

    @Autowired
    private SparkSession spark;
    @Autowired
    private MemberService memberService;
    @Autowired
    private TOrderService tOrderService;
    private final static String ALS_MODEL_PATH = "/fun/alsModel";
//    private final static String ALS_MODEL_PATH = "D:/毕业设计/fun/training_model/alsModel";
    private final static String BEHAVIOR_CSV_PATH = "/fun/behavior.csv";
//    private final static String BEHAVIOR_CSV_PATH = "D:/毕业设计/fun/training_model/behavior.csv";


    /**
     * ALS召回算法模型训练
     * 1.过拟合:训练结果过分趋近于真实数据,一旦真实数据出现误差则预测结果就不尽人意。
     * 解决办法:增大数据规模;减小Rank(减少特征维度,更加松散);增大正则化系数
     * 2. 欠拟合:训练结果与真实数据偏差过大,没有很好的与真实数据收敛
     * 解决办法:增加Rank;减小正则化系数(缩小正则距离(偏差值));
     */
    @ApiOperation("ALS召回算法模型训练")
    @PostMapping("/alsRecallTrain")
    public R alsRecallTrain(@RequestBody List<Integer> userIds) {
        this.geneBehaviorCSV(userIds);
        Dataset<Row> dataSet = loadBehaviorDataAsDataFrame();
        // 将所有数据分出80%用于训练，20%用于测试
        Dataset<Row>[] dataSets = dataSet.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainingData = dataSets[0];
        Dataset<Row> testingData = dataSets[1];
        // ALS模型定义
        ALS als = new ALS()
                .setMaxIter(10)    //最大迭代次数,为了保证性能避免模型过分拟合此处设置10
                .setRank(5)        //矩阵的特征(feature)数量
                .setRegParam(0.01) //正则化系数,用于防止过拟合情况
                .setUserCol("userId") //矩阵结构字段
                .setItemCol("shopId")
                .setRatingCol("rating");
        try {
            // 模型训练并把结果保存在文件中
            ALSModel alsModel = als.fit(trainingData);
            alsModel.save("file:///"+ALS_MODEL_PATH);
            // 使用模型对测试数据预测"rating"值，并保存在内存表的"prediction"字段中
            Dataset<Row> predictions = alsModel.transform(testingData);
            // 计算回归模型的评估指标，用于模型参数调优
            // 求均方根误差(rmse)，({预测值与真实值的差值}的平方÷{观测次数})再开平方，因此均方根误差越小越理想
            RegressionEvaluator evaluator = new RegressionEvaluator()
                    .setMetricName("rmse")            //评测指标名称
                    .setLabelCol("rating")            //真实值所在列
                    .setPredictionCol("prediction");  //预测值所在列
            double rmse = evaluator.evaluate(predictions);
            System.out.println("rmse=" + rmse);
        } catch (Exception e) {
            log.error("ALS模型训练失败", e);
            return R.error("ALS模型训练失败");
        }
        return R.ok();
    }


    /**
     * 使用训练好的 ALS召回算法模型 进行召回预测
     */
    @ApiOperation("召回预测")
    @GetMapping("/alsRecallTrain")
    public R alsRecallPredict() {
        // 加载训练好的模型
        ALSModel alsModel = ALSModel.load("file:///"+ALS_MODEL_PATH);
        // 加载数据文件
        Dataset<Row> dataFrame = loadBehaviorDataAsDataFrame();
        // 给所有用户做离线召回预测
        Dataset<Row> users = dataFrame.select(alsModel.getUserCol()).distinct();
        // 以下API在spark-mllib依赖2.2.0以上才有
        Dataset<Row> recommendations = alsModel.recommendForAllUsers(10);//给训练数据中所有的user推荐numItems个商铺

        // 若使用foreach()是召回商铺持久化时每个都要开一个数据库连接,因此切分到每个分片批量持久化效率更高
        recommendations.foreachPartition(new RecommendationsForeachPartitionFunction());
        return R.ok();
    }


    /**
     * 生成最新用户行为文件
     *
     * @param userIds
     */
    private void geneBehaviorCSV(List<Integer> userIds) {
        List<MemberEntity> users = memberService.lambdaQuery()
                .in(CollectionUtils.isNotEmpty(userIds), MemberEntity::getId, userIds)
                .list();
        if (CollectionUtils.isEmpty(users)) {
            throw new RRException("没有找到对应用户");
        }
        List<RatingMatrix> behaviorDataList = new ArrayList<>();
        // 每个用户购买过的店铺
        for (MemberEntity user : users) {
            Integer userId = user.getId();
            List<Integer> boughtShopIds = tOrderService.listBoughtShopByUserId(userId);
            for (Integer shopId : boughtShopIds) {
                behaviorDataList.add(new RatingMatrix().setUserId(userId).setShopId(shopId).setRating(1));
            }
        }
        String behaviorContent = FileUtils.toTxtFileContent(behaviorDataList);
        try {
            FileUtils.saveFile(behaviorContent, BEHAVIOR_CSV_PATH);
            // TODO 待实现
//            FileUtils.saveFile(featurevalueContent, "C:/Users/Administrator/Desktop/featurevalue.csv");
//            FileUtils.saveFile(featureContent, "C:/Users/Administrator/Desktop/feature.csv");
        } catch (Exception e) {
            log.error("生成behavior.csv失败", e);
            throw new RRException("生成behavior.csv失败");
        }
    }

    /**
     * 加载基于已有用户行为所得的用户-商铺打分矩阵.csv文件
     *
     * @return spark内存表
     */
    private Dataset<Row> loadBehaviorDataAsDataFrame() {
        JavaRDD<String> csvFileRDD = spark.read().textFile("file:///"+BEHAVIOR_CSV_PATH).toJavaRDD();
        // csv数据行 转为 RatingMatrix矩阵结构
        JavaRDD<RatingMatrix> ratingMatrixRDD = csvFileRDD.map(new RatingMatrixParseLineFunction());
        // RatingMatrix结构内存表
        return spark.createDataFrame(ratingMatrixRDD, RatingMatrix.class);
    }


//    public static void main(String[] args) {
//        // 必须设置此环境变量且必须是配置好winutils.exe的hadoop，否则NPE
//        System.setProperty("hadoop.home.dir", "D:/解决winutils.exe问题的Hadoop/hadoop-2.6.0/");
//        spark = SparkSession.builder().master("local").appName("dianping").getOrCreate();
////        alsRecallTrain();
//        alsRecallPredict();
//    }

}
