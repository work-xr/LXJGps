package com.hsf1002.sky.xljgps.command;

/**
 * Created by hefeng on 18-8-8.
 */

public class IntervalCommand {
    private String interval;    // 600s
    private int command;
    private String time;

    public IntervalCommand(String interval, int command, String time) {
        this.interval = interval;
        this.command = command;
        this.time = time;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "IntervalCommand{" +
                "interval='" + interval + '\'' +
                ", command=" + command +
                ", time='" + time + '\'' +
                '}';
    }
}
