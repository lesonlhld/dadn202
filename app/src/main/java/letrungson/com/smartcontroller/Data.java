package letrungson.com.smartcontroller;

public class Data {
    int id;
    String last_value;
    String updated_at;
    DetailData data;

    public Data(int id, String last_value, String updated_at, DetailData data){
        this.id = id;
        this.last_value = last_value;
        this.updated_at = updated_at;
        this.data = data;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setLast_value(String last_value) {
        this.last_value = last_value;
    }

    public String getLast_value() {
        return last_value;
    }

    public void setData(DetailData data) {
        this.data = data;
    }

    public DetailData getData() {
        return data;
    }
}
