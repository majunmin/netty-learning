package com.majm.rpc.transport.netty.client;

import com.majm.rpc.dto.RpcResponse;
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


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        log.info("client received msg : ");
        RpcResponse rpcResponse = (RpcResponse) msg;
        // 声明一个 AttributeKey 对象，类似于 Map 中的 key
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcResponse.getRequestId());
        /*
         * AttributeMap 可以看作是一个Channel的共享数据源
         * AttributeMap 的 key 是 AttributeKey，value 是 Attribute
         */
        // 将服务端的返回结果保存到 AttributeMap 上
        ctx.channel().attr(key).set(rpcResponse);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NettyClientHandler occur error");
        cause.printStackTrace();
        ctx.close();
    }
}
