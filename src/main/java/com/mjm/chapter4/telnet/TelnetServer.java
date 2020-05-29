package com.mjm.chapter4.telnet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author majunmin
 * @description
 * @datetime 2020/5/29 3:31 下午
 * @since
 */
public class TelnetServer {

    private static ServerBootstrap bootstrap;
    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workGroup;


    public static void start(){
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup,  workGroup)
                .localAddress(8989)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new TelnetServerInitializer());

        try {
            bootstrap.bind().sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            close();
        }
    }

    private static void close(){
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        start();
    }
}
