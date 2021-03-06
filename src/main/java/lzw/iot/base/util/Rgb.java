package lzw.iot.base.util;

import org.springframework.stereotype.Component;

/**
 * Rgb
 *
 * @author lzw
 * @date 2018/5/17 21:10
 **/
@Component
public class Rgb {

    private RGBLed rgbLed;

    private Rgb() {
        rgbLed = new RGBLed(PinLayout.PIBORG_LEDBORG);
    }

    public RGBLed getInstance(){
        return rgbLed;
    }

}
