package com.mjm.chapter2.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-06-25 15:42
 * @since
 */
public class EchoServer {

    private int port;

    public EchoServer(int port){
        this.port = port;
    }

    public static void main(String[] args) {

        new EchoServer(8989).start();

    }

    private void start() {
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        // 创建 ServerBootstrap
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                // 指定所使用的 NIO 传输 Channel
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                // 添加一个 EchoServerHandler 到 子Channel 的 ChannelPipline
                // 服务端必须绑定 处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //  EchoServerHandler 被标注为@Shareable，
                        //  所以我们可以总是使用同样的实例
                        ch.pipeline().addLast(echoServerHandler);
                    }
                });

        try {
            // 异步调用绑定服务器，
            // 调用sync() 阻塞知道绑定完成， 返回 ChannelFuture 可以利用 channelFuture 进行后续客户端与 服务器端交互逻辑
            ChannelFuture future = bootstrap.bind().sync();
            // 获取 Channel 的 CloseFuture， 并且阻塞当前线程直到完成
            // 回收资源
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭 EventLoopGroup, 释放所有资源
                // shutdownGracefully(): 安全的关闭方法 可以保证不放弃任何一个已连接的客户端请求
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
