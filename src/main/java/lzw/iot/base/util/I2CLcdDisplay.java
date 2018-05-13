package lzw.iot.base.util;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.util.BitSet;

/**
 * I2CLcdDisplay
 *
 * @author lzw
 * @date 2018/5/13 20:06
 **/
public class I2CLcdDisplay {
    boolean rsFlag = false;
    boolean eFlag = false;
    boolean backlightFlag = false;
    private static I2CDevice dev = null;
    private final int[] LCD_LINE_ADDRESS = {0x80, 0xC0};  //Address for LCD Lines 0 and 1

    private final boolean LCD_CHR = true; //To decide sent data is data or command
    private final static boolean LCD_CMD = false;

    private I2CBus bus;

    int RS_PIN = 0; //Pin of PCF8574 PORTB/A connected LCD RS pin
    int BACKLIGHT_PIN = 3; //Pin of PCF8574 PORTB/A connected BACKLIGHTpin
    int EN_PIN = 2; //Pin of PCF8574 PORTB/A connected LCD E pin
    int D7_PIN = 7; //Pin of PCF8574 PORTB/A connected LCD D7 pin
    int D6_PIN = 6; //Pin of PCF8574  PORTB/A connected LCD D6 pin
    int D5_PIN = 5; //Pin of PCF8574  PORTB/A connected LCD D5 pin
    int D4_PIN = 4; //Pin of PCF8574 PORTB/A connected LCD D4 pin

    public I2CLcdDisplay(int address, int busNumber) throws Exception {

        bus = I2CFactory.getInstance(busNumber);
        dev = bus.getDevice(address);
        dev.write(0x01, (byte) 0x00);

        init(); //LCD Initialization
        lcd_byte(0x01, LCD_CMD); //LCD Clear Command
        lcd_byte(0x02, LCD_CMD); //LCD Home Command
    }

    /**
     * Output string to display lines
     *
     * @param firstLine - string for first line
     * @param secondLine - string for second line
     * @param backlightFlag - on/off - back light
     */
    public void outputToDisplay(String firstLine, String secondLine, boolean backlightFlag) {

        this.backlightFlag = backlightFlag;

        try {
            lcd_byte(0x01, LCD_CMD); //LCD Clear Command
            lcd_byte(0x02, LCD_CMD); //LCD Home Command
        } catch (Exception e) {
            e.printStackTrace();
        }

        write(firstLine);
        setCursorPosition(1, 0);
        write(secondLine);
    }

    /**
     * Writes 1 Byte data to LCD
     *
     * @param data
     */
    public void write(byte data) {
        try {
            lcd_byte(data, LCD_CHR);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Write string
     *
     * @param data
     */
    public void write(String data) {

        for (int i = 0; i < data.length(); i++) {
            try {
                lcd_byte(data.charAt(i), LCD_CHR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets RS flag and send value to ports depending on DATA or COMMAND
     *
     * @param val
     * @param type
     * @throws Exception
     */
    public void lcd_byte(int val, boolean type) throws Exception {

        rsFlag = type;

        write(val >> 4);
        pulse_en(type, val >> 4);    // cmd or display data

        write(val & 0x0f);
        pulse_en(type, val & 0x0f);
    }

    /**
     * Convert a byte into Bitset
     *
     * @param b
     * @return
     */
    public static BitSet fromByte(byte b) {
        BitSet bits = new BitSet(8);

        for (int i = 0; i < 8; i++) {
            bits.set(i, (b & 1) == 1);
            b >>= 1;
        }

        return bits;
    }

    /**
     * Initialization routine of LCD
     *
     * @throws Exception
     */
    private void init() throws Exception {
        lcd_byte(0x33, LCD_CMD);    // 4 bit
        lcd_byte(0x32, LCD_CMD);    // 4 bit
        lcd_byte(0x28, LCD_CMD);    // 4bit - 2 line
        lcd_byte(0x08, LCD_CMD);    // don't shift, hide cursor
        lcd_byte(0x01, LCD_CMD);    // clear and home display
        lcd_byte(0x06, LCD_CMD);    // move cursor right
        lcd_byte(0x0c, LCD_CMD);    // turn on
    }

    /**
     * Make the enable pin high and low to provide a pulse.
     *
     * @param type
     * @param val
     * @throws Exception
     */
    private void pulse_en(boolean type, int val) throws Exception {
        eFlag = true;
        write(val);
        eFlag = false;
        write(val);

        if (type == LCD_CMD) {
            Thread.sleep(1);
        }
    }

    /**
     * Arrange the respective bit of value to be send depending upon the pins the LCD is connected to.
     *
     * @param incomingData
     * @throws Exception
     */
    private void write(int incomingData) throws Exception {
        int tmpData = incomingData;
        BitSet bits = fromByte((byte) tmpData);
        byte out = (byte) ((bits.get(3)
                ? 1 << D7_PIN
                : 0 << D7_PIN) | (bits.get(2)
                ? 1 << D6_PIN
                : 0 << D6_PIN) | (bits.get(1)
                ? 1 << D5_PIN
                : 0 << D5_PIN) | (bits.get(0)
                ? 1 << D4_PIN
                : 0 << D4_PIN) | (rsFlag
                ? 1 << RS_PIN
                : 0 << RS_PIN) | (eFlag
                ? 1 << EN_PIN
                : 0 << EN_PIN) | (backlightFlag
                ? 1 << BACKLIGHT_PIN
                : 0 << BACKLIGHT_PIN));

        dev.write(0x13, out); //Set the value to PORT B register.
    }

    /**
     * Set row number
     *
     * @param row
     * @param column
     */
    public void setCursorPosition(int row, int column) {

        try {
            lcd_byte(LCD_LINE_ADDRESS[row] + column, LCD_CMD);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
