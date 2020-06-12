package com.majm.rpc.transport.netty.client;

import com.majm.rpc.dto.RpcRequest;
import com.majm.rpc.dto.RpcResponse;
import com.majm.rpc.factory.SingletonFactory;
import com.majm.rpc.registry.ServiceDiscovery;
import com.majm.rpc.registry.ZkServiceDiscovery;
import com.majm.rpc.transport.ClientTransport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 5:50 下午
 * @since
 */
@Slf4j
public class NettyClientTransport implements ClientTransport {


    private final ServiceDiscovery serviceDiscovery;

    private UnprocessedRequests unprocessedRequests;



    public NettyClientTransport() {
        serviceDiscovery = new ZkServiceDiscovery();
        unprocessedRequests = SingletonFactory.getSingleton(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRpcRequest(RpcRequest rpcRequest) {
        // 构建返回值
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookUpService(rpcRequest.getInterfaceName());
        Channel channel = ChannelProvider.get(inetSocketAddress);
        if (Objects.nonNull(channel) && channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("client send message: {}", rpcRequest);
                    } else {
                        future.channel().close();
                        resultFuture.completeExceptionally(future.cause());
                        log.error("Send failed:", future.cause());
                    }
                }
            });
        }  else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }
}
