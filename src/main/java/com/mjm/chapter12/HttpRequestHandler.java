package com.mjm.chapter12;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * 扩展  SimpleChannelInboundHandler 以处理 FullHttpRequest</br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-01 18:36
 * @since
 */
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public final String wsUri;

    public static final File INDEX;

    static {
//        String path = HttpRequestHandler.class
//                .getClassLoader()
//                .getResource("")
//                .getPath() + "index.html";
        String path = "/Users/majunmin/IdeaProjects/netty-learning/build/resources/main/index.html";
        path = path.contains("file:") ? path.substring(5) : path;
        INDEX = new File(path);
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        if (StringUtils.equalsIgnoreCase(wsUri, request.uri())) {
            // 1. 如果请求了 websocket 协议升级
            // 则增加引用计数(retain()) 并将它传递给下一个 ChannelHandler
            ctx.fireChannelRead(request.retain());
        } else {
            // 2. 处理 100Continue请求 以符合 http1.1 规范
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            // 读取 index.html
            RandomAccessFile raf = new RandomAccessFile(INDEX, "r");
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");

            boolean keepAlive = HttpUtil.isKeepAlive(request);

            if (keepAlive) {
                // 如果请求了 keep-alive, 则需要添加相应的 http头部
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, raf.length());
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            // 3. 将 HttpResponse 写到客户端
            ctx.write(response);
            // 4. 将 index.html 写到客户端
            if (ctx.pipeline().get(SslHandler.class) == null) {
                // 如果没有加密和压缩， 可以将 index.html 内容存储到 FileRegion 中来达到最佳效率 (zero-copy)
                ctx.write(new DefaultFileRegion(raf.getChannel(), 0, raf.length()));
            } else {
                ctx.write(new ChunkedNioFile(raf.getChannel()));
            }

            // 写 LastHttpContent(标记响应结束) 并冲刷至客户端, (结束时调用)
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            if (!keepAlive) {
                // 如果没有 keepAlive ,那么就在写操作完成后关闭 Channel
                future.addListener(ChannelFutureListener.CLOSE);
            }

        }
    }

    private void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(fullHttpResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
