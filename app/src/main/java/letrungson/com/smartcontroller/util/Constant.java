package letrungson.com.smartcontroller.util;

import java.util.HashMap;

import letrungson.com.smartcontroller.model.Value;

public class Constant {
    public static HashMap<String, Value> listFeeds = new HashMap<String, Value>();

    public static void setListFeeds() {
        listFeeds.put("bk-iot-led", new Value("1", "LED", ""));
        listFeeds.put("bk-iot-speaker", new Value("2", "SPEAKER", ""));
        listFeeds.put("bk-iot-lcd", new Value("3", "LCD", ""));
        listFeeds.put("bk-iot-button", new Value("4", "BUTTON", ""));
        listFeeds.put("bk-iot-touch", new Value("5", "TOUCH", ""));
        listFeeds.put("bk-iot-traffic", new Value("6", "TRAFFIC", ""));
        listFeeds.put("bk-iot-temp-humid", new Value("7", "TEMP-HUMID", "C-%"));
        listFeeds.put("bk-iot-magnetic", new Value("8", "MAGNETIC", ""));
        listFeeds.put("bk-iot-soil", new Value("9", "SOIL", ""));
        listFeeds.put("bk-iot-drv", new Value("10", "DRV_PWM", ""));
        listFeeds.put("bk-iot-relay", new Value("11", "RELAY", ""));
        listFeeds.put("bk-iot-sound", new Value("12", "SOUND", ""));
        listFeeds.put("bk-iot-light", new Value("13", "LIGHT", ""));
        listFeeds.put("bk-iot-infrared", new Value("16", "INFRARED", ""));
        listFeeds.put("bk-iot-servo", new Value("17", "SERVO", ""));
        listFeeds.put("bk-iot-time", new Value("22", "TIME", ""));
        listFeeds.put("bk-iot-gas", new Value("23", "GAS", ""));
    }

    public static HashMap<String, Value> getListFeeds() {
        return listFeeds;
    }
}
