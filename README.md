

ZConsole是一个通过jmx管理jvm、排查jvm问题的web应用。
解决了应用没有开启jmx端口，无法远程连接，linux无法运行jconsole之类的应用查看jvm状况；

目前已经实现的功能：
1. 远程连接开启了jmx端口的jvm；
2. 连接与ZConsole部署在一台服务器上的jvm，无需开启jmx端口,对于没有开启jmx，不可以重启的应用，将ZConsole部署上去排查问题是非常方便的；
3. 实现了jconsole里面MBean标签的全部功能，可以查看Mbean属性，执行MBean方法，订阅通知；
4. jvm信息，操作系统信息查看；
5. 曲线图实时展示jvm各分区内存的最大值、已经申请的内存、已经使用的内存、以及初始内存情况；
6. java线程栈dump；

后续功能：
1. java线程栈锁分析，根据此功能，检查是否存在导致系统性能瓶颈的锁；
2. 健康检查，检查jvm当前状态，常见的参数设置，gc状态等
3. jstat命令


# 如何运行
## ZConsole配置

* ZConsole 是一个springboot项目，启动类为com.zqz.Bootstart；

启动后的访问地址http://ip:8080/jvm/list.html；

启动端口可以在src\main\resources\application.yml文件中配置；

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
