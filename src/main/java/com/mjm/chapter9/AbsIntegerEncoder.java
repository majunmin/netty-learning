package com.mjm.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 编码器 </br>
 * 将负数 转化为 正整数输出
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-01 11:53
 * @since
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {


    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        //检查是否有足够的字节用来编码
        while(msg.readableBytes() >= 4){
            int val =  Math.abs(msg.readInt());
            // 将该整数写入到编码消息的 List 中
            out.add(val);
        }
    }
}
