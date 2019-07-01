package com.mjm.chapter4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 阻塞的 OIO Server </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-06-26 17:04
 * @since
 */
public class NettyOioServer {

    public static void main(String[] args) {

//        ByteBuf byteBuf = Unpooled.unreleasableBuffer(
//                Unpooled.copiedBuffer("Hi world!".getBytes()));

        OioEventLoopGroup group = new OioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(group)
                .channel(OioServerSocketChannel.class)
                .localAddress(8989)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {

                                /**
                                 * Netty 的 channel 实现是线程安全的
                                 */
                                Channel channel = ctx.channel();
                                ExecutorService threadPool = Executors.newCachedThreadPool();
                                for (int i = 0; i < 50; i++) {
                                    ByteBuf buf = Unpooled.copiedBuffer("your data" + i, CharsetUtil.UTF_8).retain();
                                    Runnable task = () ->{
                                        channel.writeAndFlush(buf);
                                    };
                                    threadPool.execute(task);
                                }
                            }
                        });
                    }
                });

        try {
            ChannelFuture channelFuture = bootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
