package com.majm.rpc.serialize;

import java.io.Serializable;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 12:05 下午
 * @since
 */
public interface Serializer {

    byte[] seralize(Object obj);


    <T> T deSeralize(byte[] bytes, Class<T> clazz);
}
