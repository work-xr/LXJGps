package com.hsf1002.sky.xljgps.command;

/**
 * Created by hefeng on 18-8-8.
 */

public class OuterElectricCommand extends BaseCommand{
    //command 107

    public OuterElectricCommand(String imei, int command, String time) {
        super(imei, command, time);
    }

    @Override
    public String toString() {
        return "OuterElectricCommand{" +
                "imei='" + imei + '\'' +
                ", command=" + command +
                ", time='" + time + '\'' +
                '}';
    }
}
