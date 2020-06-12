## Netty 组件

### Channel

> Socket
> Netty网络通信的组件，能够用于执行网络I/O操作。
>
> Channel为用户提供：
> 1. 当前网络连接的通道的状态（例如是否打开？是否已连接？）
> 2. 网络连接的配置参数 （例如接收缓冲区大小）
> 3. 提供异步的网络I/O操作(如建立连接，读写，绑定端口)，异步调用意味着任何I / O调用都将立即返回，并且不保证在调用结束时所请求的I / O操作已完成。调用立即返回一个ChannelFuture实例，通过注册监听器到ChannelFuture上，可以I / O操作成功、失败或取消时回调通知调用方。
> 4. 支持关联I/O操作与对应的处理程序
>

- NioSocketChannel，异步的客户端 TCP Socket 连接
- NioServerSocketChannel，异步的服务器端 TCP Socket 连接
- NioDatagramChannel，异步的 UDP 连接
- NioSctpChannel，异步的客户端 Sctp 连接
- NioSctpServerChannel，异步的 Sctp 服务器端连接

这些通道涵盖了 UDP 和 TCP网络 IO以及文件 IO.

### Selector

Netty基于Selector对象实现I/O多路复用，通过 Selector, 一个线程可以监听多个连接的Channel事件, 当向一个Selector中注册Channel 后, Selector 内部的机制就可以自动不断地查询(
select) 这些注册的Channel是否有已就绪的I/O事件(例如可读, 可写, 网络连接完成等)， 这样程序就可以很简单地使用一个线程高效地管理多个 Channel.

![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210604203458.png)

### EventLoop & NioEventLoopGroup

#### 1. EventLoop

控制流 多线程

NioEventLoop中维护了一个线程和任务队列,支持异步提交执行任务,线程启动时会调用`NioEventLoop#run()`, 执行I/O任务和非I/O任务:

I/O任务 : 即selectionKey中ready的事件，如 `accept`、`connect`、`read`、`write`等，由`processSelectedKeys()`方法触发 非IO任务 :
添加到taskQueue中的任务，如`register0()`、`bind0()`等任务，由`runAllTasks()`方法触发。

两种任务的执行时间比由变量`ioRatio`控制: 默认为50，则表示允许非IO任务执行的时间与IO任务的执行时间相等。

#### 2. NioEventLoopGroup

`NioEventLoopGroup`，主要管理eventLoop的生命周期, 可以理解为一个线程池,内部维护了一组线程, 每个线程(NioEventLoop)负责处理多个Channel上的事件, 而一个Channel只对应于一个线程

1. 一个EventLoopGroup 对应多个 EventLoop
2. 一个EventLoop 在其生命周期内只会与一个 Thread 绑定
3. 所有的 EventLoop 处理的IO事件 都将它注册在专有的Thread 上处理
4. 一个Channel 在其生命周期内只注册于一个 EventLoop
5. 一个 EventLoop 可能回报被分配给 一或多个Channel

> 这样的设计 一个给定的 Channel 的IO操作都是有相同的 Thread执行,消除了对同步的需要

### ChannelFuture

异步通知

### ChannelHandler & ChannelHandlerContext & ChannelPipeline

#### 1. ChannelHandler

ChannelHandler 是一个 接口,处理IO事件 和 拦截 IO操作,并将其转发到`ChannelPipeline`(业务处理链)中下一个处理程序

`ChannelInboundHandler` :用于处理入站I / O事件
`ChannelOutboundHandler`:用于处理出站I / O操作

或者使用以下适配器类：

`ChannelInboundHandlerAdapter` : 用于处理入站I / O事件
`ChannelOutboundHandlerAdapter`: 用于处理出站I / O操作
`ChannelDuplexHandler`          :用于处理入站和出站事件

#### 2. ChannelHandlerContext

保存Channel相关上下文信息,同时关联一个 ChannelHandler 对象

#### 3. ChannelPipeline

保存`ChannelHandler`的List，用于处理或拦截Channel的入站事件和出站操作. ChannelPipeline
实现了一种高级形式的拦截过滤器模式,使用户可以完全控制事件的处理方式,以及Channel中各个的`ChannelHandler`如何相互交互

```
                                                 I/O Request
                                            via Channel or
                                        ChannelHandlerContext
                                                      |
  +---------------------------------------------------+---------------+
  |                           ChannelPipeline         |               |
  |                                                  \|/              |
  |    +---------------------+            +-----------+----------+    |
  |    | Inbound Handler  N  |            | Outbound Handler  1  |    |
  |    +----------+----------+            +-----------+----------+    |
  |              /|\                                  |               |
  |               |                                  \|/              |
  |    +----------+----------+            +-----------+----------+    |
  |    | Inbound Handler N-1 |            | Outbound Handler  2  |    |
  |    +----------+----------+            +-----------+----------+    |
  |              /|\                                  .               |
  |               .                                   .               |
  | ChannelHandlerContext.fireIN_EVT() ChannelHandlerContext.OUT_EVT()|
  |        [ method call]                       [method call]         |
  |               .                                   .               |
  |               .                                  \|/              |
  |    +----------+----------+            +-----------+----------+    |
  |    | Inbound Handler  2  |            | Outbound Handler M-1 |    |
  |    +----------+----------+            +-----------+----------+    |
  |              /|\                                  |               |
  |               |                                  \|/              |
  |    +----------+----------+            +-----------+----------+    |
  |    | Inbound Handler  1  |            | Outbound Handler  M  |    |
  |    +----------+----------+            +-----------+----------+    |
  |              /|\                                  |               |
  +---------------+-----------------------------------+---------------+
                  |                                  \|/
  +---------------+-----------------------------------+---------------+
  |               |                                   |               |
  |       [ Socket.read() ]                    [ Socket.write() ]     |
  |                                                                   |
  |  Netty Internal I/O Threads (Transport Implementation)            |
  +-------------------------------------------------------------------+
```

Netty中 每个Channel 都有一个 ChannelPipeline 与之对应,组成的关系如下:

![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210605215834.png)

1. 一个Channel包含了一个 ChannelPipeline
2. 一个ChannelPipeline 维护了一个ChannelHandlerContext 组成的双向链表,
3. 一个ChannelHandlerContext 中关联着一个 ChannelHandler

> 入站事件和出站事件在一个双向链表中,入站事件会从链表head往后传递到最后一个入站的handler,
> 出站事件会从链表tail往前传递到最前一个出站的handler,两种类型的handler互不干扰


### Decoder  & Encoder




### BootStrap










