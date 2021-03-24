package com.adomy.mirpc.core.serialize.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.adomy.mirpc.core.serialize.MiRpcSerializer;
import com.adomy.mirpc.core.util.except.MiRpcException;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

/**
 * Hessian序列化器
 * 
 * @author adomyzhao 2015-9-26 02:53:29
 */
public class HessianMiRpcSerializer implements MiRpcSerializer {

    /**
     * 序列化处理
     * 
     * @param obj
     * @param <T>
     * @return
     */
    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(os);
        try {
            ho.writeObject(obj);
            ho.flush();
            return os.toByteArray();
        } catch (Exception e) {
            throw new MiRpcException(e);
        } finally {
            try {
                ho.close();
            } catch (IOException e) {
                throw new MiRpcException(e);
            }

            try {
                os.close();
            } catch (IOException e) {
                throw new MiRpcException(e);
            }
        }
    }

    /**
     * 反序列化处理
     * 
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Hessian2Input hi = new Hessian2Input(is);
        try {
            return hi.readObject();
        } catch (IOException e) {
            throw new MiRpcException(e);
        } finally {
            try {
                hi.close();
            } catch (Exception e) {
                throw new MiRpcException(e);
            }

            try {
                is.close();
            } catch (IOException e) {
                throw new MiRpcException(e);
            }
        }
    }
}
