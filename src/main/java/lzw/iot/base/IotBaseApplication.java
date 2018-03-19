package lzw.iot.base;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.ConsoleColor;
import lzw.iot.base.util.WifiAPUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class IotBaseApplication {

    private static final Log LOGGER = LogFactory.getLog(IotBaseApplication.class);



    public static void main(String[] args) {

        LOGGER.info("\n========================================================="
                + "\n                                                         "
                + "\n          欢迎来到柠檬IOT                                  "
                + "\n                                                         "
                + "\n    本程序为柠檬IOT多功能网关系统                            "
                + "\n    gitHub: https://github.com/lzwzzy/iot-base           "
                + "\n                                                         "
                + "\n=========================================================" );
		SpringApplication.run(IotBaseApplication.class, args);

        final GpioController gpio = GpioFactory.getInstance();

        //按键GPIO
        Pin pin = CommandArgumentParser.getPin(
                RaspiPin.class,
                RaspiPin.GPIO_01,
                args);

        // 默认按键方式
        PinPullResistance pull = CommandArgumentParser.getPinPullResistance(
                PinPullResistance.PULL_DOWN,
                args);

        // 按键事件
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(pin, pull);

        // 程序退出时，释放引脚
        myButton.setShutdownOptions(true);



        // 事件监听
        myButton.addListener((GpioPinListenerDigital) event -> LOGGER.info(event.getEdge()));


        // forcefully shutdown all GPIO monitoring threads and scheduled tasks
        gpio.shutdown();
	}


}
