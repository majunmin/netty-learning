package com.mjm.chapter13;

import lombok.Data;

import java.net.InetSocketAddress;

/**
 * 消息组件 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-03 10:12
 * @since
 */
@Data
public class LogEvent {

    public static final byte SEPTERATOR = (byte)':';

    private final InetSocketAddress source;
    // 文件名
    private final String logFile;
    // 日志消息
    private final String msg;

    private final long received;

    public LogEvent(String logFile, String msg){
        this(null, -1, logFile, msg);
    }

    public LogEvent(InetSocketAddress source, long received, String logFile, String msg) {
        this.source = source;
        this.logFile = logFile;
        this.msg = msg;
        this.received = received;
    }
}
