package lzw.iot.base.util;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * PinLayout
 *
 * @author lzw
 * @date 2018/5/18 10:53
 **/
public class PinLayout {
    public static PinLayout PIBORG_LEDBORG;
    private final Pin redPin;
    private final Pin greenPin;
    private final Pin bluePin;

    public PinLayout(Pin redPin, Pin greenPin, Pin bluePin) {
        this.redPin = redPin;
        this.greenPin = greenPin;
        this.bluePin = bluePin;
    }

    public Pin getRedPin() {
        return this.redPin;
    }

    public Pin getGreenPin() {
        return this.greenPin;
    }

    public Pin getBluePin() {
        return this.bluePin;
    }

    static {
        PIBORG_LEDBORG = new PinLayout(RaspiPin.GPIO_00, RaspiPin.GPIO_02, RaspiPin.GPIO_03);
    }
}
