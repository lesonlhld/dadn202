package letrungson.com.smartcontroller;

public class Schedule {
    private String startDay;
    private String endDay;
    private int temp;
    private int humid;
    private String startTime;
    private String endTime;

    public Schedule(){
        this.startDay = "";
        this.endDay = "";
        this.temp = -1;
        this.humid = -1;
        this.startTime = "";
        this.endTime = "";
    }

    public Schedule(String startDay, String endDay, int temp, int humid, String startTime, String endTime) {
        this.startDay = startDay;
        this.endDay = endDay;
        this.temp = temp;
        this.humid = humid;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
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
}
