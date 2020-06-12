package com.majm.rpc.config;

import com.majm.rpc.utils.concurrent.ThreadPoolFactoryUtils;
import com.majm.rpc.utils.zk.CuratorUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端关闭时做一些操作, 比如清空所有注册的服务
 */
@Slf4j
public class CustomShutdownHook {
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CuratorUtils.clearRegistry();
            ThreadPoolFactoryUtils.shutDownAllThreadPool();
        }));
    }
}