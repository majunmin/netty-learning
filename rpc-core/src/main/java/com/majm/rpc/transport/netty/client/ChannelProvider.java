package com.majm.rpc.transport.netty.client;

import com.majm.rpc.enums.RpcErrorMessageEnum;
import com.majm.rpc.exception.RpcException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 7:11 下午
 * @since
 */
@Slf4j
public class ChannelProvider {

    private static Bootstrap bootstrap = NettyClient.initializeBootstrap();
    private static Channel channel = null;
    /**
     * 最多重试次数
     */
    private static final int MAX_RETRY_COUNT = 5;

    public static Channel get(InetSocketAddress inetSocketAddress){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            connect(bootstrap, inetSocketAddress, countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch) {
        connect(bootstrap, inetSocketAddress, countDownLatch, MAX_RETRY_COUNT);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch, int retry) {
        bootstrap.connect(inetSocketAddress).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    log.info("connect server success!");
                    channel = future.channel();
                    countDownLatch.countDown();
                    return;
                }
                if (retry == 0) {
                    log.error("客户端连接失败:重试次数已用完，放弃连接！");
                    countDownLatch.countDown();
                    throw new RpcException(RpcErrorMessageEnum.CLIENT_CONNECT_SERVER_FAILURE);
                }
                // 第几次重连
                int order = (MAX_RETRY_COUNT - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                log.error("{}: 连接失败，第 {} 次重连……", LocalDateTime.now(), order);
                bootstrap.config().group()
                        .schedule(() -> connect(bootstrap, inetSocketAddress, countDownLatch, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }


}
