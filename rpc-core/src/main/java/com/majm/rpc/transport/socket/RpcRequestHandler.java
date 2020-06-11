package com.majm.rpc.transport.socket;

import com.majm.rpc.dto.RpcRequest;
import com.majm.rpc.dto.RpcResponse;
import com.majm.rpc.enums.RpcResponseCode;
import com.majm.rpc.provider.ServiceProviderImpl;
import com.majm.rpc.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 3:37 下午
 * @since
 */
@Slf4j
public class RpcRequestHandler {

    private static final ServiceProvider SERVICE_PROVIDER;

    static {
        SERVICE_PROVIDER = new ServiceProviderImpl();
    }

    /**
     * 处理 rpcRequest 然后返回方法执行结果
     */
    public Object handle(RpcRequest rpcRequest) {
        Object result = null;
        // 通过注册中心获取到目标类（客户端需要调用类）
        Object service = SERVICE_PROVIDER.getServiceProvider(rpcRequest.getInterfaceName());
        try {
            result = invokeTargetMethod(rpcRequest, service);
            log.info("service:{} successful invoke method:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("occur exception", e);
        }
        return result;
    }

    /**
     * 根据 rpcRequest 和 service 对象特定的方法并返回结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        if (null == method) {
            return RpcResponse.fail(RpcResponseCode.NOT_FOUND_METHOD);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
