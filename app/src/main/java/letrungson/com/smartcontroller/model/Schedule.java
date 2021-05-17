package letrungson.com.smartcontroller.model;

public class Schedule {
    //private boolean[] repeatDay;
    private String repeatDay;
    private int temp;
    private int humid;
    private String startTime;
    private String endTime;
    private String scheduleID;
    private String roomID;

    public Schedule(){
        this.temp = 30;
        this.humid = 30;
        this.startTime = "00:00";
        this.endTime = "00:00";
//        this.repeatDay = new boolean[7];
//        for(int i = 0; i < 7; i++) {
//            this.repeatDay[i] = false;
//        }
        this.repeatDay = "0000000";
        this.roomID = "";
        this.scheduleID = "";
    }

    public Schedule(int temp, int humid, String startTime, String endTime, String repeatDay) {
        this.temp = temp;
        this.humid = humid;
        this.startTime = startTime;
        this.endTime = endTime;
//        this.repeatDay = new boolean[7];
//        for(int i = 0; i < 7; i++) {
//            this.repeatDay[i] = repeatDay[i];
        this.repeatDay = repeatDay;
    }

    public String getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(String scheduleID) {
        this.scheduleID = scheduleID;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getHumid() {
        return humid;
    }

    public void setHumid(int humid) {
        this.humid = humid;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRepeatDay(){
        return this.repeatDay;
    }

    public void setRepeatDay(String repeatDay){
        this.repeatDay = repeatDay;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
