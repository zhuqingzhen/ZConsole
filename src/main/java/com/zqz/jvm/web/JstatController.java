package com.zqz.jvm.web;

import com.zqz.jvm.tools.jstat.service.JvmJstatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sun.tools.jstat.Arguments;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping(value = "/jstat")
public class JstatController {

    private static Logger logger = LoggerFactory.getLogger(JstatController.class);

    @Autowired
    private JvmJstatService jvmJstatService;


    @RequestMapping("/options")
    @ResponseBody
    public Object getOptions() {
        Arguments arguments = new Arguments(new String[]{"-options"});

        return jvmJstatService.getJstatOptions(arguments);

    }

    /**
     * 执行jstat命令
     *
     * @param response HttpServletResponse对象
     * @param option jstat的操作
     * @param jvmId jvmId
     * @param interval 打印时间间隔
     * @param times 打印次数
     */
    @RequestMapping("/exec/{option}/{jvmId}/{interval}/{times}")
    public void exec(HttpServletResponse response,
                     @PathVariable("option") String option,
                     @PathVariable(value = "jvmId") int jvmId,
                     @PathVariable("interval") int interval,
                     @PathVariable("times") int times) {
        // 使用流信息推送
        response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Connection", "keep-alive");

        try {
            Arguments arguments = new Arguments(new String[]{option, "" + jvmId, "" + interval, "" + times});
            logger.debug("Arguments: option={},jvmId={},interval={},times={}", option, jvmId, interval, times);
            jvmJstatService.logSamples(arguments, response.getWriter());
        } catch (IllegalArgumentException e) {
            logger.error("Arguments: option={},jvmId={},interval={},times={}", option, jvmId, interval, times, new IllegalArgumentException());
        } catch (Exception e) {
            // nothing to do
            logger.error("jstat exec error: ", e);
        }
    }
}
