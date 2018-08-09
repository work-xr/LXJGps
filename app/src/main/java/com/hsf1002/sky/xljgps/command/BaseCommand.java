package com.hsf1002.sky.xljgps.command;

/**
 * Created by hefeng on 18-8-8.
 */

public class BaseCommand {
    protected String imei;
    protected int command;
    protected String time;    // yyyyMMddHHmmss

    public BaseCommand(String imei, int command, String time) {
        this.imei = imei;
        this.command = command;
        this.time = time;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
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
        return "BaseCommand{" +
                "imei='" + imei + '\'' +
                ", command=" + command +
                ", time='" + time + '\'' +
                '}';
    }
}
