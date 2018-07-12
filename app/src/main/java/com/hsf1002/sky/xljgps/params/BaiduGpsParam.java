package com.hsf1002.sky.xljgps.params;

/**
 * Created by hefeng on 18-6-15.
 */

public class BaiduGpsParam {
    protected String position_type;
    protected String loc_type;
    protected String longitude;
    protected String latitude;

    public BaiduGpsParam() {
    }

    public BaiduGpsParam(String position_type, String loc_type, String longitude, String latitude) {
        this.position_type = position_type;
        this.loc_type = loc_type;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getPosition_type() {
        return position_type;
    }

    public String getLoc_type() {
        return loc_type;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setPosition_type(String position_type) {
        this.position_type = position_type;
    }

    public void setLoc_type(String loc_type) {
        this.loc_type = loc_type;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
