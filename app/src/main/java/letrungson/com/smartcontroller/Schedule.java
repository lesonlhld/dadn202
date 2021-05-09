package letrungson.com.smartcontroller;

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
        this.temp = -1;
        this.humid = -1;
        this.startTime = "";
        this.endTime = "";
//        this.repeatDay = new boolean[7];
//        for(int i = 0; i < 7; i++) {
//            this.repeatDay[i] = false;
//        }
        this.repeatDay = "";
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

//    public void getRepeatDay(boolean repeatDay[]){
//        for(int i = 0; i < 7; i++) {
//            repeatDay[i] = this.repeatDay[i];
//        }
//    }
//
//    public void setRepeatDay(boolean repeatDay[])
//    {
//        for(int i = 0; i < 7; i++) {
//            this.repeatDay[i] = repeatDay[i];
//        }
//    }
    public String getRepeatDay(){
        return this.repeatDay;
    }

    public void setRepeatDay(String repeatDay){
        this.repeatDay = repeatDay;
    }
}
