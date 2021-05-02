package gachon.mpclass.prezzy_pop;

import android.media.Image;

import java.net.URL;
import java.util.Date;

public class BalloonStat {      //상속용
    private int set_time;
    private String achievement;
    private String date;
    private Image present;
    private String parent_key;

    BalloonStat() {}

    BalloonStat(String achievement, String date, int set_time, String parent_key) {
        this.achievement = achievement;
        this.date = date;
        this.parent_key = parent_key;
//        this.present = present;
        this.set_time = set_time;
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

    public int getSet_time() {
        return set_time;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPresent(Image present) {
        this.present = present;
    }

    public void setSet_time(int set_time) {
        this.set_time = set_time;
    }

    public String getParent() { return parent_key; }

    public void setParent(String parent_key) { this.parent_key = parent_key; }
}