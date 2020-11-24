package com.lsd.fun.config;

import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * ES配置类
 *  1.仅支持HTTP
 *  2.嗅探不支持 spring-boot-devtools，热部署后sniffing nodes会报错：Request cannot be executed; I/O reactor status: STOPPED
 * <p>
 * Created by lsd
 * 2020-01-28 23:59
 */
@Configuration
public class EsConfig {

    @Value("#{'${fun.elasticsearch.node-list}'.split(',')}")
    private List<String> nodeList;

    private HttpHost[] getHttpHostList(List<String> nodeList) {
        return nodeList.stream().map(nodeStr -> {
            String[] items = nodeStr.split(":");
            return new HttpHost(items[0], Integer.parseInt(items[1]), "http");
        }).toArray(HttpHost[]::new);
    }


    @Bean
    public RestClientBuilder restClientBuilder() {
        // 集群配置（REST Client不需要配置集群名称），客户端以轮询方式请求
        RestClientBuilder builder = RestClient.builder(this.getHttpHostList(nodeList));
        // 修改默认请求配置（例如，请求超时，连接超时，身份验证等），默认参数见RestClientBuilder.java
        builder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder.setSocketTimeout(30000)     //连接建立超时时间，即三次握手完成超时时间，默认30s
                        .setConnectTimeout(10000)                //连接建立后，数据传输过程中数据包之间间隔的最大时间，默认1s
                        .setConnectionRequestTimeout(10000)      //从httpClient连接池获取连接的超时时间，默认0.5s
        );
        // 修改异步http客户端连接池配置
        builder.setHttpClientConfigCallback(httpAsyncClientBuilder ->
                httpAsyncClientBuilder.setDefaultIOReactorConfig(
                        IOReactorConfig.custom()
                                .setIoThreadCount(8)             //线程数，默认是可用处理器数量
                                .setSoTimeout(30000)             //NIO的SocketTimeout
                                .setConnectTimeout(10000)        //连接建立后，数据传输过程中数据包之间间隔的最大时间
                                .build()
                )
        );
        return builder;
    }

    /**
     * 低级客户端
     */
    @Bean
    public RestClient restClient() {
        // 连接失败时的嗅探监听器
        SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
        // 将失败监听器设置到restClient中
        RestClient restClient = restClientBuilder()
                .setFailureListener(sniffOnFailureListener)
                .build();
        // 构造嗅探器
        // Sniffer将定期使用提供的RestClient（默认情况下每隔5分钟）从集群中获取当前节点的列表，并通过调用RestClient＃setNodes来更新它们。
        Sniffer sniffer = Sniffer.builder(restClient)
                .setSniffIntervalMillis(60000)              //常规嗅探60s一次，用于更新节点信息。默认5分钟
                .setSniffAfterFailureDelayMillis(30000)     //连接失败后开启额外的嗅探task30s一次，默认1分钟
                .build();
        // 监听器绑定嗅探器
        sniffOnFailureListener.setSniffer(sniffer);
        return restClient;
    }

    /**
     * 基于低级客户端创建高级客户端
     */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestClientBuilder builder = restClientBuilder();
        SniffOnFailureListener listener = new SniffOnFailureListener();
        builder.setFailureListener(listener);
        RestHighLevelClient client = new RestHighLevelClient(builder);
        Sniffer sniffer = Sniffer.builder(client.getLowLevelClient())
                .setSniffIntervalMillis(60000)              //常规嗅探60s一次，用于更新节点信息。默认5分钟
                .setSniffAfterFailureDelayMillis(30000)     //连接失败后开启额外的嗅探task30s一次，默认1分钟
                .build();
        listener.setSniffer(sniffer);
        return client;
    }

}
