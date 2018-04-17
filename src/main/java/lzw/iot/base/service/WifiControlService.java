package lzw.iot.base.service;

import lzw.iot.base.model.Ap;

/**
 * @author zzy
 * @date 2018/3/21 19:11
 **/
public interface WifiControlService {

    /**
     * 创建AP
     * @return 执行结果
     */
    void createAP();

    /**
     * 停止AP
     * @return 执行结果
     */
    boolean stopAp();

    /**
     * 连接AP
     * @param ap ssid
     *           pwd
     * @return 执行结果
     */
    boolean connectAp(Ap ap);

    /**
     * 微信Airkiss配网
     */
    void airkiss_connect_wifi();
}
