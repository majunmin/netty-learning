package com.majm.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 * @author majunmin
 */
public interface ServiceDiscovery {

    /**
     * 查找服务
     * @param serviceName 服务名
     * @return
     */
    InetSocketAddress lookUpService(String serviceName);
}
