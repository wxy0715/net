# 内网穿透

## READEME

需要有一台公网服务器

配置文件提供了默认使用的端口,如果存在端口占用需要自行修改源码重新打包

## 基于socket版本的http内网穿透

### 使用的模块

![image-20240401111552429](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20240401111552429.png)

### jar包

![image-20240401130210823](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20240401130210823.png)

### 服务端

```yml
server:
  port: 19998 # 服务启动端口
http:
  # 和客户端连接socket端口
  port: 19999
  # 超时时间（单位毫秒）
  timeout: 1800000
```
**服务器启动命令:**
```
java -jar http-socket-server.jar --server.port=19998 --http.port=19999
```

![image-20240401124439185](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20240401124439185.png)

### 客户端

启动客户端

```properties
命令: java -jar http-socket-client.jar  服务端ip 服务端端口 内网程序ip 内网程序端口
示例:java -jar http-socket-client.jar 127.0.0.1 19999 127.0.0.1 8000
```

![image-20240401125915392](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20240401125915392.png)

访问客户端程序返回的代理地址

![image-20240401125843909](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20240401125843909.png)

## 基于netty版本http内网穿透-todo



## 基于netty版本的tcp内网穿透-todo



## 基于netty版本的udp内网穿透-todo

