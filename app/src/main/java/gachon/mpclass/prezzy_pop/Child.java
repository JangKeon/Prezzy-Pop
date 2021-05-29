package gachon.mpclass.prezzy_pop;

import java.util.HashMap;

public class Child extends User{
    private HashMap<String, String> Balloon_list;
    private String current_balloon_id;

    private HashMap<String, String> Parent_list;
    Child(){}

    Child(String key, String nick, String domain) {
        super(key, nick, domain);
    }

    public HashMap<String, String> getBalloon_list() {
        return Balloon_list;
    }

    public void setBalloon_list(HashMap<String, String> balloon_list) {
        Balloon_list = balloon_list;
    }

    public HashMap<String, String> getParent_list() {
        return Parent_list;
    }

    public void setParent_list(HashMap<String, String> parent_list) {
        Parent_list = parent_list;
    }

    public String getCurrent_balloon_id() {
        return current_balloon_id;
    }

    public void setCurrent_balloon_id(String current_balloon_id) {
        this.current_balloon_id = current_balloon_id;
    }
}