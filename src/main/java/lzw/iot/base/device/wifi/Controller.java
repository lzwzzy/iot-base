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
@RestController("/ap")
public class Controller {

    @PutMapping("/info")
    public String apInfo(@RequestBody Ap ap) {
        return null;
    }
}
