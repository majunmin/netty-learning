package com.majm.rpc.transport.netty.client;

import com.majm.rpc.dto.RpcResponse;
import com.majm.rpc.factory.SingletonFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 7:03 下午
 * @since
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getSingleton(UnprocessedRequests.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        log.info("client received msg : ");
        RpcResponse rpcResponse = (RpcResponse) msg;
        unprocessedRequests.complete(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NettyClientHandler occur error");
        cause.printStackTrace();
        ctx.close();
    }
}
