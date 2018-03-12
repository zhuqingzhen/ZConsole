

ZConsole是一个通过jmx管理jvm、排查jvm问题的web应用。
还解决了应用没有开启jmx端口，linux无法运行jconsole之类的应用时，通过将本应用与要监控的应用部署到同一台服务器上，即可远程对jvm进行管理；

演示地址：http://118.24.47.200:8080/

目前已经实现的功能：
1. 远程连接开启了jmx端口的jvm；
2. 连接与ZConsole部署在一台服务器上的jvm，无需开启jmx端口,对于没有开启jmx，不可以重启的应用，将ZConsole部署上去排查问题是非常方便的；
3. MBean树查看器，实现了jconsole里面MBean标签功能，可以查看Mbean属性，执行MBean方法，订阅通知；
4. 查看jvm信息，操作系统信息，诊断参数的配置；
5. 实时曲线图展示操作系统cpu使用率、load值（需要操作系统和jdk支持）、被监控应用cpu使用率、线程数统计、直接内存的使用状况；
6. 实时曲线图展示jvm各分区内存的最大值、已经申请的内存、已经使用的内存、以及初始内存情况、gc次数、gc耗时、最近一分钟gc次数；
7. java线程栈dump，java线程栈锁分析，可以列出所有的锁，持有锁的线程和等待锁的线程，辅助查找锁导致的性能问题；
8. class直方图导出功能，用来辅助排查内存泄露，大对象导致的内存问题；
9. jstat命令（桑健实现，当前只支持本地jvm使用此命令);

后续功能：

 1.健康检查，检查jvm当前状态，常见的参数设置，gc状态等；
 2.jfr功能，诊断系统性能瓶颈；

目前只是在做功能，没有对ui和交互做更好的体验，一些请求容错处理也没有做，遇到问题刷新页面重新请求，把功能完善以后，会做这方面的优化；

# 如何运行
## ZConsole配置

* ZConsole 是一个springboot项目，启动类为com.zqz.Bootstart；

启动后的访问地址http://ip:8080

启动端口可以在src\main\resources\application.yml文件中配置；

运行环境：
jdk1.8，依赖jdk的lib目录下的tool.jar；

详情见/doc/publish/readme文件说明；

## java服务器端配置
远程连接jvm，java启动时加上如下参数：
```
  -Djava.rmi.server.hostname=jvm虚拟机所在的服务器的ip 
  -Dcom.sun.management.jmxremote
  -Dcom.sun.management.jmxremote.port=8999
  -Dcom.sun.management.jmxremote.ssl=false
  -Dcom.sun.management.jmxremote.authenticate=false
```
连接与ZConsole部署在同一台服务器上的jvm，无需做任何配置；






  客户端连接url service:jmx:rmi:///jndi/rmi://localhost:8888/server
