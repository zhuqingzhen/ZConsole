package com.zqz.jvm.tools.jstat.service;

import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.jmx.bean.GCInfo;
import com.zqz.jvm.jmx.bean.GCMemoryInfo;
import com.zqz.jvm.jmx.bean.JstatGCInfo;
import com.zqz.jvm.tools.jstat.JStatWriter;
import org.springframework.stereotype.Service;
import sun.jvmstat.monitor.*;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.tools.jstat.*;

import java.io.*;
import java.lang.management.MemoryUsage;
import java.net.URL;
import java.util.*;

@Service
public class JvmJstatService {


    /**
     * todo 未完成
     * @param jvmId
     * @return
     * @throws Exception
     */
    public JstatGCInfo getInfo(long jvmId) throws Exception {
        JVM jvm = JVMManager.get(jvmId);
        jvm.getGCINFO();
        JstatGCInfo info = new JstatGCInfo();
        GCMemoryInfo memoryInfo = jvm.getMemoryInfo();
        long s0c = memoryInfo.getSurvivorSpace().getCommitted()/1024;
        long s1c = memoryInfo.getSurvivorSpace().getCommitted()/1024;

        long s0u = memoryInfo.getSurvivorSpace().getUsed()/1024;
        long s1u = memoryInfo.getSurvivorSpace().getUsed()/1024;

        long ec = memoryInfo.getEdenSpace().getCommitted()/1024;
        long eu = memoryInfo.getEdenSpace().getUsed()/1024;

        long oc = memoryInfo.getOldGen().getCommitted()/1024;
        long ou = memoryInfo.getOldGen().getUsed()/1024;

        long mc = memoryInfo.getMetaspace().getCommitted()/1024;
        long mu = memoryInfo.getMetaspace().getUsed()/1024;

        long ccsc = memoryInfo.getCompressedClassSpace().getCommitted()/1024;
        long ccsu = memoryInfo.getCompressedClassSpace().getUsed()/1024;

        GCInfo ygcInfo = jvm.getLastygc();
        long ygc = ygcInfo.getCount();
        float ygct = (float)ygcInfo.getTotalTime()/1000;

        GCInfo fgcInfo = jvm.getLastfullgc();

        long fgc = fgcInfo.getCount();
        float fgct = (float)fgcInfo.getTotalTime()/1000;

        float gct = ygct + fgct;

        System.out.println("S0C\tS1C\tS0U\tS1U\tEC\tEU\tOC\tOU\tMC\tMU\tCCSC\tCCSU\tYGC\tYGCT\tFGC\tFGCT\tGCT ");
        System.out.format("%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%f\t%d\t%f\t%f\n",s0c,s1c,s0u,s1u,ec,eu,oc,ou,mc,mu,ccsc,ccsu,ygc,ygct,fgc,fgct,gct);
        return null;
    }

    /**
     * 获取jstat命令的所有option
     * @param arguments
     * @return
     */
    public List<String> getJstatOptions(Arguments arguments){
        if (arguments.isOptions()) {
            return listOptions(arguments.optionsSources());
        }
        return null;
    }

    private List<String> listOptions(List<URL> sources) {
        Comparator<OptionFormat> c = new Comparator<OptionFormat>() {
            public int compare(OptionFormat o1, OptionFormat o2) {
                OptionFormat of1 = o1;
                OptionFormat of2 = o2;
                return (of1.getName().compareTo(of2.getName()));
            }
        };

        Set<OptionFormat> options = new TreeSet<OptionFormat>(c);

        List<String> result = new ArrayList<>();

        for (URL u : sources) {
            try {
                Reader r = new BufferedReader(
                        new InputStreamReader(u.openStream()));
                Set<OptionFormat> s = new Parser(r).parseOptions();
                options.addAll(s);
            } catch (IOException e) {

            } catch (ParserException e) {
                // Exception in parsing the options file.
                System.err.println(u + ": " + e.getMessage());
                System.err.println("Parsing of " + u + " aborted");
            }
        }

        for ( OptionFormat of : options) {
            if (of.getName().compareTo("timestamp") == 0) {
                // ignore the special timestamp OptionFormat.
                continue;
            }
            result.add("-" + of.getName());
        }
        return result;
    }

	public JstatGCInfo getJstatGCInfo(long jvmId){
        JVM jvm = JVMManager.get(jvmId);
        JstatGCInfo info = new JstatGCInfo();
        GCInfo gcInfo = jvm.getLastygc();
        MemoryUsage mu = gcInfo.getGcMemoryInfoAfter().getEdenSpace();
        info.setEC(mu.getCommitted()/1024);
        info.setEU(mu.getUsed()/1024);
        mu = gcInfo.getGcMemoryInfoAfter().getMetaspace();
        if (mu != null) {
            info.setMC(mu.getCommitted()/1024);
            info.setMU(mu.getUsed()/1024);
            mu = gcInfo.getGcMemoryInfoAfter().getCompressedClassSpace();
            info.setCCSC(mu.getCommitted()/1024);
            info.setCCSU(mu.getUsed()/1024);
        }else{
            mu = gcInfo.getGcMemoryInfoAfter().getPermgen();
            info.setPC(mu.getCommitted()/1024);
            info.setPU(mu.getUsed()/1024);
        }
        mu = gcInfo.getGcMemoryInfoAfter().getCodecache();
        info.setCCC(mu.getCommitted()/1024);
        info.setCCU(mu.getUsed()/1024);
        mu = gcInfo.getGcMemoryInfoAfter().getOldGen();
        info.setOC(mu.getCommitted()/1024);
        info.setOU(mu.getUsed()/1024);
        mu = gcInfo.getGcMemoryInfoAfter().getSurvivorSpace();
        info.setSU(mu.getUsed()/1024);
        info.setSC(mu.getCommitted()/1024);
        return info;
	}

    /**
     *
     * 根据参数输出采集的数据
     *
     * @param arguments 参数
     * @param out 输出流
     * @throws MonitorException
     */
    public void logSamples(Arguments arguments, PrintWriter out) throws MonitorException {
        final VmIdentifier vmId = arguments.vmId();
        int interval = arguments.sampleInterval();
        final MonitoredHost monitoredHost =
                MonitoredHost.getMonitoredHost(vmId);
        MonitoredVm monitoredVm = monitoredHost.getMonitoredVm(vmId, interval);
        final JStatWriter writer = new JStatWriter(monitoredVm);
        OutputFormatter formatter = null;

        if (arguments.isSpecialOption()) {
            OptionFormat format = arguments.optionFormat();
            formatter = new OptionOutputFormatter(monitoredVm, format);
        } else {
            List<Monitor> logged = monitoredVm.findByPattern(arguments.counterNames());
            Collections.sort(logged, arguments.comparator());
            List<Monitor> constants = new ArrayList<>();

            for (Iterator i = logged.iterator(); i.hasNext(); /* empty */) {
                Monitor m = (Monitor)i.next();
                if (!(m.isSupported() || arguments.showUnsupported())) {
                    i.remove();
                    continue;
                }
                if (m.getVariability() == Variability.CONSTANT) {
                    i.remove();
                    if (arguments.printConstants()) constants.add(m);
                } else if ((m.getUnits() == Units.STRING)
                        && !arguments.printStrings()) {
                    i.remove();
                }
            }

            if (!constants.isEmpty()) {
                writer.writeList(constants, arguments.isVerbose(),
                        arguments.showUnsupported(), out);
                if (!logged.isEmpty()) {
                    System.out.println();
                }
            }

            if (logged.isEmpty()) {
                monitoredHost.detach(monitoredVm);
                return;
            }

            formatter = new RawOutputFormatter(logged,
                    arguments.printStrings());
        }

        // handle user termination requests by stopping sampling loops
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                writer.stopLogging();
            }
        });

        // handle target termination events for targets other than ourself
        HostListener terminator = new HostListener() {
            public void vmStatusChanged(VmStatusChangeEvent ev) {
                Integer lvmid = new Integer(vmId.getLocalVmId());
                if (ev.getTerminated().contains(lvmid)) {
                    writer.stopLogging();
                } else if (!ev.getActive().contains(lvmid)) {
                    writer.stopLogging();
                }
            }

            public void disconnected(HostEvent ev) {
                if (monitoredHost == ev.getMonitoredHost()) {
                    writer.stopLogging();
                }
            }
        };

        if (vmId.getLocalVmId() != 0) {
            monitoredHost.addHostListener(terminator);
        }

        writer.logSamples(formatter, arguments.headerRate(),
                arguments.sampleInterval(), arguments.sampleCount(),
                out);

        // detach from host events and from the monitored target jvm
        if (terminator != null) {
            monitoredHost.removeHostListener(terminator);
        }
        monitoredHost.detach(monitoredVm);
    }

}
