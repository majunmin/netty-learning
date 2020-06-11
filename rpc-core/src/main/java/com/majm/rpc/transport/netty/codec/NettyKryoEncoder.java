package com.majm.rpc.transport.netty.codec;

import com.majm.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 *  编码器
 * @author majunmin
 * @description
 * @datetime 2020/6/10 5:38 下午
 * @since
 */
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {

    private Serializer serializer;
    private Class<?> genericClass;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)){
            byte[] bytes = serializer.seralize(msg);
            // 写入 消息长度，  writeIndex + 4 (解决 半包 沾包问题)
            out.writeInt(bytes.length);
            // 写入消息
            out.writeBytes(bytes);
        }
    }
}
