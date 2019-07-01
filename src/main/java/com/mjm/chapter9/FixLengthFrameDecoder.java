package com.mjm.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码器 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-01 11:24
 * @since
 */
public class FixLengthFrameDecoder extends ByteToMessageDecoder {

    public final int frameLength;

    public FixLengthFrameDecoder(int frameLength){
        if ( frameLength <= 0){
            throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 检查是否有足够的字节可以被读取,以生成下一个帧
        while(in.readableBytes() >= frameLength){
            // 从 ByteBuf 中读取一个新帧
            ByteBuf byteBuf = in.readBytes(frameLength);
            // 将该帧添加到已被解码的消息列表中
            out.add(byteBuf);
        }
    }
}
