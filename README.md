

ZMBean是一个通过jmx远程管理jvm的web应用。

目前已经实现的功能：
1. 远程连接开启了jmx端口的jvm；
2. 连接与ZMBean部署在一台服务器上的jvm，无需开启jmx端口,对于没有开启jmx，还无法重启的应用，将ZMBean部署上去排查问题是非常方便的；
3. 实现了jconsole里面MBean标签的全部功能，可以查看Mbean属性，执行MBean方法，订阅通知
4. 曲线图实时展示jvm各分区内存的最大值、已经申请的内存、已经使用的内存、以及初始内存情况；


# 如何运行
## ZMBean配置

* 安装mysql数据库，执行sql脚本src\main\resources\zqz_jmx_jvm.sql创建数据库（为方便使用计划将mysql去掉，ZMBean不存储数据）；
* 创建好数据库后，修改配置文件application.yml中数据库的配置信息；
* ZMBean 是一个springboot项目，启动类为com.zqz.Bootstart；

## java服务器端配置
java启动时加上如下参数：
```
  -Djava.rmi.server.hostname=jvm虚拟机所在的服务器的ip 
  -Dcom.sun.management.jmxremote
  -Dcom.sun.management.jmxremote.port=8999
  -Dcom.sun.management.jmxremote.ssl=false
  -Dcom.sun.management.jmxremote.authenticate=false
```
  客户端连接url service:jmx:rmi:///jndi/rmi://localhost:8888/server