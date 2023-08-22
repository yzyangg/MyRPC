package com.yzy.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.protostuff.runtime.RuntimeSchema.getSchema;

/**
 * @author yzy
 * @version 1.0
 * @description TODO
 * @date 2023/8/22 14:10
 */
public class ProtobufSerializer implements CommonSerializer {

    private LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    private Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();


    @Override
    public byte[] serialize(Object obj) {
        Class clazz = obj.getClass();
        Schema schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Schema schema = getSchema(clazz);
        Object o = schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes, o, schema);
        return o;
    }

    @Override
    public int getCode() {
        return 0;
    }
}
