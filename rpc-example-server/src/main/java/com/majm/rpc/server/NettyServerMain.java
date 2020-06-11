package com.majm.rpc.server;

import com.majm.rpc.api.HelloService;
import com.majm.rpc.provider.ServiceProviderImpl;
import com.majm.rpc.server.service.impl.HelloServiceImpl;
import com.majm.rpc.transport.netty.server.NettyServer;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 7:30 下午
 * @since
 */
public class NettyServerMain {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceProviderImpl registry = new ServiceProviderImpl();
        registry.addServiceProvider(helloService, HelloService.class);

        // 启动服务
        NettyServer nettyServer = new NettyServer(9000);
        nettyServer.start();
    }
}
