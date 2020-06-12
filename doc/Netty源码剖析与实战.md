## netty

为什么 netty 有多种 nio实现?

| COMMON | LINUX | Mac OS | 
| ---- | ---- | ---- | 
|NioEventLoopGroup        |EpollEventLoopGroup         |KqueueEventLoopGroup|
|NioEventLoop             |EpollEventLoop              |KqueueEventLoop|
|NioSocketChannel         |EpollSocketChannel          |KQueueSocketChannel    |
|NioServerSocketChannel   |EpollServerSocketChannel    |KQueueServerSocketChannel|

- netty 暴露了更多的 可控参数,例如
    * JDK 的nio默认实现 是 水平触发
    * Netty 边缘触发(default) 水平触发可切换
- Netty 的实现 的垃圾回收更少,性能更好

> Nio 一定 优于 BIO 吗？
> 不见得, 特定场景下,连接数少,并发度不高,BIO性能不输NIO
>

### Reactor 及其 三种版本

`Reactor 单线程模式`:   接收请求 和 处理请求都在一个线程完成
`Reactor 多线程模式`:   接收请求 和 处理请求 都在多线程完成
`Reactor 主从多线程模式`: 拆分 接收请求 和 处理请求两个动作,分别 有 两个线程池 完成

Reactor是一种开发模式,模式的核心流程:
注册感兴趣的事件 -->  扫描是否有感兴趣的事件发生 ---> 事件发生后做出相应的处理

| Client/Server | SocketChannel/ServerSocketChannel | OP_ACCEPT | OP_CONNECT | OP_READ |  OP_WRITE |
| ---- | ---- | ---- | ---- | ---- |  ---- |
| client | SocketChannel |   | Y | Y |   |
| server | ServerSocketChannel | Y |  |   |    |
| server | SocketChannel |  |  | Y |  Y |

https://www.open-open.com/lib/view/open1522308192318.html
https://juejin.cn/post/6844903636422623240

![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210605004844.png)



