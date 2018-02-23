package com.zqz.jvm.tools.jstat;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatter;
import sun.tools.jstat.RowClosure;

/**
 * Created by sangjian on 2018/2/23 0023.
 */
public class JSONOptionOutputFormatter extends OptionOutputFormatter {

    private MonitoredVm _vm;
    private OptionFormat _format;

    public JSONOptionOutputFormatter(MonitoredVm vm, OptionFormat format) throws MonitorException {
        super(vm, format);
        this._vm = vm;
        this._format = format;
    }

    @Override
    public String getRow() throws MonitorException {
        JSONRowClosure rc = new JSONRowClosure(_vm);
        _format.apply(rc);
        return rc.getRow();
    }
}
