import cn.hutool.json.JSONObject;
import com.kz.rpc.config.RegistryConfig;
import com.kz.rpc.model.ServiceMetaInfo;
import com.kz.rpc.registry.EtcdRegistry;
import com.kz.rpc.registry.Registry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

public class RegistryTest {
    final Registry registry = new EtcdRegistry();

    @Before
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }

//    @Test
//    public void testGetKey() throws Exception {
//        String serviceName = "com.kz.example.common.service.UserService";
//        String key = registry.getKey(serviceName);
//        JSONObject jsonObject = new JSONObject(key);
//        String serviceKeyName = jsonObject.getStr("serviceName");
//        System.out.println("key = " + serviceKeyName);
//    }

}





