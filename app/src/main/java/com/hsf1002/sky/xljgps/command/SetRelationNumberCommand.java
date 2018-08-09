package com.hsf1002.sky.xljgps.command;

/**
 * Created by hefeng on 18-8-8.
 */

public class SetRelationNumberCommand extends BaseCommand {
    // command 103

    private String sos_phone;
    private String name;

    public SetRelationNumberCommand(String imei, int command, String time, String sos_phone, String name) {
        super(imei, command, time);

        this.sos_phone = sos_phone;
        this.name = name;
    }

    public String getSos_phone() {
        return sos_phone;
    }

    public void setSos_phone(String sos_phone) {
        this.sos_phone = sos_phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SetRelationNumberCommand{" +
                "imei='" + imei + '\'' +
                ", command=" + command +
                ", sos_phone='" + sos_phone + '\'' +
                ", time='" + time + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
