package gachon.mpclass.prezzy_pop;

import java.util.HashMap;

public class Parent extends User{
    private HashMap<Integer, String> child_list = new HashMap<>();

    Parent() {}

    Parent(String key, String nick, String domain) {
        super(key, nick, domain);
    }

    public HashMap<Integer, String> getChild_list() {
        return child_list;
    }

    public void setChild_list(HashMap<Integer, String> child_list) {
        this.child_list = child_list;
    }

    public void addChild_list(String child_email_key) {
        this.child_list.put(this.child_list.size()+1, child_email_key);
    }
}
