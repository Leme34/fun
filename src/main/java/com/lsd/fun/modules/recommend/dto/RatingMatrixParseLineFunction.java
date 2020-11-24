package com.lsd.fun.modules.recommend.dto;

import org.apache.spark.api.java.function.Function;

import java.io.Serializable;

/**
 * 我分析原因是：这里的实现了接口ForeachFunction的内部类在运行的时候Master会将其序列化传给Slave，因此这个类必须要序列化，但是内部类序列化的时候需要其所寄宿的外部类的对象，而我这个外部类并没有实现Serializable接口，因此无法序列化，则会报上边的错误，将外部类实现Serializable接口后则问题解决。
 * 按照上边的分析，如果我单独将ForeachFunction接口实现成一个类是不是就不需要让宿主类实现Serializable接口了呢？于是实现了一个类
 * SparkRDD 的map/forEach操作单独抽取出来，解决其父类序列化问题
 * <p>
 * Created by lsd
 * 2020-04-14 23:13
 */
public class RatingMatrixParseLineFunction implements Function<String, RatingMatrix>, Serializable {
    public static final long serialVersionUID = 1069997744909372812L;

    @Override
    public RatingMatrix call(String str) throws Exception {
        return RatingMatrix.parseLine(str);
    }
}
