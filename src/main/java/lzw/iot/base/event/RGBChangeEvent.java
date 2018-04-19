package lzw.iot.base.event;

import lzw.iot.base.model.RgbEventType;
import org.springframework.context.ApplicationEvent;

/**
 * RGBChangeEvent
 *
 * @author lzw
 * @date 2018/4/20 1:07
 **/
public class RGBChangeEvent extends ApplicationEvent {

    /**
     * 灯光事件
     */
    private RgbEventType rgbEventType;

    public RGBChangeEvent(Object source, RgbEventType rgbEventType) {
        super(source);
        this.rgbEventType = rgbEventType;
    }

    public RgbEventType getRgbEventType() {
        return rgbEventType;
    }
}
