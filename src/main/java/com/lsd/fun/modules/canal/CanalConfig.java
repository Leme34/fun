package com.lsd.fun.modules.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 阿里中间件Canal配置类
 * <p>
 * Created by lsd
 * 2020-03-20 17:10
 */
//@Configuration
@Deprecated  //转用 Canal+ Kafka 实现，弃用此方案
public class CanalConfig implements DisposableBean {

    private CanalConnector canalConnector;

    @Bean
    public CanalConnector getCanalConnector() {
        // canal-deployer集群地址
        final List<InetSocketAddress> addresses = Lists.newArrayList(
                new InetSocketAddress("192.168.101.10", 11111)
        );
        canalConnector = CanalConnectors.newClusterConnector(addresses,
                "example", "canal", "canal");
        //发起连接
        canalConnector.connect();
        //指定filter，格式为{database}.{table}，空则为订阅所有数据库表
        canalConnector.subscribe();
        //回滚寻找上次管道流中断的位置
        canalConnector.rollback();
        return canalConnector;
    }


    /**
     * 应用关闭断开连接，防止连接泄露
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        if (canalConnector != null) {
            canalConnector.disconnect();
        }
    }
}
