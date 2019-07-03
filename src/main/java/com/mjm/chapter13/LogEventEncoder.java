package com.mjm.chapter13;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * LogEventEncoder 创建了
 * 即将被发送到指定的 InetSocketAddress 的 DatagramPacket 消息 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-03 10:19
 * @since
 */
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {

    private final InetSocketAddress remoteAddress;

    public LogEventEncoder(InetSocketAddress remoteAddress){
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent logEvent, List<Object> out) throws Exception {
        byte[] logFile = logEvent.getLogFile().getBytes(StandardCharsets.UTF_8);
        byte[] msg = logEvent.getMsg().getBytes(StandardCharsets.UTF_8);

        int capacity = logFile.length + msg.length + 1;
        ByteBuf byteBuf = ctx.alloc().buffer(capacity);
        byteBuf.writeBytes(logFile);
        byteBuf.writeByte(LogEvent.SEPTERATOR);
        byteBuf.writeBytes(msg);
        out.add(new DatagramPacket(byteBuf, remoteAddress));
    }
}
