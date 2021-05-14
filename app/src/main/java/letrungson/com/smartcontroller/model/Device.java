package letrungson.com.smartcontroller.model;

public class Device {
    private String deviceId;
    private String deviceName;
    private String roomId;
    private String type;
    private String state;

    public Device() {

    }

    public Device(String deviceId, String deviceName, String roomId, String type, String state) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.roomId = roomId;
        this.type = type;
        this.state = state;
    }

    public Device(Device device) {
        this.deviceId = device.getDeviceId();
        this.deviceName = device.getDeviceName();
        this.roomId = device.getRoomId();
        this.type = device.getType();
        this.state = device.getState();
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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void assign(Device device) {
//        this.deviceId = device.getDeviceId();
//        this.deviceName = device.getDeviceName();
//        this.roomId = device.getroomId();
//        this.type = device.getType();
        this.state = device.getState();
    }
}
