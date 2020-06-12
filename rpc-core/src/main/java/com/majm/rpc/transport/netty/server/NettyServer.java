package com.majm.rpc.transport.netty.server;

import com.majm.rpc.config.CustomShutdownHook;
import com.majm.rpc.dto.RpcRequest;
import com.majm.rpc.dto.RpcResponse;
import com.majm.rpc.provider.ServiceProvider;
import com.majm.rpc.provider.ServiceProviderImpl;
import com.majm.rpc.registry.ServiceRegistry;
import com.majm.rpc.registry.ZkServiceProvider;
import com.majm.rpc.serialize.Serializer;
import com.majm.rpc.serialize.kyro.KryoSerializer;
import com.majm.rpc.transport.netty.codec.NettyKryoDecoder;
import com.majm.rpc.transport.netty.codec.NettyKryoEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 5:50 下午
 * @since
 */
public class NettyServer {

    private String host;
    private int port;
    private Serializer serializer;
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    public NettyServer(int port) {
        this("localhost", port, new KryoSerializer());
    }

    public NettyServer(String host, int port) {
        this(host, port, new KryoSerializer());
    }

    private NettyServer(String host, int port, Serializer serializer) {
        this.host = host;
        this.port = port;
        this.serializer = new KryoSerializer();
        serviceRegistry = new ZkServiceProvider();
        serviceProvider = new ServiceProviderImpl();
    }

    public <T> void publishService(T service, Class<T> serviceClass){
        serviceProvider.addServiceProvider(service, serviceClass);
        serviceRegistry.registerService(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    public void start(){
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                .option(ChannelOption.SO_BACKLOG, 128)
                // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 是否开启 TCP 底层心跳机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new NettyKryoDecoder(serializer, RpcRequest.class))
                                .addLast(new NettyKryoEncoder(serializer, RpcResponse.class))
                                .addLast(new NettyServerHandler());
                    }
                });

        try {
            // 绑定端口，同步等待绑定成功
            ChannelFuture f = serverBootstrap.bind(host, port).sync();
            // 等待服务端*监听端口关闭*
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }


}
