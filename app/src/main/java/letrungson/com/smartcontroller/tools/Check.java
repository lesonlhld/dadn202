package letrungson.com.smartcontroller.tools;

import java.util.List;

import letrungson.com.smartcontroller.model.Device;

public class Check {
    public static boolean checkExistDeviceInDatabase(List<Device> c, String id) {
        for (Device o : c) {
            if (o != null && o.getDeviceId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
