package lzw.iot.base.event.listener;

import de.pi3g.pi.rgbled.PinLayout;
import de.pi3g.pi.rgbled.RGBLed;
import lzw.iot.base.event.RGBChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
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

    private RGBLed rgbLed = new RGBLed(PinLayout.PIBORG_LEDBORG);

    private boolean isConnecting = true;
    @Override
    public void onApplicationEvent(RGBChangeEvent rgbChangeEvent) {
        //GPIO 0,2,3

        switch (rgbChangeEvent.getRgbEventType()) {
            case CONNECTED_WIFI:
                isConnecting = false;
                rgbLed.displayColor(Color.GREEN);
                break;
            case CONNECTING_WIFI:
                do {
                    rgbLed.displayColor(Color.BLUE);
                    try {
                        Thread.sleep(1000L);
                        rgbLed.off();
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (isConnecting);
                break;
            case DISCONNECTED_WIFI:
                isConnecting = false;
                rgbLed.displayColor(Color.RED);
                break;
            default:
                break;
        }
    }
}
