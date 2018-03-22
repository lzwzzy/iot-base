package lzw.iot.base.service.impl;

import jdk.nashorn.tools.Shell;
import lzw.iot.base.common.ErrorCode;
import lzw.iot.base.exception.LemonException;
import lzw.iot.base.model.Ap;
import lzw.iot.base.service.WifiControlService;
import lzw.iot.base.util.ShellUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author zzy
 * @date 2018/3/21 19:15
 **/
@Service
public class WifiControlServiceImpl implements WifiControlService {
    /**
     * 创建AP
     *
     * @return 执行结果
     */
    @Override
    public void createAP() {
        String creatAp = "sudo sh start_ap";
        try {
            String pwd = ShellUtil.excuteCMD("pwd");
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
}
