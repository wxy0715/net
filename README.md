# 内网穿透

## READEME

需要有一台公网服务器

## 基于socket版本的http内网穿透

### 使用的模块

![image-20240401111552429](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20240401111552429.png)

### 服务端

```yml
server:
  port: 9090 # 服务启动端口
http:
  # 和客户端连接socket端口
  port: 19999
  # 超时时间（单位毫秒）
  timeout: 1800000
```

**启动方式:** 下载源码 进行mvn clean instal 编译后文件http-socket-server.jar上传到linux服务器(也可以本地启动测试)

**服务器启动命令:** java -jar http-socket-server.jar

![image-20240401112530552](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20240401112530552.png)

### 客户端

```properties
# 服务端ip
serverIp=127.0.0.1
# 服务端端口
serverPort=19999
# 内网程序ip
localIp=127.0.0.1
# 内网程序端口
localPort=8000
```

启动客户端

![image-20240401112726615](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20240401112726615.png)

访问代理端口

![image-20240401123548421](https://wxy-md.oss-cn-shanghai.aliyuncs.com/image-20240401123548421.png)

## 基于netty版本http内网穿透-todo