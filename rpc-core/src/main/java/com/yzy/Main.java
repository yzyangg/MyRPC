package com.yzy;

import com.yzy.rpc.entity.RpcRequest;
import com.yzy.serializer.HessianSerializer;
import com.yzy.serializer.JsonSerializer;
import com.yzy.serializer.KryoSerializer;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
//        testSerializer();
    }

    private static void testSerializer() {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId("123");
        rpcRequest.setInterfaceName("com.yzy.HelloService");
        rpcRequest.setMethodName("hello");
        rpcRequest.setParameters(new Object[]{"yzy"});
        rpcRequest.setParamTypes(new Class[]{String.class});
        rpcRequest.setHeartBeat(false);
        HessianSerializer hessianSerializer = new HessianSerializer();
        System.out.println("------------------hessian------------------");
        byte[] serialize = hessianSerializer.serialize(rpcRequest);
        System.out.println(Arrays.toString(serialize));
        RpcRequest deserialize = (RpcRequest) hessianSerializer.deserialize(serialize, RpcRequest.class);
        System.out.println(deserialize != null);
        if (deserialize != null) {
            System.out.println(Arrays.toString(deserialize.getParameters()));
        }
        System.out.println("------------------json------------------");
        JsonSerializer jsonSerializer = new JsonSerializer();
        byte[] serialize1 = jsonSerializer.serialize(rpcRequest);
        System.out.println(Arrays.toString(serialize1));
        RpcRequest deserialize1 = (RpcRequest) jsonSerializer.deserialize(serialize1, RpcRequest.class);
        System.out.println(deserialize1 != null);
        if (deserialize1 != null) {
            System.out.println(Arrays.toString(deserialize1.getParameters()));
        }


        System.out.println("------------------kryo------------------");
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] serialize2 = kryoSerializer.serialize(rpcRequest);
        System.out.println(Arrays.toString(serialize2));
        RpcRequest deserialize2 = (RpcRequest) kryoSerializer.deserialize(serialize2, RpcRequest.class);
        System.out.println(deserialize2 != null);
        if (deserialize2 != null) {
            System.out.println(Arrays.toString(deserialize2.getParameters()));
        }

        System.out.println("------------------protobuf------------------");
        byte[] serialize3 = kryoSerializer.serialize(rpcRequest);
        System.out.println(Arrays.toString(serialize3));
        RpcRequest deserialize3 = (RpcRequest) kryoSerializer.deserialize(serialize3, RpcRequest.class);
        System.out.println(deserialize3 != null);
        if (deserialize3 != null) {
            System.out.println(Arrays.toString(deserialize3.getParameters()));
        }
    }
}