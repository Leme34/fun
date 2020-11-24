//package com.lsd.fun.modules.recommend;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.function.Function;
//import org.apache.spark.ml.classification.LogisticRegression;
//import org.apache.spark.ml.classification.LogisticRegressionModel;
//import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
//import org.apache.spark.ml.linalg.Vector;
//import org.apache.spark.ml.linalg.VectorUDT;
//import org.apache.spark.ml.linalg.Vectors;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.RowFactory;
//import org.apache.spark.sql.SparkSession;
//import org.apache.spark.sql.types.DataTypes;
//import org.apache.spark.sql.types.Metadata;
//import org.apache.spark.sql.types.StructField;
//import org.apache.spark.sql.types.StructType;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 个性化排序算法之LR算法
// * <p>
// * Created by lsd
// * 2020-03-22 16:51
// */
//@RequestMapping("lr")
//@RestController
//public class LRController {
//
//    //@Autowired
//    private static SparkSession spark;
//
//    /**
//     * LR排序算法模型训练
//     */
//    //@GetMapping("/alsRecallTrain")
//    public static String LRTrain() {
//        Dataset<Row> dataset = loadFeatureDataAsDataFrame();
//        // 将所有数据分出80%用于训练，20%用于测试
//        Dataset<Row>[] dataSets = dataset.randomSplit(new double[]{0.8, 0.2});
//        Dataset<Row> trainingData = dataSets[0];
//        Dataset<Row> testingData = dataSets[1];
//        // 逻辑回归模型定义
//        LogisticRegression lr = new LogisticRegression()
//                .setMaxIter(10)       //最大迭代次数,为了保证性能避免模型过分拟合此处设置10
//                .setRegParam(0.3)     //正则化系数,用于防止过拟合情况
//                .setElasticNetParam(0.8)           //正则化范式比(默认0.0)，正则化一般有两种范式：L1(Lasso)和L2(Ridge)。L1一般用于特征的稀疏化，L2一般用于防止过拟合。这里的参数即设置L1范式的占比，默认0.0即只使用L2范式
//                .setFamily("multinomial");         //设置为多分类问题，防止过拟合
//        try {
//            // 模型训练并把结果保存在文件中
//            LogisticRegressionModel lrModel = lr.fit(trainingData);
//            lrModel.save("file:///C:/Users/Administrator/Desktop/lrModel");
//            // 使用模型对测试数据预测"rating"值，并保存在内存表的"prediction"字段中
//            Dataset<Row> predictions = lrModel.transform(testingData);
//            // 计算回归模型的评估指标，用于模型参数调优
//            MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
//                    .setMetricName("accuracy")        //评测指标名称
//                    .setLabelCol("label")             //真实值所在列在列
//                    .setPredictionCol("prediction");  //预测值所在列
//            double accuracy = evaluator.evaluate(predictions);
//            System.out.println("accuracy=" + accuracy);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "error";
//        }
//        return "success";
//    }
//
//
//    /**
//     * 加载特征文件
//     * 特征文件是对 featurevalue.csv 去除了userId和shopId两个没有意义的列，特征处理后得到的
//     * ABCD列是年龄的bucket编码，EF列是性别，G列是评分的max-min标准化，HIJK列是人均价格的bucket编码，L列是需要预估的用户是否点击标志
//     *
//     * @return spark内存表
//     */
//    private static Dataset<Row> loadFeatureDataAsDataFrame() {
//        JavaRDD<String> csvFileRDD = spark.read().textFile("file:///E:/SpringBoot项目实战/ElasticSearch+Spark 构建高匹配度搜索服务+千人千面推荐系统/dianpingjava-master/dianpingjava/第五和第六章-基础服务之品类，门店能力建设加搜索推荐/src/main/resources/training_data/feature.csv").toJavaRDD();
//        // csv数据行 转为 特征矩阵结构
//        JavaRDD<Row> rowRDD = csvFileRDD.map(new Function<String, Row>() {
//            @Override
//            public Row call(String str) throws Exception {
//                String[] strArr = StringUtils.split(str.replace("\"", ""), ",");
//                // 11维的特征向量
//                Vector vector = Vectors.dense(Double.parseDouble(strArr[0]), Double.parseDouble(strArr[1]),
//                        Double.parseDouble(strArr[2]), Double.parseDouble(strArr[3]), Double.parseDouble(strArr[4]), Double.parseDouble(strArr[5]),
//                        Double.parseDouble(strArr[6]), Double.parseDouble(strArr[7]), Double.parseDouble(strArr[8]), Double.parseDouble(strArr[9]), Double.parseDouble(strArr[10]));
//                return RowFactory.create(new Double(strArr[11]), vector); //用户是否点击标志,特征向量
//            }
//        });
//        // Row内存表结构
//        StructType structType = new StructType(
//                new StructField[]{
//                        new StructField("label", DataTypes.DoubleType, false, Metadata.empty()), //用户是否点击标志
//                        new StructField("features", new VectorUDT(), false, Metadata.empty())    //特征处理（压缩为0~1的double型数值）后的特征向量
//                }
//        );
//        return spark.createDataFrame(rowRDD, structType);
//    }
//
//
//    public static void main(String[] args) {
//        // 必须设置此环境变量且必须是配置好winutils.exe的hadoop，否则NPE
//        System.setProperty("hadoop.home.dir", "D:/解决winutils.exe问题的Hadoop/hadoop-2.6.0/");
//        spark = SparkSession.builder().master("local").appName("dianping").getOrCreate();
//        LRTrain();
//    }
//
//}
