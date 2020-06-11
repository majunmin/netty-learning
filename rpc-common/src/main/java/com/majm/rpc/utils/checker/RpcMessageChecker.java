package com.majm.rpc.utils.checker;


import com.majm.rpc.dto.RpcRequest;
import com.majm.rpc.dto.RpcResponse;
import com.majm.rpc.enums.RpcErrorMessageEnum;
import com.majm.rpc.enums.RpcResponseCode;
import com.majm.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shuang.kou
 * @createTime 2020年05月26日 18:03:00
 */
public class RpcMessageChecker {
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);
    private static final String INTERFACE_NAME = "interfaceName";

    private RpcMessageChecker() {
    }

    public static void check(RpcResponse rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            logger.error("调用服务失败,rpcResponse 为 null,serviceName:{}", rpcRequest.getInterfaceName());
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            logger.error("调用服务失败,rpcRequest 和 rpcResponse 对应不上,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
