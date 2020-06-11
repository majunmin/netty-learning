package com.majm.rpc.provider;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 2:02 下午
 * @since
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    private static final Map<String, Object> registryMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = new HashSet<>();

    @Override
    public <T> void addServiceProvider(T service, Class<T> serviceClass) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        registryMap.put(serviceName, service);
        log.info("ServiceProviderImpl add service: [{}] and interfaces:[{}]", serviceName, service.getClass().getInterfaces());
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        return registryMap.get(serviceName);
    }
}
