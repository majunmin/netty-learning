package com.majm.rpc.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/11 9:52 下午
 * @since
 */
public class SingletonFactory {

    private static final Map<String, Object> OBJECT_MAP = new HashMap<>();

    public SingletonFactory() {
    }

    public static <T> T getSingleton(Class<T> clazz){
        String key = clazz.toString();
        Object obj = OBJECT_MAP.get(key);
        synchronized (clazz){
            if (Objects.isNull(obj)){
                try {
                    obj = clazz.getDeclaredConstructor().newInstance();
                    OBJECT_MAP.put(key, obj);
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        return clazz.cast(obj);
    }
}
