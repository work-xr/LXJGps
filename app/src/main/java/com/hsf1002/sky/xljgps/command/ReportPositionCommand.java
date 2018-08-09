package com.hsf1002.sky.xljgps.command;

/**
 * Created by hefeng on 18-8-8.
 */

public class ReportPositionCommand extends BaseCommand {
    // command 101


    public ReportPositionCommand(String imei, int command, String time) {
        super(imei, command, time);
    }

    @Override
    public String toString() {
        return "ReportPositionCommand{" +
                "imei='" + imei + '\'' +
                ", command=" + command +
                ", time='" + time + '\'' +
                '}';
    }
}
