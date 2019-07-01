package com.mjm.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-01 11:24
 * @since
 */
public class FixLengthFrameDecoderTest {

    @Test
    public void testFramesDecoded(){

        ByteBuf byteBuf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            byteBuf.writeByte(i);
        }

        ByteBuf input = byteBuf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixLengthFrameDecoder(3));

        assertTrue(channel.writeInbound(input.retain()));
        assertTrue(channel.finish());

        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        // 标记 channel 为已完成状态
        assertNull(channel.readInbound());
        byteBuf.release();
    }

    @Test
    public void testFramesDecoded2(){

        ByteBuf byteBuf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            byteBuf.writeByte(i);
        }

        ByteBuf input = byteBuf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixLengthFrameDecoder(3));

        // return false, 因为没有一个完整的可供读取的帧
        assertFalse(channel.writeInbound(input.readBytes(2)));
        assertTrue(channel.writeInbound(input.readBytes(7)));
        assertTrue(channel.finish());

        ByteBuf read = (ByteBuf) channel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        assertEquals(byteBuf.readSlice(3), read);
        read.release();

        // 标记 channel 为已完成状态
//        channel.finish();
        assertNull(channel.readInbound());
        byteBuf.release();
    }
}
