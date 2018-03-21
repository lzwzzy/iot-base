package lzw.iot.base;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;
import com.pi4j.util.ConsoleColor;
import lzw.iot.base.service.AsyncTaskService;
import lzw.iot.base.service.impl.AsyncTaskServiceImpl;
import lzw.iot.base.util.WifiAPUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import static com.pi4j.wiringpi.Gpio.delay;


@SpringBootApplication
@EnableAsync
public class IotBaseApplication {

    private static final Log LOGGER = LogFactory.getLog(IotBaseApplication.class);

    private static final int KEY_SHORT_PRESS = 1;

    private static final int KEY_LONG_PRESS = 2;

    private static final int KEY_LONG_TIMER = 5;

    private long lastKeytime = 0;

    private PinState pinState;


    public static void main(String[] args) {
        LOGGER.info("\n========================================================="
                + "\n                                                         "
                + "\n          欢迎来到柠檬IOT                                  "
                + "\n                                                         "
                + "\n    本程序为柠檬IOT多功能网关系统                            "
                + "\n    gitHub: https://github.com/lzwzzy/iot-base           "
                + "\n                                                         "
                + "\n=========================================================");
        SpringApplication.run(IotBaseApplication.class, args);

//        AsyncTaskServiceImpl asyncTaskService = new AsyncTaskServiceImpl();
//        asyncTaskService.gpioListenerTask();
        IotBaseApplication iotBaseApplication = new IotBaseApplication();
        iotBaseApplication.gpioListenerTask();
    }
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
            LOGGER.info(event.getState());
            this.pinState = event.getState();
            switch (keydown()) {
                case KEY_SHORT_PRESS:
                    LOGGER.info("点按");
                    break;
                case KEY_LONG_PRESS:
                    LOGGER.info("开始配网...");
                    break;
                default:
                    LOGGER.info("qita");
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
     * @return
     */
    private synchronized int keydown() {
        long keepTime;
        if (this.pinState == PinState.HIGH) {
            delay(100);
            keepTime = System.currentTimeMillis() / 1000;
            while (this.pinState == PinState.HIGH) {
                if ((System.currentTimeMillis() / 1000 - keepTime) > KEY_LONG_TIMER) {
                    lastKeytime = System.currentTimeMillis();
                    return KEY_LONG_PRESS;
                }else{
                    break;
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
