package letrungson.com.smartcontroller;

public class Data {
    private String id;
    private int last_value;
    private String updated_at;
    private String key;
//    DetailData data;

    public Data(String id, int last_value, String updated_at, String key){
        this.id = id;
        this.last_value = last_value;
        this.updated_at = updated_at;
        this.key = key;
//        this.data = data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setLast_value(int last_value) {
        this.last_value = last_value;
    }

    public int getLast_value() {
        return last_value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

//    public void setData(DetailData data) {
//        this.data = data;
//    }
//
//    public DetailData getData() {
//        return data;
//    }
}