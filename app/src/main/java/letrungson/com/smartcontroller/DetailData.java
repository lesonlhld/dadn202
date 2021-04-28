package letrungson.com.smartcontroller;

public class DetailData {
    String created_at;
    String value;
    int location;
    String id;

    public DetailData(String created_at, String value, int location, String id){
        this.created_at = created_at;
        this.value = value;
        this.location = location;
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getLocation() {
        return location;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
