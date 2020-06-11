package com.majm.rpc.transport.netty.server;

import com.majm.rpc.dto.RpcRequest;
import com.majm.rpc.dto.RpcResponse;
import com.majm.rpc.transport.socket.RpcRequestHandler;
import com.majm.rpc.utils.concurrent.ThreadPoolFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 6:34 下午
 * @since
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final String THREAD_NAME_PREFIX = "netty-server-handler-rpc-pool";
    private static final RpcRequestHandler rpcRequestHandler;
    private static final ExecutorService threadPool;

    static {
        rpcRequestHandler = new RpcRequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        threadPool.submit(() ->{
            log.info("NettyServerHandler receive message [{}], by client [{}]", msg, Thread.currentThread().getName());
            Object result = rpcRequestHandler.handle(msg);
            log.info("server handle result [{}]", result);
            ChannelFuture channelFuture = ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error occur, ", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
