package com.hsf1002.sky.xljgps.result;

/**
 * Created by hefeng on 18-6-7.
 */

public class RelationNumberMsg {

    private String relationship;
    private String phone;

    public String getRelationship() {
        return relationship;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "RelationNumberMsg{" +
                "relationship='" + relationship + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

}
