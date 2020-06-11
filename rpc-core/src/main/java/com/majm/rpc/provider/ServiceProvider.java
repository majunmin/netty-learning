package com.majm.rpc.provider;

/**
 * 服务注册接口
 * @author majunmin
 */
public interface ServiceProvider {
    <T> void addServiceProvider(T service, Class<T> serviceClass);

    Object getServiceProvider(String serviceName);
}
