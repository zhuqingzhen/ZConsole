nohup  java -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+CMSConcurrentMTEnabled -XX:CMSInitiatingOccupancyFraction=75 -XX:+ExplicitGCInvokesConcurrent -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+PrintGCDetails -XX:+PrintGCCause -XX:+PrintGCTimeStamps -Xloggc:logs/gc.log -XX:-DisableExplicitGC  -XX:+PrintCommandLineFlags -Djava.rmi.server.hostname=172.27.0.6 -Dcom.sun.management.jmxremote.port=8081 -Dcom.sun.management.jmxremote   -Dcom.sun.management.jmxremote.ssl=false  -Dcom.sun.management.jmxremote.authenticate=false  -Djava.ext.dirs=./lib  -jar zconsole-0.0.1-SNAPSHOT.jar &
