package com.yzy.codec;

import com.yzy.rpc.entity.RpcRequest;
import com.yzy.rpc.enumeration.PackageType;
import com.yzy.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author yzy
 * @version 1.0
 * @description 编码器
 * @date 2023/8/22 14:04
 */
public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 编码
     *
     * @param channelHandlerContext
     * @param msg
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
        // 魔数
        byteBuf.writeInt(MAGIC_NUMBER);
        // 数据包类型
        if (msg instanceof RpcRequest) {
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        // 序列化器
        byteBuf.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        // 数据长度
        byteBuf.writeInt(bytes.length);
        // 数据
        byteBuf.writeBytes(bytes);
    }
}
