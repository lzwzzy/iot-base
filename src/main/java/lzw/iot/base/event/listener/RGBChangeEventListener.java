package lzw.iot.base.event.listener;

import lzw.iot.base.event.RGBChangeEvent;
import lzw.iot.base.service.AsyncTaskService;
import lzw.iot.base.util.RGBLed;
import lzw.iot.base.util.Rgb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.*;


/**
 * RGBChangeEventListener
 *
 * @author lzw
 * @date 2018/4/20 1:13
 **/
@Component
public class RGBChangeEventListener implements ApplicationListener<RGBChangeEvent> {


    private boolean isConnecting = true;

    @Autowired
    private Rgb rgb;

    @Override
    public void onApplicationEvent(RGBChangeEvent rgbChangeEvent) {
        //GPIO 0,2,3
        RGBLed rgbLed = rgb.getInstance();
        try {
        switch (rgbChangeEvent.getRgbEventType()) {
            case CONNECTED_WIFI:
                isConnecting = false;
                rgbLed.displayColor(Color.GREEN);
                break;
            case CONNECTING_WIFI:
                do {
                    Thread.sleep(2000L);
                    rgbLed.off();
                    Thread.sleep(2000L);
                } while (isConnecting);
                break;
            case DISCONNECTED_WIFI:
                isConnecting = false;
                rgbLed.displayColor(Color.RED);
                break;
            default:
                break;
        }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
