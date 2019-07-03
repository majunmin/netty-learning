package com.mjm.chapter13;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 负责将传入的 DatagramPacket 解码为 LogEvent 消息 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-03 16:37
 * @since
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {


    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {

        ByteBuf content = datagramPacket.content();
        int idx = content.indexOf(0 , content.readableBytes(), LogEvent.SEPTERATOR);
        String fileName = content.slice(0, idx).toString(StandardCharsets.UTF_8);
        String logMsg = content.slice(idx+ 1, content.readableBytes()).toString(StandardCharsets.UTF_8);

        LogEvent logEvent = new LogEvent(fileName, logMsg);
        out.add(logEvent);
    }
}
