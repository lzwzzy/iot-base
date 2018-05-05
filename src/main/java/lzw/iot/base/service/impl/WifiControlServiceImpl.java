package lzw.iot.base.service.impl;

import lzw.iot.base.common.ErrorCode;
import lzw.iot.base.event.RGBChangeEvent;
import lzw.iot.base.exception.LemonException;
import lzw.iot.base.model.Ap;
import lzw.iot.base.model.RgbEventType;
import lzw.iot.base.service.WifiControlService;
import lzw.iot.base.util.ShellUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author zzy
 * @date 2018/3/21 19:15
 **/
@Service
public class WifiControlServiceImpl implements WifiControlService {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    ApplicationContext applicationContext;


    /**
     * 创建AP
     *
     * @return 执行结果
     */
    @Override
    public void createAP() {
        String creatAp = "sudo sh start_ap";
        try {
            logger.info("pre pwd");
            String pwd = ShellUtil.excuteCMD("pwd");
            logger.info(pwd);
            logger.info("pre createAp");
            ShellUtil.excuteShellScript(creatAp, pwd.trim(), "");
        } catch (IOException e) {
            throw new LemonException(e,ErrorCode.System.FAIL_CREATE_AP);
        }
    }

    /**
     * 停止AP
     *
     * @return 执行结果
     */
    @Override
    public boolean stopAp() {
        return false;
    }

    /**
     * 连接AP
     *
     * @param ap ssid
     *           pwd
     * @return 执行结果
     */
    @Override
    public boolean connectAp(Ap ap) {
        return false;
    }

    /**
     * 微信Airkiss配网
     */
    @Override
    @Async
    public void airkiss_connect_wifi() {
        String stopwlanScript = "sudo ifconfig wlan0 down";
        String startAirkissScript = "sudo ./airkiss mon0";
        String startMon0Script = "sudo ifconfig mon0 up";
        try {
            String pwd = ShellUtil.excuteCMD("pwd");
            logger.info("stopping wlan ...");
            ShellUtil.excuteShellScript(stopwlanScript, pwd.trim(), "");
            ShellUtil.excuteShellScript(startMon0Script, pwd.trim(), "");
            logger.info("starting airkiss ...");
            ShellUtil.excuteShellScript(startAirkissScript, pwd.trim(), "");
            applicationContext.publishEvent(new RGBChangeEvent(this, RgbEventType.CONNECTED_WIFI));
        } catch (IOException e) {
            throw new LemonException(e,ErrorCode.System.FAIL_CREATE_AP);
        }
    }
}
