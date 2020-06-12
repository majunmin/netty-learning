package com.majm.rpc.registry;

import com.majm.rpc.utils.zk.CuratorUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * zk实现服务查找
 *
 * @author majunmin
 * @description
 * @datetime 2020/6/11 12:00 上午
 * @since
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    @Override
    public InetSocketAddress lookUpService(String serviceName) {
        // todo: 负载均衡
        String serviceAddress = CuratorUtils.getChildrenNodes(serviceName).get(0);
        log.info("成功找到服务地址: [{}]", serviceAddress);
        String[] split = serviceAddress.split(":");
        String host = split[0];
        int port = Integer.parseInt(split[1]);
        return new InetSocketAddress(host, port);
    }
}
