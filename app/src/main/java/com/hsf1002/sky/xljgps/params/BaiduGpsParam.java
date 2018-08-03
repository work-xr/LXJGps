package com.hsf1002.sky.xljgps.params;

/**
 * Created by hefeng on 18-6-15.
 */

public class BaiduGpsParam {
    //protected String address;       // 暂时不用
    protected String position_type;
    protected String loc_type;
    protected String lon;
    protected String lat;

    public BaiduGpsParam() {
    }

    public BaiduGpsParam(String position_type, String loc_type, String longitude, String latitude) {
        this.position_type = position_type;
        this.loc_type = loc_type;
        this.lon = longitude;
        this.lat = latitude;
    }

    public String getPosition_type() {
        return position_type;
    }

    public String getLoc_type() {
        return loc_type;
    }

    public String getLongitude() {
        return lon;
    }

    public String getLatitude() {
        return lat;
    }

    public void setPosition_type(String position_type) {
        this.position_type = position_type;
    }

    public void setLoc_type(String loc_type) {
        this.loc_type = loc_type;
    }

    public void setLongitude(String longitude) {
        this.lon = longitude;
    }

    public void setLatitude(String latitude) {
        this.lat = latitude;
    }
/*
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
*/
    @Override
    public String toString() {
        return "BaiduGpsParam{" +
                //"address='" + address + '\'' +
                ", position_type='" + position_type + '\'' +
                ", loc_type='" + loc_type + '\'' +
                ", longitude='" + lon + '\'' +
                ", latitude='" + lat + '\'' +
                '}';
    }
}
