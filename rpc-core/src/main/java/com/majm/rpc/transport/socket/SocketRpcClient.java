package com.majm.rpc.transport.socket;

import com.majm.rpc.dto.RpcRequest;
import com.majm.rpc.dto.RpcResponse;
import com.majm.rpc.exception.RpcException;
import com.majm.rpc.transport.ClientTransport;
import com.majm.rpc.utils.checker.RpcMessageChecker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 4:13 下午
 * @since
 */
@Slf4j
@AllArgsConstructor
public class SocketRpcClient implements ClientTransport {

    private String host;
    private int port;

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        try(Socket socket = new Socket(host, port)) {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            // 通过输出流发送到 服务端
            outputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // 从输入流中读取 RpcResponse
            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
            //校验 RpcResponse 和 RpcRequest
            RpcMessageChecker.check(rpcResponse, rpcRequest);
            return rpcResponse.getData();
        } catch (IOException | ClassNotFoundException e) {
            log.error("SocketRpcClient 服务调用失败");
            throw new RpcException("调用服务失败:", e);
        }
    }
}
