package com.brick.robotctrl;

/**
 * Created by kjnijk on 2016-06-18.
 */
public class UserTimer {

    private static int timerOutCount = 0;

    public void clearTimerCount() {
        this.timerOutCount = 0;
    }

    public void addTimerCount() {
        this.timerOutCount++;
    }

    public int getTimerCount() {
        return this.timerOutCount;
    }
}
