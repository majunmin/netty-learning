package com.majm.rpc.proxy;

import com.majm.rpc.dto.RpcRequest;
import com.majm.rpc.transport.ClientTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * 动态代理类
 * 使用 动态代理 使调用远程方法(rpc) 使用起来像是调用本地方法一样
 */
@Slf4j
@AllArgsConstructor
public class RpcClientProxy implements InvocationHandler {
    
    private ClientTransport clientTransport;

    public <T> T getProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("invoke service [{}] method [{}]", method.getName());
        RpcRequest rpcRequest = new RpcRequest().setRequestId(UUID.randomUUID().toString())
                .setMethodName(method.getName())
                .setInterfaceName(method.getDeclaringClass().getCanonicalName())
                .setParameters(args)
                .setParamTypes(method.getParameterTypes());
        return clientTransport.sendRpcRequest(rpcRequest);
    }
}
