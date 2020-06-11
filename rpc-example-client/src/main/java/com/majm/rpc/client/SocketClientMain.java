package com.majm.rpc.client;

import com.majm.rpc.api.HelloService;
import com.majm.rpc.entity.Hello;
import com.majm.rpc.proxy.RpcClientProxy;
import com.majm.rpc.transport.socket.SocketRpcClient;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 4:47 下午
 * @since
 */
public class SocketClientMain {

    public static void main(String[] args) {
        SocketRpcClient socketRpcClient = new SocketRpcClient("localhost", 9000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(socketRpcClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);

        Hello hello = new Hello("majm", "!!!天下第一!!!");
        String result = helloService.hello(hello);
        System.out.println(result);
    }
}
