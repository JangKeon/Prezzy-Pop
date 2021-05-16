package gachon.mpclass.prezzy_pop;

public class TimeAlgorithm {

    private double sec;
    private double penaltySec;

    TimeAlgorithm(){};

    TimeAlgorithm(int sec){
        this.sec=sec;
        algorithm();
    }

    public int getPenaltyTime(){
        int penaltyTime=Integer.parseInt(String.valueOf(Math.round(penaltySec)));
        return penaltyTime;
    }

    public void algorithm(){
        this.penaltySec=(sec/2)*baseLog(sec);
    }

    private double baseLog(double x){
        return Math.log(x+1)/Math.log(20);
    }

}
