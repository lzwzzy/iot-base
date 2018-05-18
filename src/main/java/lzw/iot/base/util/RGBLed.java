package lzw.iot.base.util;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.SoftPwm;

import java.awt.*;

/**
 * RGBLed
 *
 * @author lzw
 * @date 2018/5/18 10:54
 **/
public class RGBLed {
    private final PinLayout pinLayout;
    private Color color;

    public RGBLed(PinLayout pinLayout) {
        this.color = Color.BLACK;
        this.pinLayout = pinLayout;
        GpioController gpio = GpioFactory.getInstance();
        GpioPinPwmOutput ledRed = gpio.provisionSoftPwmOutputPin(pinLayout.getRedPin());
        GpioPinPwmOutput ledGreen = gpio.provisionSoftPwmOutputPin(pinLayout.getGreenPin());
        GpioPinPwmOutput ledBlue = gpio.provisionSoftPwmOutputPin(pinLayout.getBluePin());
        ledRed.setPwmRange(255);
        ledGreen.setPwmRange(255);
        ledBlue.setPwmRange(255);
        ledRed.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        ledGreen.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        ledBlue.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        this.off();
    }

    public void displayColor(Color color) {
        float[] colors = color.getRGBColorComponents((float[])null);
        SoftPwm.softPwmWrite(this.pinLayout.getRedPin().getAddress(), color.getRed());
        SoftPwm.softPwmWrite(this.pinLayout.getGreenPin().getAddress(), color.getGreen());
        SoftPwm.softPwmWrite(this.pinLayout.getBluePin().getAddress(), color.getBlue());
        this.color = color;
    }

    public final void off() {
        this.displayColor(Color.BLACK);
    }

    public Color getDisplayedColor() {
        return this.color;
    }
}
