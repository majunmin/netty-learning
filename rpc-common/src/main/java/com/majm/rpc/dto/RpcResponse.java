package com.majm.rpc.dto;

import com.majm.rpc.enums.RpcResponseCode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 11:36 上午
 * @since
 */
@Accessors(chain = true)
@Data
public class RpcResponse<T> implements Serializable {

    private String requestId;
    private Integer code;
    private String msg;
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId).setData(data).setCode(200);
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcResponseCode rpcResponseCode) {
        return fail(rpcResponseCode.getMessage(), rpcResponseCode.getCode());
    }

    public static <T> RpcResponse<T> fail(String msg, Integer code) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setMsg(msg).setCode(code);
        return response;
    }
}
