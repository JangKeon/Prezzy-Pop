package gachon.mpclass.prezzy_pop;

import android.media.Image;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;

public class BalloonStat {
    private String child_key;
    private int set_time;
    private String achievement;
    private String date;
    private String parent_key;
    private int cur_time;
    private String state;
    private String image;

    BalloonStat() {}

    public BalloonStat(String child_key, int set_time, String achievement, String date, String parent_key, int cur_time, String state, String image) {
        this.child_key = child_key;
        this.set_time = set_time;
        this.achievement = achievement;
        this.date = date;
        this.parent_key = parent_key;
        this.cur_time = cur_time;
        this.state = state;
        this.image = image;
    }

    public String getAchievement() {
        return achievement;
    }

    public String getDate() {
        return date;
    }

    public int getSet_time() {
        return set_time;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSet_time(int set_time) {
        this.set_time = set_time;
    }

    public String getParent_key() {
        return parent_key;
    }

    public void setParent_key(String parent_key) {
        this.parent_key = parent_key;
    }

    public int getCur_time() {
        return cur_time;
    }

    public void setCur_time(int cur_time) {
        this.cur_time = cur_time;
    }

    public String getChild_key() {
        return child_key;
    }

    public void setChild_key(String child_key) {
        this.child_key = child_key;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setInit() {
        Date now = new Date();
        String strNow = DateString.DateToString(now);

        this.setChild_key("자식 key");
        this.setAchievement("test current");
        this.setDate(strNow);
        this.setSet_time(10000);
        this.setCur_time(4000);
        this.setParent_key("부모 key");
        this.setState("test");
    }

    public HashMap toHashMap() {
        HashMap newHashMap = new HashMap();

        newHashMap.put("child_key", this.getChild_key());
        newHashMap.put("achievement", this.getAchievement());
        newHashMap.put("date", this.getDate());
        newHashMap.put("set_time", this.getSet_time());
        newHashMap.put("cur_time", this.getCur_time());
        newHashMap.put("parent_key", this.getParent_key());
        newHashMap.put("state", this.getState());
        newHashMap.put("image", this.getImage());

        return newHashMap;
    }
}