package lzw.iot.base.device.wifi;

import lzw.iot.base.model.Ap;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;

/**
 * @author zzy
 * @date 2018/3/8 14:43
 **/
@RestController
public class Controller {

    @GetMapping("/info")
    public String apInfo() {
        return "Hello";
    }
}
