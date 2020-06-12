package com.majm.rpc.transport;

import com.majm.rpc.dto.RpcRequest;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 2:11 下午
 * @since
 */
public interface ClientTransport {

    /**
     * send reqeust to server
     *
     * @param rpcRequest
     * @return
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
