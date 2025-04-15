import cn.hutool.core.util.IdUtil;
import com.kz.rpc.constant.RpcConstant;
import com.kz.rpc.model.RpcRequest;
import com.kz.rpc.protocol.*;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.junit.Test;


import java.io.IOException;

public class ProtocolMessageTest {
    @Test
    public void testMessage() throws IOException {
        // 构造消息
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = getHeader();

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("com.kz.rpc.service.HelloService");
        rpcRequest.setMethodName("hello");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setParameters(new Object[]{"world","aaa"});
        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);

        Buffer buffer = ProtocolMessageEncoder.encode(protocolMessage);
        ProtocolMessage<?> message = ProtocolMessageDecode.decode(buffer);
        Assert.assertNotNull(message);

    }

    private static ProtocolMessage.Header getHeader() {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JSON.getKey());
        header.setMessageType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setStatusCode((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);
        return header;
    }
}
