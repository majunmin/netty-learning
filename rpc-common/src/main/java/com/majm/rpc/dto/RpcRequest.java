package com.majm.rpc.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 11:36 上午
 * @since
 */
@Data
@Accessors(chain = true)
public class RpcRequest implements Serializable {

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;

}
