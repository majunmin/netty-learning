package com.mjm.chapter5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.util.CharsetUtil;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-06-27 16:12
 * @since
 */
public class TestCompositeByteBuf {

    public static void main(String[] args) {

//        test1();
//        test2();
//        test3();
//        test4();
//        test5();
//
//        test6();

        test7();
    }

    private static void test7() {

        ByteBuf byteBuf = Unpooled.copiedBuffer("hello World", CharsetUtil.UTF_8);
        ByteBuf duplicate = byteBuf.duplicate();
        System.out.println(byteBuf.refCnt());

    }

    // get()和 set()操作，从给定的索引开始，并且保持索引不变
    // read()和 write()操作，从给定的索引开始，并且会根据已经访问过的字节数对索引进行调整
    private static void test6() {

        ByteBuf byteBuf = Unpooled.copiedBuffer("hello World", CharsetUtil.UTF_8);
        System.out.println("readIndex: " + byteBuf.readerIndex());
        System.out.println("writeIndex: " + byteBuf.writerIndex());
        byteBuf.getByte(1);
        System.out.println("readIndex: " + byteBuf.readerIndex());
        System.out.println("writeIndex: " + byteBuf.writerIndex());

        byteBuf.setByte(2, '1');
        System.out.println("readIndex: " + byteBuf.readerIndex());
        System.out.println("writeIndex: " + byteBuf.writerIndex());

        byteBuf.writeByte('a');
        System.out.println("readIndex: " + byteBuf.readerIndex());
        System.out.println("writeIndex: " + byteBuf.writerIndex());

        byteBuf.readerIndex(1);
        System.out.println("readIndex: " + byteBuf.readerIndex());
        System.out.println("writeIndex: " + byteBuf.writerIndex());
    }

    // copy 复制一个新数组
    // 数据是不共享的
    private static void test5() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello World", CharsetUtil.UTF_8);
        ByteBuf copyByteBuf = byteBuf.copy();

        // false
        System.out.println(byteBuf == copyByteBuf);
        copyByteBuf.setByte(0, 'J');
        // false
        System.out.println(byteBuf == copyByteBuf);
    }

    // scice 切片
    // 返回一个 新的 ByteBuf, 与原来的 ByteBuf 共享一个数组
    private static void test4() {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes("hello world".getBytes());
        System.out.println(byteBuf.readerIndex());
        System.out.println(byteBuf.writerIndex());

        ByteBuf sliceByteBuf = byteBuf.slice(0, 5);
        System.out.println(sliceByteBuf.readerIndex());
        System.out.println(sliceByteBuf.writerIndex());

        sliceByteBuf.setByte(0, 'J');
        // true
        System.out.println(byteBuf.getByte(0) == sliceByteBuf.getByte(0));

    }


    /**
     * ByteBuf 的使用
     *
     * ByteBuf 与JDK中的 ByteBuffer 最大的区别就是：
     * 1. netty 的 ByteBuf 采用了读/写索引分离，一个初始化的 ByteBuf 的 readerIndex 和 writerIndex 都处于0位置。
     * 2. 当读索引和写索引处于同一位置时，如果我们继续读取，就会抛出异常 IndexOutOfBoundsException。
     * 3. 对于 ByteBuf 的任何读写操作都会分别单独的维护读索引和写索引。maxCapacity 最大容量默认的限制就是 Integer.MAX_VALUE。
     *
     */
    private static void test3() {

    }

    // test composite
    private static void test2() {
        // 符合缓冲区
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();

        // 缓冲区
        ByteBuf buffer = Unpooled.buffer();

        // 直接缓冲区
        ByteBuf directBuffer = Unpooled.directBuffer();

        buffer.writeBytes("header".getBytes());
        directBuffer.writeBytes("body".getBytes());

        compositeByteBuf.addComponents(buffer, directBuffer);

        for (ByteBuf byteBuf : compositeByteBuf) {
            System.out.println(byteBuf.toString(0, byteBuf.readableBytes(), CharsetUtil.UTF_8));
        }
    }

    private static void test1(){
        ByteBuf directBuffer = Unpooled.directBuffer(16);
        System.out.println(directBuffer.readableBytes());
        if (!directBuffer.hasArray()){
            int length = directBuffer.readerIndex();
            byte[] arr = new byte[length];
            directBuffer.getBytes(0, arr);
        }
    }
}
