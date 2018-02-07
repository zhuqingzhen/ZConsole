package com.zqz.jvm.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * ObjectName
 * @author zqz
 * @date 2018-01-01 00:43
 */
public class ObjectNameUtil {
	
	/**年轻带gc**/
	/**COPY**/
	public static ObjectName ojbectName_Copy = null ;
	/**ParNew**/
	public static ObjectName ojbectName_ParNew = null ;
	/**PS Scavenge**/
	public static ObjectName ojbectName_PSScavenge = null;
	/**G1**/
	public static ObjectName ojbectName_G1YoungGeneration = null ;
	
	/**老年代GC**/
	/**UseSerialGC**/
	public static ObjectName ojbectName_MarkSweepCompact = null;
	/**PS MarkSweep**/
	public static ObjectName ojbectName_PSMarkSweep=null;
	/**CMS**/
	public static ObjectName ojbectName_ConcurrentMarkSweep = null;
	/**G1 Old Generation**/
	public static ObjectName ojbectName_G1OldGeneration=null;
	
	static{
		try{
			/**年轻带gc**/
			ojbectName_Copy = new ObjectName("java.lang:type=GarbageCollector,name=Copy");
			ojbectName_ParNew = new ObjectName("java.lang:type=GarbageCollector,name=ParNew");
			ojbectName_PSScavenge = new ObjectName("java.lang:type=GarbageCollector,name=PS Scavenge");
			ojbectName_G1YoungGeneration = new ObjectName("java.lang:type=GarbageCollector,name=G1 Young Generation");
		
			/**老年代GC**/
			ojbectName_MarkSweepCompact = new ObjectName("java.lang:type=GarbageCollector,name=MarkSweepCompact");
			ojbectName_PSMarkSweep = new ObjectName("java.lang:type=GarbageCollector,name=PS MarkSweep");
			ojbectName_ConcurrentMarkSweep =  new ObjectName("java.lang:type=GarbageCollector,name=ConcurrentMarkSweep");
			ojbectName_G1OldGeneration = new ObjectName("java.lang:type=GarbageCollector,name=G1 Old Generation");
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
}
