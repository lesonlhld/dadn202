package letrungson.com.smartcontroller.model;

public class Device {
    private String deviceId;
    private String deviceName;
    private String currentState;

    public Device(){

    }

    public Device(String deviceName, String currentState){
        this.deviceName = deviceName;
        this.currentState = currentState;
    }

    public Device(String deviceId, String deviceName, String currentState){
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.currentState = currentState;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
