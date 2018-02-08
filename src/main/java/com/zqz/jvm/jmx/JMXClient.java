package com.zqz.jvm.jmx;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.zqz.jvm.jmx.bean.LocalVMInfo;
import com.zqz.jvm.task.ZQZTaskManager;
import com.zqz.jvm.task.job.ConnectTask;

/**
 * 服务器端设置 -Djava.rmi.server.hostname=本服务器的ip地址 -Dcom.sun.management.jmxremote
 * -Dcom.sun.management.jmxremote.port=8999
 * -Dcom.sun.management.jmxremote.ssl=false
 * -Dcom.sun.management.jmxremote.authenticate=false
 * 
 * 客户端连接url service:jmx:rmi:///jndi/rmi://localhost:8888/server
 * 
 * @author zqz
 */
public class JMXClient {

	private static Logger logger = LoggerFactory.getLogger(JVM.class);

	/** jmx连接断开时监听器返回的type **/
	private final static String CONNECT_CLOSED = "jmx.remote.connection.closed";

	/** 关联的jvmId **/
	private long jvmId;
	/** 当前虚拟机的jmx连接 **/
	private JMXConnector connector;
	private MBeanServerConnection mbsc;

	/** jmx连接信息 **/
	private JMXServiceURL url = null;
	private Map<String, String[]> environment = null;

	/** 当前连接状态 **/
	private boolean connected = false;

	/** 是否自动重新连接 **/
	private boolean autoConnect = true;

	public JMXClient(long jvmId) {
		this.jvmId = jvmId;
	}
	

	public static void main(String[] args) throws Exception {
		System.out.println(getAgentFile());
		System.out.println(JMXClient.listLocalVM().size());
		String port = "8999";
		String host = "192.168.2.174";
		// port = "9999";
		// host = "localhost";
		JMXClient client = new JMXClient(1);
		client.connect(host, port);
		JVM jvm = new JVM(1, client);
		jvm.getRuntime();
	}

	/** START -- 连接本地jvm ***********************************************/

	private static String agentFile = null;
	private final static Object AGENT_FILE_LOCK = new Object();
	
	private final static String JMX_REMOTE="com.sun.management.jmxremote";
	private final static String JMX_LOCAL_CONNECTOR_ADDRESS="com.sun.management.jmxremote.localConnectorAddress";
	/**
	 * 获取本地虚拟机信息列表,用于连接本地虚拟机
	 * 
	 * @return
	 * @throws IOException
	 * @throws AgentInitializationException 
	 * @throws AgentLoadException 
	 */
	public static List<LocalVMInfo> listLocalVM() throws IOException, AgentLoadException, AgentInitializationException {
		List<VirtualMachineDescriptor> vms = VirtualMachine.list();
		List<LocalVMInfo> vmList = new ArrayList<LocalVMInfo>();
		for (VirtualMachineDescriptor desc : vms) {
			VirtualMachine vm;
			try {
				vm = VirtualMachine.attach(desc);
			} catch (AttachNotSupportedException e) {
				continue;
			}
			String connectorAddress = vm.getAgentProperties().getProperty(JMX_LOCAL_CONNECTOR_ADDRESS);
			if (connectorAddress == null) {
				connectorAddress = getConnectStr(vm);
				if(connectorAddress == null){
					vm.detach();
					continue;
				}
			}
			LocalVMInfo info = new LocalVMInfo();
			info.setId("-" + desc.id());
			info.setName(desc.displayName());
			info.setSystemProperties(vm.getSystemProperties());
			info.setLocalConnectorAddress(connectorAddress);
			vmList.add(info);
			vm.detach();
		}
		return vmList;
	}


	public static String getAgentFile() {
		if (agentFile == null || "".equals(agentFile)) {
			synchronized(AGENT_FILE_LOCK){
				if(agentFile != null && !"".equals(agentFile) ){
					return agentFile;
				}
				String home = System.getProperty("java.home");
				String agent = new StringBuffer(home).append(File.separator).append("jre").append(File.separator).append("lib").append(File.separator).append("management-agent.jar").toString();
				File f = new File(agent);
				if (!f.exists()) {
					agent = new StringBuffer(home).append(File.separator).append("lib").append(File.separator).append("management-agent.jar").toString();
					f = new File(agent);
					if (!f.exists()) {
						return null;
					}
				}
				try {
					agentFile = f.getCanonicalPath();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return agentFile;
	}
	
	/**
	 * http://hg.openjdk.java.net/jdk7/jdk7/jdk/file/9b8c96f96a0f/src/share/classes/sun/tools/jconsole/LocalVirtualMachine.java
	 * @date 2018-02-02
	 * @author zqz
	 * @param vm
	 * @return
	 * @throws AgentLoadException
	 * @throws AgentInitializationException
	 * @throws IOException
	 */
	public static String getConnectStr(VirtualMachine vm) throws AgentLoadException, AgentInitializationException, IOException{
		vm.loadAgent(getAgentFile(), JMX_REMOTE);
		return (String)vm.getAgentProperties().get(JMX_LOCAL_CONNECTOR_ADDRESS);
	}

	/**
	 * 连接本地jvm
	 * 
	 * @param vmInfo
	 * @return
	 * @throws IOException
	 */
	public boolean getLocalVmMBeanServer(LocalVMInfo vmInfo) throws IOException {
		JMXServiceURL url = new JMXServiceURL(vmInfo.getLocalConnectorAddress());
		return connect(url);
	}

	/** END -- 连接本地jvm ***********************************************/

	/** START -- 远程连接jvm ***********************************************/

	/**
	 * 建立连接 ，获取MBeanServerConnection，通过 MBeanServer间接地访问 MXBean接口
	 * MBeanServerConnection mbsc = createMBeanServer("192.168.2.37", "21811");
	 * 
	 * @param ip
	 * @param jmxport
	 * @return
	 */
	public boolean connect(String ip, String jmxport) {
		return this.connect(ip, jmxport, null, null);
	}

	/**
	 * 建立连接 ，获取MBeanServerConnection，通过 MBeanServer间接地访问 MXBean 接口
	 * MBeanServerConnection mbsc = createMBeanServer("192.168.2.37",
	 * "21811","controlRole", "123456");
	 * 
	 * @param ip
	 * @param jmxport
	 * @return
	 */
	public boolean connect(String ip, String jmxport, String userName, String password) {
		Map<String, String[]> environment = null;
		if (userName != null && !userName.equals("")) {
			environment = new HashMap<String, String[]>();
			String[] credentials = new String[] { userName, password };
			environment.put(JMXConnector.CREDENTIALS, credentials);
		}
		try {
			return connect(new JMXServiceURL(new StringBuilder("service:jmx:rmi:///jndi/rmi://").append(ip).append(":")
					.append(jmxport).append("/jmxrmi").toString()), environment);
		} catch (MalformedURLException e) {
			logger.warn(e.getMessage());
			try {
				changeConnectStatus(false);
			} catch (SchedulerException e1) {
				logger.error("", e1);
			}
		}
		return connected;
	}

	/**
	 * @param url
	 * @param environment
	 * @return
	 * @throws IOException
	 */
	public boolean connect(JMXServiceURL url) {
		return this.connect(url, null);
	}

	/**
	 * 最终调用的连接方法
	 * 
	 * @param url
	 * @param environment
	 * @return
	 * @throws IOException
	 */
	public boolean connect(JMXServiceURL url, Map<String, String[]> environment) {
		this.url = url;
		this.environment = environment;
		System.out.println(url.getURLPath());
		try {
			connector = JMXConnectorFactory.connect(url, environment);
			connector.addConnectionNotificationListener(new NotificationListener() {
				@Override
				public void handleNotification(Notification notification, Object handback) {
					System.out.println("连接状态：" + notification.getType());
					try {
						if (notification.getType().equals(CONNECT_CLOSED)) {
							new ConnectTask(JVMManager.get(jvmId));
							try {
								changeConnectStatus(false);
							} catch (SchedulerException e1) {
								logger.error("", e1);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, null, null);
			mbsc = connector.getMBeanServerConnection();

			try {
				changeConnectStatus(true);
			} catch (SchedulerException e1) {
				logger.error("", e1);
			}
		} catch (IOException e) {
			logger.warn(e.getMessage());
			try {
				changeConnectStatus(false);
			} catch (SchedulerException e1) {
				logger.error("", e1);
			}
		}
		return connected;
	}

	/** END -- 远程连接jvm ***********************************************/

	/** START -- 连接管理操作 ***********************************************/
	/**
	 * 更改连接状态
	 * 
	 * @param isConnect
	 * @throws SchedulerException
	 */
	public void changeConnectStatus(boolean isConnect) throws SchedulerException {
		if (isConnect != this.connected) {
			this.connected = isConnect;
			if (connected) {
				System.out.println("建联成功");
				ZQZTaskManager.resumeJobsByJVM(jvmId);
			} else {
				System.out.println("建联失败");
				ZQZTaskManager.pauseJobsByJVM(jvmId);
			}
		}
	}

	/**
	 * 测试连接是否正常
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean testConnect() throws Exception {
		if (connector == null) {
			return false;
		}
		if (!this.isConnected()) {
			synchronized (connector) {
				if (!this.isConnected()) {
					return this.tryConnect();
				}
			}
		}
		return true;
	}

	/**
	 * 尝试重新建立连接
	 * 
	 * @return
	 */
	public boolean tryConnect() {
		if (!connected) {
			return this.connect(this.url, this.environment);
		}
		return true;
	}

	/** 关闭连接 **/
	public void close() {
		if (connector != null) {
			try {
				connector.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/** END -- 连接管理操作 ***********************************************/

	public boolean isAutoConnect() {
		return autoConnect;
	}

	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public JMXConnector getConnector() {
		return connector;
	}

	public void setConnector(JMXConnector connector) {
		this.connector = connector;
	}

	public MBeanServerConnection getMbsc() {
		return mbsc;
	}

	public void setMbsc(MBeanServerConnection mbsc) {
		this.mbsc = mbsc;
	}
}
