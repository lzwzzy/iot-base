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

/**
 * @author zzy
 * @date 2018/3/20 11:00
 **/
@Service
public class AsyncTaskServiceImpl implements AsyncTaskService {

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    @Async
    public  void gpioListenerTask() {
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
            long startTime = System.currentTimeMillis();
            if (event.getState().isHigh() && System.currentTimeMillis() - startTime > 3000L){
                logger.info("开始配网...");
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
}
