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
 * @datetime 2019-07-01 11:57
 * @since
 */
public class AbsIntegerEncoderTest {

    @Test
    public void testEncoded(){

        ByteBuf byteBuf = Unpooled.buffer();
        for (int i = 0; i < 10; i++) {
             byteBuf.writeInt(i * -1);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        assertTrue(channel.writeOutbound(byteBuf));
        // 标记channel 为已完成状态
        assertTrue(channel.finish());

        for (int i = 0; i < 10; i++) {
            assertEquals((int)channel.readOutbound(), i);
        }

        assertNull(channel.readOutbound());
    }

}
