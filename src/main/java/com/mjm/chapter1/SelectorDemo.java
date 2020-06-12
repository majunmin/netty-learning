package com.mjm.chapter1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * ![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210604203458.png) </br>
 *
 * @author majunmin
 * @description
 * @datetime 2021-06-04 20:32
 * @since
 */
public class SelectorDemo {
    public static final int BUF_SIZE = 256;
    public static final int TIMEOUT = 256;

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 创建一个选择器
        Selector selector = Selector.open();

        // 服务端 socket 监听 8080端口, 并配置为 非阻塞模式  bind  listen
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        // 配置非阻塞 channel
        serverSocketChannel.configureBlocking(false);

        // 将 channel注册进 selector(一个选择器 可以同时处理 多个 channel 的事件)
        // 通常我们都是先注册一个  OP_ACCEPT 事件,
        // 然后 在 OP_ACCEPT事件到来时,再将这个 channel 的 OP_READ 事件 注册到 selector 中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            //通过调用 select()  阻塞等待 channel IO事件
            if (selector.select(TIMEOUT) == 0) {
                System.out.println(".");
                continue;
            }

            // 获取 IO 操作就绪的 SelectionKey, 通过 SelectionKey 可以知道哪些Channel 的 哪类IO操作已经就绪
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                // 当获取一个 SelectionKey 后, 就要将它删除, 表示我们已经对这个 IO 事件进行了处理.
                keyIterator.remove();

                // 收到 accept 事件之后,会与 客户端建立连接, 获取 channel
                if (key.isAcceptable()) {
                    // 当 OP_ACCEPT 事件到来时, 我们就有从 ServerSocketChannel 中获取一个 SocketChannel, 代表客户端的连接
                    // 注意:  在 OP_ACCEPT 事件中, 从 key.channel() 返回的 Channel 是 ServerSocketChannel.
                    //      而在 OP_WRITE 和 OP_READ 中, 从 key.channel() 返回的是 SocketChannel.
                    SelectableChannel clientChannel = key.channel();
                    clientChannel.configureBlocking(false);
                    // OP_ACCEPT 到来时, 再将这个 channel的 OP_READ 注册到 selector中
                    //  注意: 如果 我们没有设置OP_READ ,  即 interestSet 仍然是 OP_CONNECT 的话, 那么 select 方法会一直直接返回.
                    clientChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(BUF_SIZE));
                }

                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    long bytesRead = channel.read(buffer);
                    if (bytesRead == -1) {
                        channel.close();
                    } else if (bytesRead > 0) {
                        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        System.out.println("Get data length : " + bytesRead);
                    }
                }

                if (key.isValid() && key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    buffer.flip();

                    SocketChannel channel = (SocketChannel) key.channel();
                    channel.write(buffer);
                    if (!buffer.hasRemaining()) {
                        key.interestOps(SelectionKey.OP_READ);
                    }
                    buffer.compact();
                }
            }
        }


    }
}
