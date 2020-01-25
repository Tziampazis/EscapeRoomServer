package Server;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimerTime extends Thread {

    private long start;
    private long stop;
    private long totalTime = 1800;
    public GameServer server;
    boolean run = true;
    boolean lose = true;


    public TimerTime(GameServer srv) {

        this.server = srv;
    }


    @Override
    public void run() {
        while (run) {
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                // FIXME Auto-generated catch block
                e.printStackTrace();
            }

            if (System.currentTimeMillis() / 1000 - getStart() >= getBound()) {
                System.out.println("TIME IS UP!!!");
                run = false;
            }
            System.out.println(" TIME RUNNING  :  " + (System.currentTimeMillis() / 1000 - getStart()));
        }

        if(lose){
            this.server.stopGame();
        }
    }

    public void setStart(long st) {
        this.start = st;
    }

    public long getStart() {
        return this.start;
    }

    public void setBound(long st) {
        this.totalTime = st;
    }

    public long getBound() {
        return this.totalTime;
    }

    public void timePenalty() {
        setBound(this.totalTime - 30);
        System.out.println("THE total time is : " + this.totalTime);
    }

    public String getTime() {
        long tm = System.currentTimeMillis()/1000-getStart();
        Date date = new Date((long)(tm*1000));
        String formattedDate = new SimpleDateFormat("mm:ss").format(date);
        return formattedDate;
    }

    public long getCTime(){
        return System.currentTimeMillis()/1000-getStart();
    }
    public void setLose(){
        this.lose= false;
    }

    public void endTimer(){
        setLose();
        this.run = false;
    }
}
