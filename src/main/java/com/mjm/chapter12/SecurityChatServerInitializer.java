package com.mjm.chapter12;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 *  扩展 ChatServerInitializer 以添加加密 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-02 14:32
 * @since
 */
public class SecurityChatServerInitializer extends ChatServerInitializer {

    private final SslContext sslContext;


    public SecurityChatServerInitializer(ChannelGroup group, SslContext sslContext) {
        super(group);
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        super.initChannel(ch);
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        sslEngine.setUseClientMode(false);
        ch.pipeline().addFirst(new SslHandler(sslEngine));
    }
}
