package gachon.mpclass.prezzy_pop;

public class Cur_balloon extends BalloonStat{
    private int cur_time;

    public Cur_balloon() {}

    Cur_balloon(String achievement, String date, int set_time, int cur_time) {
        super(achievement,date, set_time);
        this.cur_time = cur_time;
    }

    public int getCur_time() {
        return cur_time;
    }

    public void setCur_time(int cur_time) {
        this.cur_time = cur_time;
    }
}