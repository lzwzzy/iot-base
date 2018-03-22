package lzw.iot.base.service.impl;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.CommandArgumentParser;
import lzw.iot.base.common.ErrorCode;
import lzw.iot.base.exception.LemonException;
import lzw.iot.base.service.AsyncTaskService;
import lzw.iot.base.service.WifiControlService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.pi4j.wiringpi.Gpio.delay;

/**
 * @author zzy
 * @date 2018/3/20 11:00
 **/
@Service
public class AsyncTaskServiceImpl implements AsyncTaskService {


    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private WifiControlService wifiControlService;

    /**
     * 短按
     */
    private static final int KEY_SHORT_PRESS = 1;
    /**
     * 长按
     */
    private static final int KEY_LONG_PRESS = 2;
    /**
     * 长按时间
     */
    private static final int KEY_LONG_TIMER = 3;
    /**
     * 是否退出线程
     */
    protected boolean exiting = false;
    /**
     * 记录上次按键时长
     */
    private long lastKeytime = 0;
    /**
     * 按键引脚状态
     */
    private PinState pinState;

    @Override
    @Async
    public void gpioListenerTask() {

        final GpioController gpio = GpioFactory.getInstance();

        //当前线程退出时，结束按键扫描
        Runtime.getRuntime().addShutdownHook(new Thread(() -> exiting = true));
        //按键GPIO
        Pin pin = CommandArgumentParser.getPin(
                RaspiPin.class,
                RaspiPin.GPIO_02);

        // 默认按键方式
        PinPullResistance pull = CommandArgumentParser.getPinPullResistance(
                PinPullResistance.PULL_DOWN);

        // 按键事件
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(pin, pull);

        // 程序退出时，释放引脚
        myButton.setShutdownOptions(true);

        // 按键事件监听
        myButton.addListener((GpioPinListenerDigital) event -> {
            logger.info(event.getState());
            this.pinState = event.getState();
        });

        try {
            while (!exiting) {
                switch (keydown()) {
                    case KEY_SHORT_PRESS:
                        logger.info("点按");
                        break;
                    case KEY_LONG_PRESS:
                        logger.info("开始配网...");
                        WifiControlServiceImpl wifiControlService;
                        wifiControlService = new WifiControlServiceImpl();
                        wifiControlService.createAP();
                        break;
                    default:
                        break;
                }
                Thread.sleep(50L);
            }
        } catch (InterruptedException e) {
            throw new LemonException(e, ErrorCode.System.THREAD_INTERRUPTION);
        }
        //关闭gpio
        gpio.shutdown();

    }

    /**
     * 按键按下时间检测
     *
     * @return 按下时间
     */
    private synchronized int keydown() {
        long keepTime;
        if (this.pinState == PinState.HIGH) {
            delay(100);
            keepTime = currentTimeSeconds();
            while (this.pinState == PinState.HIGH) {
                if ((currentTimeSeconds() - keepTime) > KEY_LONG_TIMER) {
                    lastKeytime = System.currentTimeMillis();
                    return KEY_LONG_PRESS;
                }
            } //until open the key

            if ((currentTimeSeconds() - lastKeytime) > KEY_LONG_TIMER) {
                return KEY_SHORT_PRESS;
            }
            return 0;
        }
        return 0;
    }

    /**
     * 计算当前时间数->秒
     *
     * @return 秒
     */
    private long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    private void waittingConnectLedStat(GpioController gpio) {
        GpioPinDigitalOutput wifiState = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "wifiState", PinState.LOW);
        wifiState.blink(1000);
    }
}
