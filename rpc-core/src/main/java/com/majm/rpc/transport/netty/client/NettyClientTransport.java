package com.majm.rpc.transport.netty.client;

import com.majm.rpc.dto.RpcRequest;
import com.majm.rpc.dto.RpcResponse;
import com.majm.rpc.transport.ClientTransport;
import com.majm.rpc.utils.checker.RpcMessageChecker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 5:50 下午
 * @since
 */
@Slf4j
public class NettyClientTransport implements ClientTransport {

    private InetSocketAddress inetSocketAddress;

    public NettyClientTransport(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }



    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        AtomicReference<Object> result = new AtomicReference<>(null);
        Channel channel = ChannelProvider.get(inetSocketAddress);
        if (channel.isActive()) {
            channel.writeAndFlush(rpcRequest).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("client send message: {}", rpcRequest);
                    } else {
                        future.channel().close();
                        log.error("Send failed:", future.cause());
                    }
                }
            });
            try {
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                log.info("client get response from channel: [{}]", rpcResponse);
                //校验 RpcResponse 和 RpcRequest
                RpcMessageChecker.check(rpcResponse, rpcRequest);
                result.set(rpcResponse.getData());
            } catch (InterruptedException e) {
                log.error("error occured when send rpc message from client");
                e.printStackTrace();
            }
        }  else {
            System.exit(0);
        }
        return result;
    }
}
