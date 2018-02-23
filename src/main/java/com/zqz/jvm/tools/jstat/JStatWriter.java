package com.zqz.jvm.tools.jstat;

import com.zqz.jvm.tools.jstat.util.JSONUtils;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.StringMonitor;
import sun.tools.jstat.OutputFormatter;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * Created by sangjian on 2018/2/16.
 */
public class JStatWriter {
    private MonitoredVm monitoredVm;
    private volatile boolean active = true;

    public JStatWriter(MonitoredVm monitoredVm) {
        this.monitoredVm = monitoredVm;
    }

    /**
     * print the monitors that match the given monitor name pattern string.
     */
    public void writeNames(String names, Comparator<Monitor> comparator,
                           boolean showUnsupported, PrintWriter out)
            throws MonitorException, PatternSyntaxException {

        // get the set of all monitors
        List<Monitor> items = monitoredVm.findByPattern(names);
        Collections.sort(items, comparator);

        for (Monitor m: items) {
            if (!(m.isSupported() || showUnsupported)) {
                continue;
            }
            out.println(m.getName());
        }
    }

    /**
     * print name=value pairs for the given list of monitors.
     */
    public void printlnSnapShot(String names, Comparator<Monitor> comparator,
                              boolean verbose, boolean showUnsupported,
                              PrintWriter out)
            throws MonitorException, PatternSyntaxException {

        // get the set of all monitors
        List<Monitor> items = monitoredVm.findByPattern(names);
        Collections.sort(items, comparator);

        writeList(items, verbose, showUnsupported, out);
    }

    /**
     * print name=value pairs for the given list of monitors.
     */
    public void writeList(List<Monitor> list, boolean verbose, boolean showUnsupported,
                          PrintWriter out)
            throws MonitorException {

        // print out the name of each available counter
        for (Monitor m: list ) {

            if (!(m.isSupported() || showUnsupported)) {
                continue;
            }

            StringBuilder buffer = new StringBuilder();
            buffer.append(m.getName()).append("=");

            if (m instanceof StringMonitor) {
                buffer.append("\"").append(m.getValue()).append("\"");
            } else {
                buffer.append(m.getValue());
            }

            if (verbose) {
                buffer.append(" ").append(m.getUnits());
                buffer.append(" ").append(m.getVariability());
                buffer.append(" ").append(m.isSupported() ? "Supported"
                        : "Unsupported");
            }
            out.println(buffer.toString());
        }
    }

    /**
     * method to for asynchronous termination of sampling loops
     */
    public void stopLogging() {
        active = false;
    }

    /**
     * print samples according to the given format.
     */
    public void logSamples(OutputFormatter formatter, int headerRate,
                           int sampleInterval, int sampleCount, PrintWriter out)
            throws MonitorException {

        long iterationCount = 0;
        int printHeaderCount = 0;

        // if printHeader == 0, then only an initial column header is desired.
        int printHeader = headerRate;
        if (printHeader == 0) {
            // print the column header once, disable future printing
            String data = JSONUtils.stringWithSpace2JSONArray(formatter.getHeader()).toString();
            out.println("data: " + data +"\n");
            System.out.println(formatter.getHeader());
            printHeader = -1;
        }

        while (active) {
            // check if it's time to print another column header
            if (printHeader > 0 && --printHeaderCount <= 0) {
                printHeaderCount = printHeader;
                out.println("data: " + formatter.getHeader() +"\n");
            }
            System.out.println(formatter.getRow());
            String data = formatter.getRow();
            out.println("data: " + data +"\n");
            out.flush();
            // check if we've hit the specified sample count
            if (sampleCount > 0 && ++iterationCount >= sampleCount) {
                break;
            }

            // explicitly check for client disconnect - PrintWriter does not throw exceptions
            if (out.checkError()) {
//                throw new IOException("io error");
                System.out.println("io error");
                break;
            }

            try { Thread.sleep(sampleInterval); } catch (Exception e) { };
        }
    }
}
