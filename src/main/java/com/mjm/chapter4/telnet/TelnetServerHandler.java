package com.mjm.chapter4.telnet;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.time.LocalDateTime;

/**
 * @author majunmin
 * @description
 * @datetime 2020/5/29 3:41 下午
 * @since
 */
public class TelnetServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        String msg = "Welcome ".concat(ctx.channel().remoteAddress().toString()).concat(" connect!!! \r\n")
                .concat("Lets talk! \r\n");
        ctx.writeAndFlush(msg);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean close = false;
        String response = "";
        String request =  (String)msg;
        if ("bye".equals(request)){
            close = true;
            response = "wish you have a good day!";
        } else if("time".equals(request)){
            response = "time: " + LocalDateTime.now();
        } else if("".equals(request)){
            response = "dont shy, say something!";
        } else {
            response = "did you say '" + request + "' ?";
        }
        ChannelFuture future = ctx.write(response  + "\r\n");

        if (close){
            future.addListener(ChannelFutureListener.CLOSE);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("exception caught, " + cause.getMessage());
        ctx.close();
    }
}
