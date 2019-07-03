package com.mjm.chapter12;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 初始化 ChannelPipeline </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-01 21:04
 * @since
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {

    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group){
        this.group = group;
    }

    /**
     * 通过安装所有必需的 ChannelHandler 来设置该新注 册的 Channel 的 ChannelPipeline
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        pipeline.addLast(new HttpRequestHandler("/ws"));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new TextWebSocketFrameHandler(group));
    }

    /**
     *
     * |         ChannelHandler         	|                                                                                                              职 责                                                                                                              	|
     * |:------------------------------:	|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:	|
     * |         HttpServerCodec        	| 将字节解码为 HttpRequest、HttpContent 和 LastHttp- Content。并将 HttpRequest、HttpContent 和 Last- HttpContent 编码为字节                                                                                                       	|
     * |       ChunkedWriteHandler      	| 写入一个文件的内容                                                                                                                                                                                                              	|
     * |      HttpObjectAggregator      	| 将一个 HttpMessage 和跟随它的多个 HttpContent 聚合,为单个 FullHttpRequest 或者 FullHttpResponse(取,决于它是被用来处理请求还是响应)。安装了这个之后， ChannelPipeline 中的下一个 ChannelHandler 将只会收到完整的 HTTP 请求或响应 	|
     * |       HttpRequestHandler       	| 处理 FullHttpRequest(那些不发送到/ws URI 的请求)                                                                                                                                                                                	|
     * | WebSocketServerProtocolHandler 	| 按照 WebSocket 规范的要求，处理 WebSocket 升级握手 、PingWebSocketFrame PongWebSocketFrame 和 CloseWebSocketFrame                                                                                                               	|
     * |    TextWebSocketFrameHandler   	| 处理 TextWebSocketFrame 和握手完成事件                                                                                                                                                                                          	|
     *
     *
     */
}
