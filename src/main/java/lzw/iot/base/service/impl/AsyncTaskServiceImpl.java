package lzw.iot.base.service.impl;

import com.pi4j.component.button.*;
import com.pi4j.component.button.Button;
import com.pi4j.component.button.impl.GpioButtonComponent;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.CommandArgumentParser;
import de.pi3g.pi.rgbled.PinLayout;
import de.pi3g.pi.rgbled.RGBLed;
import lzw.iot.base.common.ErrorCode;
import lzw.iot.base.exception.LemonException;
import lzw.iot.base.service.AsyncTaskService;
import lzw.iot.base.service.WifiControlService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Map;

import static com.pi4j.wiringpi.Gpio.*;

/**
 * @author zzy
 * @date 2018/3/20 11:00
 **/
@Service
public class AsyncTaskServiceImpl implements AsyncTaskService {


    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private WifiControlService wifiControlService;

    private Button button;

    /**
     * 主状态按键
     */
    private Pin stateKeyPin = RaspiPin.GPIO_01;

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


    private final GpioController gpio = GpioFactory.getInstance();


    @Override
    @Async
    public void gpioListenerTask() {


        //当前线程退出时，结束按键扫描
        Runtime.getRuntime().addShutdownHook(new Thread(() -> exiting = true));
        //按键1GPIO
        Pin pin = CommandArgumentParser.getPin(
                RaspiPin.class,
                RaspiPin.GPIO_01);

        //按键2GPIO
        Pin pin2 = CommandArgumentParser.getPin(
                RaspiPin.class,
                RaspiPin.GPIO_05);

        // 默认按键方式
        PinPullResistance pull = CommandArgumentParser.getPinPullResistance(
                PinPullResistance.PULL_DOWN);

        // 按键事件
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(pin, pull);
        final GpioPinDigitalInput myButton2 = gpio.provisionDigitalInputPin(pin2, pull);



        GpioButtonComponent gpioButtonComponent = new GpioButtonComponent(myButton2);
        gpioButtonComponent.addListener(3000, (ButtonHoldListener) buttonEvent -> logger.debug("这个键你按了3s以上了"));

        // 程序退出时，释放引脚
        myButton.setShutdownOptions(true);

        // 按键事件监听
        myButton.addListener((GpioPinListenerDigital) event -> {
//            logger.info(event.getState());
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
                        waittingConnectLedStat();
                        //微信配网
                        WifiControlServiceImpl wifiControlService = new WifiControlServiceImpl();
                        wifiControlService.airkiss_connect_wifi();
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
        if (digitalRead(stateKeyPin.getAddress()) == HIGH) {
            delay(100);
            keepTime = currentTimeSeconds();
            while (digitalRead(stateKeyPin.getAddress()) == HIGH) {
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

    /**
     * 配网指示
     */
    private void waittingConnectLedStat() {
        RGBLed rgbLed = new RGBLed(PinLayout.PIBORG_LEDBORG);
        rgbLed.displayColor(Color.RED);
    }
}
