package gachon.mpclass.prezzy_pop;

import android.media.Image;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;

public class BalloonStat {      //상속용
    private String child_key;
    private int set_time;
    private String achievement;
    private String date;
    private Image present;
    private String parent_key;
    private int cur_time;
    private String state;

    BalloonStat() {}

    BalloonStat(String child_key, String achievement, String date, int set_time, int cur_time, String parent_key, String state) {
        this.child_key = child_key;
        this.achievement = achievement;
        this.date = date;
        this.parent_key = parent_key;
//        this.present = present;
        this.set_time = set_time;
        this.cur_time = cur_time;
        this.state = state;

    }

    public String getAchievement() {
        return achievement;
    }

    public String getDate() {
        return date;
    }

    public Image getPresent() {
        return present;
    }

    public void setPresent(Image present) {
        this.present = present;
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

        return newHashMap;
    }
}