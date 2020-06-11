package com.majm.rpc.server.service.impl;

import com.majm.rpc.api.HelloService;
import com.majm.rpc.entity.Hello;
import lombok.extern.slf4j.Slf4j;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 4:39 下午
 * @since
 */
@Slf4j
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
