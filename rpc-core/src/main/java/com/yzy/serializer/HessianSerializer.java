package com.yzy.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.yzy.rpc.enumeration.SerializerCode;
import com.yzy.rpc.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author yzy
 * @version 1.0
 * @description hessian序列化
 * @date 2023/8/22 14:09
 */
public class HessianSerializer implements CommonSerializer {
    public static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);


    @Override
    public byte[] serialize(Object obj) {
        HessianOutput hessianOutput = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    logger.error("关闭流时有错误发生:", e);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try {
            new ByteArrayInputStream(bytes);
            hessianInput = new HessianInput(new ByteArrayInputStream(bytes));
            return hessianInput.readObject();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("反序列化时有错误发生");
        } finally {
            if (hessianInput != null) hessianInput.close();
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("HESSIAN").getCode();
    }
}
