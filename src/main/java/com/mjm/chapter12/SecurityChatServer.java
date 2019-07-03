package com.mjm.chapter12;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-02 14:35
 * @since
 */
public class SecurityChatServer extends ChatServer {

    private final SslContext sslContext;

    public SecurityChatServer(SslContext sslContext){
        this.sslContext = sslContext;
    }



    public static void main(String[] args) {

        try {
            SelfSignedCertificate certificate = new SelfSignedCertificate();
            SslContext sslContext = SslContext.newServerContext(certificate.certificate(), certificate.privateKey());

            SecurityChatServer endpoint = new SecurityChatServer(sslContext);
            ChannelFuture future = endpoint.start(new InetSocketAddress(8989));

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    endpoint.destroy();
                }
            });

            future.channel().closeFuture().syncUninterruptibly();



        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (SSLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChannelHandler createInitializer(ChannelGroup channelGroup) {
        return new SecurityChatServerInitializer(channelGroup, sslContext);
    }
}
