package com.mjm.chapter2.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * ChannelHandler  </br>
 * 接受所有的入站消息
 *
 * @author majunmin
 * @description
 * @datetime 2019-06-25 15:44
 * @since
 *
 * @Shareable -
 *
 * 代表当前处理器是可共享的。 也就是说这处理器可以供多个客户端共同使用
 * 如果不添加次注解， 则每次客户端请求时， 需要重新new 一个 处理器
 *
 *  ch.pipline().addLast(new XxxHandler());
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * 对每个传入的消息调用
     * @param ctx 上下文对象 包含客户端建立连接的所有资源  包括对应的channel
     * @param msg 读取到的消息类型 默认类型是 ByteBuf
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("server received msg: " + byteBuf.toString(StandardCharsets.UTF_8));
        // 将收到的消息发送给发送者 而不冲刷出站消息
        // write() 不会刷新缓存 -> 缓存中的数据不会发送到客户端， 必须再次调用 flush()
        ctx.write(byteBuf);
    }

    /**
     * 通知ChannelInboundHandler最后一次对channelRead()的调用是当前批量读取中的最后一条消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将未决消息冲刷到远程节点，并且关闭该 channel
        // writeAndFlush() 写操作自动释放缓存 避免内存溢出
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 在读取操作期间 有异常抛出时 调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 关闭该 Channel
        ctx.close();
    }
}
