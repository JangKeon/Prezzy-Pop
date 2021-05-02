package gachon.mpclass.prezzy_pop;

public class Pre_balloon extends BalloonStat{
    private int result;

    Pre_balloon(){}

    Pre_balloon(String achievement, String date, int set_time, int result) {
        super(achievement, date, set_time);
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) { this.result = result; }
}
