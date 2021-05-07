package letrungson.com.smartcontroller.model;

public class LogState {
    private String datetime;
    private String deviceid;
    private String newstate;
    private String userid;

    public LogState(){

    }

    public LogState(String datetime, String deviceid, String newstate, String userid){
        this.datetime = datetime;
        this.deviceid = deviceid;
        this.newstate = newstate;
        this.userid = userid;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getNewstate() {
        return newstate;
    }

    public void setNewstate(String newstate) {
        this.newstate = newstate;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
