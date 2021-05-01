package letrungson.com.smartcontroller.model;

public class Room {
    private String roomId;
    private String roomName;
    private Boolean roomState;
    private String roomDescription;
    private String roomTemp;

    public Room(){

    }

    public Room(String roomId, String roomName){
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public Room(String roomName, Boolean roomState, String roomDescription, String roomTemp) {
        this.roomName = roomName;
        this.roomState = roomState;
        this.roomDescription = roomDescription;
        this.roomTemp = roomTemp;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Boolean getRoomState() {
        return roomState;
    }

    public void setRoomState(Boolean roomState) {
        this.roomState = roomState;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public String getRoomTemp() {
        return roomTemp;
    }

    public void setRoomTemp(String roomTemp) {
        this.roomTemp = roomTemp;
    }
}
