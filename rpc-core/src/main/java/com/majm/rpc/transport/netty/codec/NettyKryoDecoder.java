package com.majm.rpc.transport.netty.codec;

import com.majm.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 5:43 下午
 * @since
 */
@Slf4j
@AllArgsConstructor
public class NettyKryoDecoder extends ByteToMessageDecoder {

    private Serializer serializer;
    private Class<?> genericClass;

    /**
     * Netty传输的消息长度也就是对象序列化后对应的字节数组的大小，存储在 ByteBuf 头部
     */
    private static final int BODY_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= BODY_LENGTH){
            in.markReaderIndex();
            int dataLength = in.readInt();
            if (dataLength < 0 || in.readableBytes() < 0){
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }
            if (in.readableBytes() < dataLength){
                in.resetReaderIndex();
                return;
            }
            byte[] body = new byte[dataLength];
            in.readBytes(body);
            Object obj = serializer.deSeralize(body, genericClass);
            out.add(obj);
            log.info("successful decode bytes");

        }
    }
}
