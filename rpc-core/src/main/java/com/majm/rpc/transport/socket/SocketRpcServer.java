package com.majm.rpc.transport.socket;

import com.majm.rpc.config.CustomShutdownHook;
import com.majm.rpc.provider.ServiceProvider;
import com.majm.rpc.provider.ServiceProviderImpl;
import com.majm.rpc.registry.ServiceRegistry;
import com.majm.rpc.registry.ZkServiceProvider;
import com.majm.rpc.transport.SocketRpcRequestHandlerRunnable;
import com.majm.rpc.utils.concurrent.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 2:24 下午
 * @since
 */
@Slf4j
public class SocketRpcServer {

    private String host;
    private int port;
    private final ExecutorService threadPool;

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;


    public SocketRpcServer(String host, int port) {
        this.host = host;
        this.port = port;
        serviceRegistry = new ZkServiceProvider();
        serviceProvider = new ServiceProviderImpl();
        threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
    }

    public <T> void publishService(T service, Class<T> serviceClass) {
        serviceProvider.addServiceProvider(service, serviceClass);
        serviceRegistry.registerService(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    public void start() {
        try (ServerSocket server = new ServerSocket()) {
            server.bind(new InetSocketAddress(host, port));
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            log.info("server start...");
            Socket socket;
            while ((socket = server.accept()) != null) {
                log.info("client connected");
                threadPool.submit(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (Exception ex) {
            log.info("SocketRpcServer  occur exception, ", ex);
        }
    }
}
