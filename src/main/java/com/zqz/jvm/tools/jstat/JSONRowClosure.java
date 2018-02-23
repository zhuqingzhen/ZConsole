package com.zqz.jvm.tools.jstat;

import com.alibaba.fastjson.JSONArray;
import com.zqz.jvm.tools.jstat.util.ReflectionUtils;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.RowClosure;
import sun.tools.jstat.ColumnFormat;
import sun.tools.jstat.Expression;
import sun.tools.jstat.ExpressionExecuter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by sangjian on 2018/2/22 0022.
 */
public class JSONRowClosure extends RowClosure {
    private MonitoredVm _vm;

    private JSONArray row = new JSONArray();

    public JSONRowClosure(MonitoredVm vm) {
        super(vm);
        this._vm = vm;
    }

    @Override
    public void visit(Object o, boolean hasNext) throws MonitorException {
        if (! (o instanceof ColumnFormat)) {
            return;
        }

        ColumnFormat c = (ColumnFormat)o;
        String s = null;

        Expression e = c.getExpression();
        ExpressionExecuter ee = (ExpressionExecuter)ReflectionUtils.newInstance("sun.tools.jstat.ExpressionExecuter", new Class<?>[]{MonitoredVm.class}, new Object[]{_vm});
        Object value = ee.evaluate(e);

        if (value instanceof String) {
            s = (String)value;
        } else if (value instanceof Number) {
            double d = ((Number)value).doubleValue();
            double scaledValue = (double)ReflectionUtils.invokeMethod(c.getScale(), "scale", new Class<?>[]{double.class},new Object[]{d});
            DecimalFormat df = new DecimalFormat(c.getFormat());
            DecimalFormatSymbols syms = df.getDecimalFormatSymbols();
            syms.setNaN("-");
            df.setDecimalFormatSymbols(syms);
            s = df.format(scaledValue);
        }

        c.setPreviousValue(value);
        row.add(s);
    }

    @Override
    public String getRow() {
        return row.toString();
    }
}
