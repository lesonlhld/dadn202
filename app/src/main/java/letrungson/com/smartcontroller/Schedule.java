package letrungson.com.smartcontroller;

public class Schedule {
    private String startDay;
    private String endDay;
    private String temp;
    private String humid;
    private String startTime;
    private String endTime;

    public Schedule(String startDay, String endDay, String temp, String humid, String startTime, String endTime) {
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

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHumid() {
        return humid;
    }

    public void setHumid(String humid) {
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
