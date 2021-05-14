package gachon.mpclass.prezzy_pop;

import java.util.HashMap;

public class Child extends User{
    private HashMap<String, String> Balloon_list;
    private HashMap<String, String> Current_Balloon;

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

    public HashMap<String, String> getCurrent_Balloon() {
        return Current_Balloon;
    }

    public void setCurrent_Balloon(HashMap<String, String> current_Balloon) {
        Current_Balloon = current_Balloon;
    }
}