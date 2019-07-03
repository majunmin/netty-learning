package com.mjm.chapter12;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * 处理文本帧 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-01 20:48
 * @since
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group){
        this.group = group;
    }

    /**
     * 重写 userEventTriggered() 方法以处理自定义事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE){
            // 如果该事件表示握手成功，则从该 Channelipeline 中移除 HttpRequestHandler 因为将不会接收到任何 HTTP 消息了
            ctx.pipeline().remove(HttpRequestHandler.class);
            // 通知所有已经连接的 WebSocket 客户端新的客户端已经连接上了
            group.writeAndFlush(new TextWebSocketFrame("Clinet: " + ctx.channel() + " joined"));
            // 将新的 WebSocket Channel 添加到 ChannelGroup 中，以便它可以接收到所有的消息
            group.add(ctx.channel());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 增加消息的引用计数，并将它写到 ChannelGroup 中所有已经连接的客户端
        group.writeAndFlush(msg.retain());
    }
}
