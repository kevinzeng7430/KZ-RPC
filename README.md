

Ran tool
看起来这是一个RPC (远程过程调用) 项目，让我探索核心组件来创建运行流程图。

Ran tool

Ran tool

Search files...

Search files...

Ran tool

Ran tool

Read file: consumer/src/main/java/com/kz/example/consumer/EasyConsumerExample.java

Ran tool

Read file: provider/src/main/java/com/kz/example/provider/UserServiceImpl.java

```bash
echo "Creating kzRPC workflow diagram..."
```
根据项目代码分析，这是一个简单的RPC框架实现，下面是运行流程图：

```
┌─────────────────┐                                             ┌─────────────────┐
│     消费者      │                                             │     提供者      │
│   Consumer      │                                             │   Provider      │
└────────┬────────┘                                             └────────┬────────┘
         │                                                               │
         │                                ┌─────────────────────────┐    │
         │                                │      注册中心           │    │
         │                                │    LocalRegistry        │    │
         │                                └───────────┬─────────────┘    │
         │                                            │                  │
         │                                            │ 1.注册服务       │
         │                                            │ register(service)│
         │                                            │◄─────────────────┘
         │                                            │
         │ 2.创建代理                                 │
         │ ServiceProxyFactory.getProxy()             │
         │                                            │
         │ 3.调用远程服务                             │
         │ userService.method()                       │
         │                                            │
         │ 4.代理拦截请求                             │
         │ ServiceProxy.invoke()                      │
         │                                            │
         │ 5.构造RPC请求                              │
         │ RpcRequest                                 │
         │                                            │
         │ 6.序列化请求                               │
         │ Serializer.serialize()                     │
         │                                            │
         │ 7.发送HTTP请求                             │
         │ HttpRequest.post()                         │
         │─────────────────────────────────────────────────────────┐
         │                                            │            │
         │                                            │            │
         │                                            │            │ 8.接收请求
         │                                            │            │ VertxHttpServer
         │                                            │            │
         │                                            │            │ 9.解析请求体
         │                                            │            │ HttpServerHandler
         │                                            │            │
         │                                            │            │ 10.反序列化请求
         │                                            │            │ Serializer.deserialize()
         │                                            │            │
         │                                            │ 11.查找服务│
         │                                            │ getService()│
         │                                            │◄───────────┘
         │                                            │
         │                                            │ 12.调用本地服务
         │                                            │ method.invoke()
         │                                            │────────────┐
         │                                            │            │
         │                                            │            │
         │                                            │            │
         │                                            │            │
         │                                            │            │ 13.执行业务逻辑
         │                                            │            │ UserServiceImpl
         │                                            │            │
         │                                            │            │ 14.返回结果
         │                                            │            │
         │                                            │            │ 15.构造RPC响应
         │                                            │            │ RpcResponse
         │                                            │            │
         │                                            │            │ 16.序列化响应
         │                                            │            │ Serializer.serialize()
         │                                            │            │
         │                                            │            │ 17.返回HTTP响应
         │ 18.接收HTTP响应                            │            │
         │ HttpResponse                               │            │
         │◄───────────────────────────────────────────────────────┘
         │
         │ 19.反序列化响应
         │ Serializer.deserialize()
         │
         │ 20.返回结果给调用者
         │
         ▼
```

kzRPC框架运行流程：

1. **服务提供方（Provider）**:
   - 实现服务接口
   - 将服务注册到本地注册中心(LocalRegistry)
   - 启动HTTP服务器(VertxHttpServer)监听请求

2. **服务消费方（Consumer）**:
   - 通过ServiceProxyFactory创建服务代理对象
   - 调用代理对象的方法
   - 代理对象拦截方法调用，创建RPC请求对象
   - 序列化请求并通过HTTP发送到服务提供方

3. **服务处理与响应**:
   - 服务提供方接收HTTP请求
   - 反序列化RPC请求
   - 从注册中心获取服务实现类
   - 通过反射调用目标方法
   - 将方法执行结果序列化后返回给消费方
   - 消费方反序列化响应并返回结果给调用者
