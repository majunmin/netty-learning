package com.majm.rpc.client;

import com.majm.rpc.api.HelloService;
import com.majm.rpc.entity.Hello;
import com.majm.rpc.proxy.RpcClientProxy;
import com.majm.rpc.transport.netty.client.NettyClientTransport;

import java.net.InetSocketAddress;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 7:35 下午
 * @since
 */
public class NettyClient {

    public static void main(String[] args) {
        Hello hello = new Hello("majm", "天下武功,为快不破!!");
        NettyClientTransport clientTransport = new NettyClientTransport();

        RpcClientProxy rpcClientProxy = new RpcClientProxy(clientTransport);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        System.out.println(helloService.hello(hello));
    }
}
