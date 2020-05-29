package com.mjm.chapter4.telnet;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author majunmin
 * @description
 * @datetime 2020/5/29 3:54 下午
 * @since
 */
public class TelnetClientInitializer extends ChannelInitializer<SocketChannel> {

    StringEncoder ENCODER = new StringEncoder(StandardCharsets.UTF_8);
    StringDecoder DECODER = new StringDecoder(StandardCharsets.UTF_8);

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new LineBasedFrameDecoder(1024))
                .addLast(ENCODER).addLast(DECODER)
                .addLast(new TelnetClientHandler());
    }
}
