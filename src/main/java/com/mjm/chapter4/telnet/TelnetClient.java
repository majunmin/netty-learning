package com.mjm.chapter4.telnet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

/**
 * @author majunmin
 * @description
 * @datetime 2020/5/29 3:54 下午
 * @since
 */
public class TelnetClient {

    private static Bootstrap bootstrap;
    private static EventLoopGroup bossGroup;
    private static String host = "localhost";
    private static Integer port = 8989;

    public static void start(){
        try {
            bootstrap = new Bootstrap();
            bossGroup = new NioEventLoopGroup();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new TelnetClientInitializer());


            Channel channel = bootstrap.connect(new InetSocketAddress(host, port)).sync().channel();
            ChannelFuture lastWriteFuture;
            while(true){
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String msg = null;

                try {
                    msg = br.readLine();
                    if (msg == null) {
                        continue;
                    }
                    // 发送消息
                    lastWriteFuture = channel.writeAndFlush(msg + "\r\n");

                    // 断开连接
                    if ("bye".equals(msg)){
                        channel.close().sync();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null){
                lastWriteFuture.sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            close();
        }
    }

    public static void close(){
        bossGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        start();
    }
}
