package com.majm.rpc.transport;

import com.majm.rpc.dto.RpcRequest;
import com.majm.rpc.dto.RpcResponse;
import com.majm.rpc.transport.socket.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 2:32 下午
 * @since
 */
@Slf4j
public class SocketRpcRequestHandlerRunnable implements Runnable {

    private Socket socket;
    private static final RpcRequestHandler rpcRequestHandler;

    static {
        rpcRequestHandler = new RpcRequestHandler();
    }

    public SocketRpcRequestHandlerRunnable(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        log.info(String.format("server handle message from client by thread: %s", Thread.currentThread().getName()));
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            RpcRequest rpcRequest = (RpcRequest)objectInputStream.readObject();
            Object result = rpcRequestHandler.handle(rpcRequest);
            outputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            outputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("SocketRpcRequestHandlerRunnable occur exception , ", e.getMessage());
        }
    }
}
