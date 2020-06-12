package com.majm.rpc.registry;


import com.majm.rpc.utils.zk.CuratorUtils;

import java.net.InetSocketAddress;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/11 12:01 上午
 * @since
 */
public class ZkServiceProvider implements ServiceRegistry {

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        //根节点下注册子节点: 服务 {rootPath}/{serviceName} = {address}
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH.concat("/").concat(serviceName);
        CuratorUtils.createPersistentNode(servicePath);
    }
}
