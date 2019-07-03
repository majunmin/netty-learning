package com.mjm.chapter13;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * 广播📢 服务器 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-03 10:49
 * @since
 */
public class LogEventBroadcaster {

    private final EventLoopGroup group;

    private final Bootstrap bootstrap;

    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, File file){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public void run() throws Exception {
        Channel channel = bootstrap.bind(0).sync().channel();

        long pointer = 0;
        while(true){
            long length = file.length();
            if (pointer > length){
                // 如果有必要，将文件指针设置到该文件的最后一个字节
                pointer = length;
            } else if (pointer < length){
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(pointer);
                String line = null;

                while((line = raf.readLine()) != null){
                    // 对于每个日志条目, 写入一个 LogEvent 到 Channel 中
                    channel.writeAndFlush(new LogEvent(file.getAbsolutePath(), line));
                }
                pointer = raf.getFilePointer();
                raf.close();
            }

            TimeUnit.SECONDS.sleep(1);
        }
    }

    public void stop(){
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        LogEventBroadcaster eventBroadcaster = new LogEventBroadcaster(new InetSocketAddress("255.255.255.255", 9999), new File("./index.html"));
        try {
            eventBroadcaster.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eventBroadcaster.stop();
        }


    }
}
