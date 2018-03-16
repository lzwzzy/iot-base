package lzw.iot.base.device.wifi;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;

/**
 * @author zzy
 * @date 2018/3/8 14:43
 **/
@RestController
public class Controller {




    @GetMapping("/hello")
    public String wifiDataUpload(@RequestParam String top,
                                 @RequestParam String msg) throws MqttException {
        return null;
    }
}
