/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network.impl.netty.codec;

import com.adomy.mirpc.core.serialize.MiRpcSerializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Netty服务编码器
 * 
 * @author adomyzhao
 * @version $Id: NettyEncoder.java, v 0.1 2021年03月23日 1:08 PM adomyzhao Exp $
 */
public class NettyEncoder extends MessageToByteEncoder<Object> {

    /**
     * 泛化类
     */
    private Class<?>        genericClass;

    /**
     * 序列化处理器
     */
    private MiRpcSerializer serializer;

    /**
     * 构造函数
     * 
     * @param genericClass
     * @param serializer
     */
    public NettyEncoder(Class<?> genericClass, MiRpcSerializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    /**
     * 编码处理
     * 
     * @param channelHandlerContext
     * @param o
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o,
                          ByteBuf byteBuf) throws Exception {

        // 第一步：判断对象是否是预期的类对象
        if (!genericClass.isInstance(o)) {
            return;
        }

        // 第二步：对对象进行序列化处理
        byte[] bytes = serializer.serialize(o);

        // 第三步：写入字节长度和数据内容
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}