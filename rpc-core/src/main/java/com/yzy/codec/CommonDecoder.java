package com.yzy.codec;

import com.yzy.rpc.entity.RpcRequest;
import com.yzy.rpc.entity.RpcResponse;
import com.yzy.rpc.enumeration.PackageType;
import com.yzy.rpc.enumeration.RpcError;
import com.yzy.rpc.exception.RpcException;
import com.yzy.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author yzy
 * @version 1.0
 * @description 解码器
 * @date 2023/8/22 14:04
 */
public class CommonDecoder extends ReplayingDecoder<Object> {
    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;


    /**
     * 解码
     *
     * @param ctx
     * @param byteBuf
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        int magic = byteBuf.readInt();
        if (magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包: {}", magic);
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }

        // 数据包类型
        int packageCode = byteBuf.readInt();

        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        // 序列化器code
        int serializerCode = byteBuf.readInt();

        // 获得序列化器
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        // 数据长度
        int length = byteBuf.readInt();


        byte[] bytes = new byte[length];

        // 数据
        byteBuf.readBytes(bytes);

        Object obj = serializer.deserialize(bytes, packageClass);

        // 将解码后的对象放入out中，传递给下一个handler处理
        out.add(obj);
    }
}
