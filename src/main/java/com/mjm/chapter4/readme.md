# 传输


ChannelPipeline 持有所有将应用于入站和出站数据以及事件的 ChannelHandler 实 例，这些 ChannelHandler 实现了应用程序用于处理状态变化以及数据处理的逻辑。
ChannelHandler 的典型用途包括: 
- 将数据从一种格式转换为另一种格式;
- 提供异常的通知;
- 提供 Channel 变为活动的或者非活动的通知;
- 提供当 Channel 注册到 EventLoop 或者从 EventLoop 注销时的通知;
- 提供有关用户自定义事件的通知。
> 拦截过滤器 `ChannelPipeline` 实现了一种常见的设计模式`拦截过滤器(Intercepting Filter)`。
> UNIX 管道是另外一个熟悉的例子:多个命令被链接在一起，其中一个命令的输出端将连 接到命令行中下一个命令的输入端。

Netty 的 Channel 实现是线程安全的，因此你可以存储一个到 Channel 的引用，可以在多线程都在使用它

