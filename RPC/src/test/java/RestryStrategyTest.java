import com.kz.rpc.fault.NoRetryStrategy;
import com.kz.rpc.fault.RetryStrategy;
import com.kz.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Slf4j
public class RestryStrategyTest {

    RetryStrategy retryStrategy = new NoRetryStrategy();
    @Test
    public void doRetry(){
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("执行重试逻辑");
                throw new RuntimeException("模拟失败");
            });
        } catch (Exception e) {
            log.info("重试失败");
            e.printStackTrace();
        }
    }
}
