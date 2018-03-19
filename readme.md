#### **简要说明：**

java 实现的android日志工具，可通过xml配置，可插拔式扩展，使用mmap ByteBuffer保证较高的写入性能同时app结束数据不会丢失，可选的snappy压缩选项

一些技术细节：

PersistenceMappedByteBuffer是使用MappedByteBuffer封装的具有进程被杀不会丢失内存数据功能的ByteBuffer，非常适合app作为ByteBuffer使用，使用MappedByteBuffer的头4个字节记录
buffer的position，每次更新buffer的数据都会更新头四个字节的position信息，对外部提供的接口和ByteBuffer相同，具体实现如图，细节参见源代码

![image](https://github.com/kingson09/bear/blob/dev_stable/buffer.png)

MappedBufferedOutputStream是使用PersistenceMappedByteBuffer作为buffer的缓冲字节输出流，api和BufferedOutputStream相同，由于使用PersistenceMappedByteBuffer所以同样具有app结束不会丢失数据的功能

MappedSnappyOutputStream是使用Snappy压缩算法的压缩输出流，为避免达到压缩块大小前app结束数据丢失，同样使用PersistenceMappedByteBuffer作为buffer

Disruptor是一个线程切换工具，由一个BlockingQeque、EventHandler和一个work thread构成，主线程向Disruptor publish Event，work thread阻塞在BlockingQeque等待Event到来，使用EventHandler处理此Event

MethodWeaver是一个使用Aspect实现的在方法前和方法后切入的注解

由于需要配合后台解析，因此目前只实现了json格式的日志，为了减少json格式化带来的性能开销，并没有使用DSL-Json等通用的json工具和SimpleDateFormat等时间格式化工具，而是使用StringBuilder进行手动格式化

#### 简单使用说明：

初始化：
日志代码真正实现是RecoderConfig（为了区分Log，所以没有使用Logger这个名词），AndroidLog是对日志内部实现的封装，暴露出公共api，也就是说日志初始化和打印的api，都在AndroidLog类里面。

初始化第一个参数是Context，用于获取资源，第二个Lookup对象需要app自己定义，为日志提供必要的app信息，lookup中的key就是日志xml配置文件中的“${appdir}”中的内容，配置好Lookup后，日志会自动从Lookup中获取花括号对应的key的实际内容
```
AndroidLog.init(bundleContext.getAPKContext().getApplicationContext(), new LogLookup());
```
日志打印：

调用AndroidLog的api即可，分为使用类名做tag的实例方法，和需要手动填写tag的静态方法
```
private AndroidLog Log = AndroidLog.getLogger(this.getClass());
Log.debug(msg); 
AndroidLog.debug(MainActivity.class.getSimpleName(), msg); 
```
更多的细节：

日志代码通过DSL形式进行插件化配置，对于日志的归档策略（包括过期时间，日志单个文件大小，总大小，是否使用buffer，是否开启压缩），都通过xml进行配置，其中是否使用buffer和启用压缩，都是默认为true，因此可以不必填写。
如果想对日志进行扩展，只需要实现对应的组件（Appender，strategy，header等等），通过bear基础库中的注解指定build方法，然后配置的xml文件即可

```
<?xml version="1.0" encoding="UTF-8"?>
<Recorders>
    <recorder name="logger">
        <LevelFilter name="logLevel" class="com.yishun.log.filter.LevelFilter" level="TRACE" onMatch="ACCEPT"
                     onMismatch="DENY">
        </LevelFilter>
        <Appenders name="logcat" class="com.yishun.log.appender.LogcatAppender">
            <LevelFilter name="logcatLevel" class="com.yishun.log.filter.LevelFilter" level="OFF" onMatch="ACCEPT"
                         onMismatch="DENY">
            </LevelFilter>
        </Appenders>
        <Appenders name="file" archiveDir="${appdir}/archive" async="true"
                   class="com.yishun.log.appender.archive.RollingFileAppender"
                   fileName="${appdir}/app.log">
            <LevelFilter name="fileLevel" class="com.yishun.log.filter.LevelFilter" level="TRACE" onMatch="ACCEPT"
                         onMismatch="DENY"/>
            <Formatter name="formatter" class="com.yishun.log.format.DefaultLogFormatter"/>
            <Header name="header" class="com.yishun.log.format.LogfileHeader">
                <header>
                    {"device":"${device}","channel":"${channel}","uid":"${uid}","product":"${product}","platform":"${platform}","logLevel":"${logLevel}","appCode":"${appCode}"}
                </header>
            </Header>
            <Strategy name="timeAndSizeStrategy"
                      class="com.yishun.log.appender.archive.strategy.TimeAndSizeBasedArchiveAndRolloverStrategy"
                      expire="7d" interval="1d" maxSize="300MB" size="3MB"/>
        </Appenders>
    </recorder>
</Recorders>
```