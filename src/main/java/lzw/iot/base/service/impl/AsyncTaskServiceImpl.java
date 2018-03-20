package lzw.iot.base.service.impl;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;
import lzw.iot.base.IotBaseApplication;
import lzw.iot.base.service.AsyncTaskService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static final int KEY_SHORT_PRESS = 1;

    private static final int KEY_LONG_PRESS = 2;

    private static final int KEY_LONG_TIMER = 3;

    private long lastKeytime = 0;

    @Override
    @Async
    public void gpioListenerTask() {
        final Console console = new Console();

        final GpioController gpio = GpioFactory.getInstance();

        console.promptForExit();
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

        // 事件监听
        myButton.addListener((GpioPinListenerDigital) event -> {
            switch (keydown(event.getState().isHigh())) {
                case KEY_SHORT_PRESS:
                    logger.info("点按");
                    break;
                case KEY_LONG_PRESS:
                    logger.info("开始配网...");
                    break;
                default:
                    break;
            }

        });

        try {
            console.waitForExit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // forcefully shutdown all GPIO monitoring threads and scheduled tasks
        gpio.shutdown();
    }

    /**
     * 按键按下时间检测
     *
     * @param isHigh 是否为高电平
     * @return
     */
    private int keydown(boolean isHigh) {
        long keepTime;
        if (isHigh) {
            delay(100);
            keepTime = System.currentTimeMillis() / 1000;
            while (isHigh) {
                if ((System.currentTimeMillis() / 1000 - keepTime) > KEY_LONG_TIMER) {
                    lastKeytime = System.currentTimeMillis();
                    return KEY_LONG_PRESS;
                }
            } //until open the key

            if ((System.currentTimeMillis() / 1000 - lastKeytime) > KEY_LONG_TIMER) {
                return KEY_SHORT_PRESS;
            }
            return 0;
        }
        return 0;
    }
}
