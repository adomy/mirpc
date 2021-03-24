/**
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.adomy.mirpc.core.remoting.network.impl.netty.codec;

import java.util.List;

import com.adomy.mirpc.core.serialize.MiRpcSerializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Netty服务解码器
 * 
 * @author adomyzhao
 * @version $Id: NettyDecoder.java, v 0.1 2021年03月23日 1:08 PM adomyzhao Exp $
 */
public class NettyDecoder extends ByteToMessageDecoder {

    /**
     * INT数据的字节数
     */
    private static final int INT_BYTE_CNT = 4;

    /**
     * 泛化累
     */
    private Class<?>         genericClass;

    /**
     * 序列化处理器
     */
    private MiRpcSerializer  serializer;

    /**
     * 构造函数
     * 
     * @param genericClass
     * @param serializer
     */
    public NettyDecoder(Class<?> genericClass, MiRpcSerializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    /**
     * 解码处理
     *
     * ByteBuf是由一段地址空间、一个reader index和一个writer index组成。
     * 两个index分别记录读写进度，省去了NIO中ByteBuffer手动调用flip和clear的烦恼。
     *
     *       +-------------------+------------------+------------------+
     *       | discardable bytes |  readable bytes  |  writable bytes  |
     *       |                   |     (CONTENT)    |                  |
     *       +-------------------+------------------+------------------+
     *       |                   |                  |                  |
     *       0      <=      readerIndex   <=   writerIndex    <=    capacity
     *
     * -- writer index到capacity之间的部分是空闲区域，可以写入数据；
     * -- reader index到writer index之间是已经写过还未读取的可读数据；
     * -- 0到reader index是已读过可以释放的区域。
     *
     * readableBytes:
     * - 返回表示 ByteBuf 当前可读取的字节数，它的值等于 writerIndex - readerIndex
     * 
     * markReaderIndex和resetReaderIndex是一个成对的操作。
     * - markReaderIndex可以打一个标记
     * - 调用resetReaderIndex可以把readerIndex重置到原来打标记的位置
     *
     * @param channelHandlerContext
     * @param byteBuf
     * @param list
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
                          List<Object> list) throws Exception {
        // 第一步：判定字节大小是否符合预期
        if (byteBuf.readableBytes() < INT_BYTE_CNT) {
            return;
        }

        // 第二步：在当前位置打上"已读"索引
        byteBuf.markReaderIndex();

        // 第三步：读取内容长度信息
        int dataLen = byteBuf.readInt();

        // 第四步：如果内容长度小于0，则表示有错，于是关闭连接
        if (dataLen < 0) {
            channelHandlerContext.close();
            return;
        }

        // 第五步：如果当前可读数据长度小于内容长度，则表示数据还没有完全到达，这时需要再等等数据传送
        // 因此，将ByteBuf的readerIndex重置为刚才打标的位置，等待下次触发时，确保不会被这次操作丢弃掉部分数据
        if (byteBuf.readableBytes() < dataLen) {
            byteBuf.resetReaderIndex();
            return;
        }

        // 第六步：数据到齐后，将ByteBuf中的数据，读取并填入到bytes数组中，填满为止
        byte[] bytes = new byte[dataLen];
        byteBuf.readBytes(bytes);

        // 第七步：调用序列化工具，进行反序列化处理
        Object obj = serializer.deserialize(bytes, genericClass);
        list.add(obj);
    }
}