package com.hsf1002.sky.xljgps.command;

/**
 * Created by hefeng on 18-8-8.
 */

public class GetStatusCommand extends BaseCommand{
   //command 108


    public GetStatusCommand(String imei, int command, String time) {
        super(imei, command, time);
    }

    @Override
    public String toString() {
        return "GetStatusCommand{" +
                "imei='" + imei + '\'' +
                ", command=" + command +
                ", time='" + time + '\'' +
                '}';
    }
}
