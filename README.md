# RPC框架流程图

## 1. 整体架构图

```mermaid
graph TD
    A[服务提供者] --> B[服务注册中心]
    C[服务消费者] --> B
    A --> D[Netty服务端]
    C --> E[Netty客户端]
    D <--> E
```

## 2. 服务注册流程

```mermaid
sequenceDiagram
    participant Provider as 服务提供者
    participant Registry as 注册中心
    participant NettyServer as Netty服务端
    
    Provider->>NettyServer: 1. 启动Netty服务端
    NettyServer-->>Provider: 2. 服务端启动成功
    Provider->>Registry: 3. 注册服务信息
    Registry-->>Provider: 4. 注册成功
```

## 3. 服务发现流程

```mermaid
sequenceDiagram
    participant Consumer as 服务消费者
    participant Registry as 注册中心
    participant NettyClient as Netty客户端
    
    Consumer->>Registry: 1. 查询服务地址
    Registry-->>Consumer: 2. 返回服务地址列表
    Consumer->>NettyClient: 3. 创建客户端连接
    NettyClient-->>Consumer: 4. 连接建立成功
```

## 4. 远程调用流程

```mermaid
sequenceDiagram
    participant Consumer as 服务消费者
    participant Proxy as 动态代理
    participant NettyClient as Netty客户端
    participant NettyServer as Netty服务端
    participant Provider as 服务提供者
    
    Consumer->>Proxy: 1. 调用代理方法
    Proxy->>NettyClient: 2. 发送RPC请求
    NettyClient->>NettyServer: 3. 网络传输
    NettyServer->>Provider: 4. 调用实际服务
    Provider-->>NettyServer: 5. 返回结果
    NettyServer-->>NettyClient: 6. 网络传输
    NettyClient-->>Proxy: 7. 接收响应
    Proxy-->>Consumer: 8. 返回结果
```

## 5. 组件交互图

```mermaid
graph LR
    subgraph 服务提供者
        A1[服务实现类]
        A2[Netty服务端]
        A3[服务注册]
    end
    
    subgraph 注册中心
        B1[Zookeeper]
    end
    
    subgraph 服务消费者
        C1[动态代理]
        C2[Netty客户端]
        C3[服务发现]
    end
    
    A1 --> A2
    A3 --> B1
    C3 --> B1
    C1 --> C2
    A2 <--> C2
```

## 6. 异常处理流程

```mermaid
graph TD
    A[远程调用] --> B{是否成功}
    B -->|是| C[返回结果]
    B -->|否| D{错误类型}
    D -->|网络错误| E[重试机制]
    D -->|服务错误| F[异常转换]
    D -->|超时| G[超时处理]
    E --> H[返回错误]
    F --> H
    G --> H
```

## 7. 负载均衡流程

```mermaid
graph TD
    A[服务发现] --> B[获取服务列表]
    B --> C{负载均衡策略}
    C -->|随机| D[随机选择]
    C -->|轮询| E[轮询选择]
    C -->|权重| F[权重选择]
    D --> G[选择服务实例]
    E --> G
    F --> G
    G --> H[建立连接]
```

## 8. 序列化流程

```mermaid
sequenceDiagram
    participant Client as 客户端
    participant Serializer as 序列化器
    participant Network as 网络传输
    participant Deserializer as 反序列化器
    participant Server as 服务端
    
    Client->>Serializer: 1. 序列化请求
    Serializer->>Network: 2. 发送字节流
    Network->>Deserializer: 3. 接收字节流
    Deserializer->>Server: 4. 反序列化请求
    Server-->>Deserializer: 5. 序列化响应
    Deserializer-->>Network: 6. 发送字节流
    Network-->>Serializer: 7. 接收字节流
    Serializer-->>Client: 8. 反序列化响应
```

这些流程图展示了RPC框架的各个关键流程，包括：

1. 整体架构
2. 服务注册
3. 服务发现
4. 远程调用
5. 组件交互
6. 异常处理
7. 负载均衡
8. 序列化过程

通过这些流程图，可以更直观地理解RPC框架的工作原理和各个组件之间的交互关系。 
