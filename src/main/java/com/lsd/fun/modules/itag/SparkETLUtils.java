package com.lsd.fun.modules.itag;

import com.google.gson.Gson;
import com.lsd.fun.modules.recommend.dto.RatingMatrixParseLineFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lsd
 * 2020-03-04 16:43
 */
@Component
public class SparkETLUtils {

    @Autowired
    private Gson gson;
    @Value("${spark.master:}")
    private String master;

    /**
     * 配置操作 Hive 和 ES 的SparkSession，一个应用中只能有一个SparkSession
     * 构造SparkSession时会自动读取resource下的hive-site.xml
     */
    @Bean
    public SparkSession initSparkSession4ES() {
        // 没有配置master，注入本地模式的SpaKSession
        if (StringUtils.isBlank(master)){
            return SparkSession.builder().master("local").appName("spark-fun").getOrCreate();
        }
        // Setting Master for running it
        SparkConf sparkConf = new SparkConf()
                .setAppName("spark-fun")
                .setMaster("spark://spark-master:7077") //提交到Spark执行
//                .setJars(new String[]{"hdfs://namenode:8020/user/spark/dev-jars/fun-1.0-SNAPSHOT.jar"})  //设置分发到集群的jar，非local模式必须配置否则ClassCastException
                .set("es.nodes", "elasticsearch")
                .set("es.port", "9200")
                .set("es.index.auto.create", "true");  // 若索引mapping结构不存在则自动创建
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        // 配置hadoop
        Configuration hadoopConf = sparkContext.hadoopConfiguration();
        // 必须有这个设置，否则No FileSystem for scheme: hdfs
        hadoopConf.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        hadoopConf.set("fs.file.impl", LocalFileSystem.class.getName());
        return SparkSession.builder()
                .config(sparkConf)
                .enableHiveSupport()
                .getOrCreate();
    }



    /**
     * 封装执行ETL SQL
     *
     * @param sql   sparkSQL
     * @param clazz 结果集pojo的元素类型
     * @param <T>   结果集pojo的元素类型
     * @return List<T>
     */
    public <T> List<T> execAndCollectAsList(SparkSession sparkSession, String sql, Class<T> clazz) {
        Dataset<Row> rowDataset = sparkSession.sql(sql);
        List<String> resultJsons = rowDataset.toJSON().collectAsList();
        return resultJsons.stream()
                .map(str -> gson.fromJson(str, clazz))
                .collect(Collectors.toList());
    }

}
