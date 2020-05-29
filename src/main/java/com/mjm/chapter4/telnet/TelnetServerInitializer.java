package com.mjm.chapter4.telnet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author majunmin
 * @description
 * @datetime 2020/5/29 3:34 下午
 * @since
 */
public class TelnetServerInitializer extends ChannelInitializer {

    StringEncoder ENCODER = new StringEncoder(StandardCharsets.UTF_8);
    StringDecoder DECODER = new StringDecoder(StandardCharsets.UTF_8);


    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast(new LineBasedFrameDecoder(1024))
                .addLast(ENCODER)
                .addLast(DECODER)
                .addLast(new TelnetServerHandler());

    }
}
