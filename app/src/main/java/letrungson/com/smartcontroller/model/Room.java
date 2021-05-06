package letrungson.com.smartcontroller.model;

import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String roomId;
    private String roomName;
    private Boolean roomState;
    private String roomTargetTemp;
    private String roomCurrentTemp;

    public Room(){

    }

    public Room(String roomName, String roomTemp){
        this.roomId = roomName;
        this.roomName = roomTemp;
    }

    public Room(String roomName, Boolean roomState, String roomTargetTemp, String roomCurrentTemp) {
        this.roomName = roomName;
        this.roomState = roomState;
        this.roomTargetTemp = roomTargetTemp;
        this.roomCurrentTemp = roomCurrentTemp;
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

    public String getRoomTargetTemp() {
        return roomTargetTemp;
    }

    public void setRoomTargetTemp(String roomTargetTemp) {
        this.roomTargetTemp = roomTargetTemp;
    }

    public String getRoomCurrentTemp() {
        return roomCurrentTemp;
    }

    public void setRoomCurrentTempTemp(String roomCurrentTemp) {
        this.roomCurrentTemp = roomCurrentTemp;
    }

    public static List<String> getAllRoomName(List<Room> lstRoom){
        List<String> lstRoomName = new ArrayList<>();
        for (Room room: lstRoom){
            lstRoomName.add(room.getRoomName());
        }
        return lstRoomName;
    }

    @Override
    public String toString() {
        return this.roomName;
    }
}
