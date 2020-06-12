package com.majm.rpc.utils.zk;

import com.majm.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * zk Curator 工具类
 */
@Slf4j
public final class CuratorUtils {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 5;
    private static final String CONNECT_STRING = "127.0.0.1:2181";
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    private static Map<String, List<String>> serviceAddressMap = new ConcurrentHashMap<>();
    //
    private static Set<String> registeredPathSet = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;

    static {
        zkClient = getZkClient();
    }

    private CuratorUtils() {
    }

    /**
     * 创建持久化节点。不同于临时节点，持久化节点不会因为客户端断开连接而被删除
     *
     * @param path 节点路径
     */
    public static void createPersistentNode(String path) {
        try {
            if (registeredPathSet.contains(path) || zkClient.checkExists().forPath(path) != null){
                log.info("节点已经存在, path: [{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("节点创建成功, path: [{}]", path);
            }
            registeredPathSet.add(path);
        } catch (Exception e) {
            log.error("节点创建失败, path:[{}], ", path);
            throw new RpcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 获取某个字节下的子节点,也就是获取所有 提供服务的生产者的地址
     *
     * @param serviceName 服务对象接口名 eg:github.javaguide.HelloService
     * @return 指定字节下的所有子节点
     */
    public static List<String> getChildrenNodes(String serviceName) {
        if (serviceAddressMap.containsKey(serviceName)){
            return serviceAddressMap.get(serviceName);
        }
        List<String> result = null;
        try {
            String servicePath = ZK_REGISTER_ROOT_PATH.concat("/").concat(serviceName);
            result = zkClient.getChildren().forPath(servicePath);
            serviceAddressMap.put(serviceName, result);
            registerWatcher(zkClient, serviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 清空注册中心的数据
     */
    public static void clearRegistry() {
        registeredPathSet.forEach(path -> {
            try {
                zkClient.delete().forPath(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        log.info("服务端（Provider）所有注册的服务都被清空:[{}]", registeredPathSet.toString());
    }

    private static CuratorFramework getZkClient() {
        // 重试策略 重试3次，并且会增加重试之间的睡眠时间 (ExponentialBackoffRetry)
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_STRING)
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();
        return curatorFramework;
    }

    /**
     * 注册监听指定节点。
     *
     * @param serviceName 服务对象接口名 eg:github.javaguide.HelloService
     */
    private static void registerWatcher(CuratorFramework zkClient, String serviceName) {
        String servicePath = ZK_REGISTER_ROOT_PATH.concat("/").concat(serviceName);
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                List<String> addressList = client.getChildren().forPath(servicePath);
                serviceAddressMap.put(serviceName, addressList);
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);

        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
    }

}
