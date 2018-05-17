package lzw.iot.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.pi4j.component.button.Button;
import com.pi4j.component.button.ButtonHoldListener;
import com.pi4j.component.button.impl.GpioButtonComponent;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.system.NetworkInfo;
import com.pi4j.util.CommandArgumentParser;
import de.pi3g.pi.rgbled.PinLayout;
import de.pi3g.pi.rgbled.RGBLed;
import lzw.iot.base.common.ErrorCode;
import lzw.iot.base.event.RGBChangeEvent;
import lzw.iot.base.exception.LemonException;
import lzw.iot.base.model.RgbEventType;
import lzw.iot.base.mqtt.Mqtt;
import lzw.iot.base.service.AsyncTaskService;
import lzw.iot.base.service.WifiControlService;
import lzw.iot.base.util.I2CLcdDisplay;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

import static com.pi4j.wiringpi.Gpio.*;

/**
 * @author zzy
 * @date 2018/3/20 11:00
 **/
@Service
public class AsyncTaskServiceImpl implements AsyncTaskService {


    private final Log logger = LogFactory.getLog(getClass());

    private RGBLed rgbLed = new RGBLed(PinLayout.PIBORG_LEDBORG);

    @Autowired
    private WifiControlService wifiControlService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Mqtt mqtt;

    private MqttClient mqttClient;

    private Button button;


    @Value("${device-id}")
    private String deviceId;

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

        //获取mqtt客户端
        mqttClient = mqtt.getClient();
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
            this.pinState = event.getState();
        });

        try {
            //初始化lcd1602
            int address = 0x27;
            I2CLcdDisplay i2CLcdDisplay = new I2CLcdDisplay(address, I2CBus.BUS_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //订阅网关控制事件
            mqttClient.subscribe("device/event/gateway", 1);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    String payload = new String(mqttMessage.getPayload());
                    Map map = JSON.parseObject(payload, Map.class);
                    logger.info("接收消息主题 : " + topic);
                    logger.info("接收消息Qos : " + mqttMessage.getQos());
                    logger.info("接收消息内容 : " + payload);
                    logger.info("==========================================================");
                    if (deviceId.equals(map.get("deviceId"))) {
                        Map event = JSON.parseObject(JSON.toJSON(map).toString(), Map.class);
                        if (Boolean.parseBoolean(String.valueOf(event.get("warn")))) {
                            //打开人体热释电感应
                        } else {
                            //关闭热释电感应
                        }

                        switch (event.get("color").toString()) {
                            case "red":
                                rgbLed.displayColor(Color.RED);
                                break;
                            case "orange":
                                rgbLed.displayColor(Color.ORANGE);
                                break;
                            case "yellow":
                                rgbLed.displayColor(Color.YELLOW);
                                break;
                            case "green":
                                rgbLed.displayColor(Color.GREEN);
                                break;
                            case "cyan":
                                rgbLed.displayColor(Color.CYAN);
                                break;
                            case "magenta":
                                rgbLed.displayColor(Color.MAGENTA);
                                break;
                            default:
                                break;
                        }

                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        try {
            while (!exiting) {
                switch (keydown()) {
                    case KEY_SHORT_PRESS:
                        logger.info("点按");
                        break;
                    case KEY_LONG_PRESS:
                        logger.info("开始配网...");
                        //配网开始（提示灯）
                        applicationContext.publishEvent(new RGBChangeEvent(this, RgbEventType.CONNECTING_WIFI));
                        //微信配网
                        wifiControlService.airkiss_connect_wifi();
                        //配网成功后订阅绑定事件主题
                        mqttClient.subscribe("device/event", 1);
                        break;
                    default:
                        break;
                }
                Thread.sleep(50L);
            }
        } catch (Exception e) {
            throw new LemonException(e, ErrorCode.System.THREAD_INTERRUPTION);
        }
        //关闭gpio
        gpio.shutdown();

    }

    @Override
    @Async
    public void wifiStatusScan() {
        try {
            if (NetworkInfo.getIPAddress() != null) {
                applicationContext.publishEvent(new RGBChangeEvent(this, RgbEventType.CONNECTED_WIFI));
            } else {
                applicationContext.publishEvent(new RGBChangeEvent(this, RgbEventType.DISCONNECTED_WIFI));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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


}
