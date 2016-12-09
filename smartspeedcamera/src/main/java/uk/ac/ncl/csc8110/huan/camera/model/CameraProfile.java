package uk.ac.ncl.csc8110.huan.camera.model;

import uk.ac.ncl.csc8110.huan.camera.Config;

import java.util.Date;

/**
 * Created by huan on 2016/12/4.
 */
public class CameraProfile {
    private String id;
    private String street;
    private String city;
    private int maxSpeed;
    private Date startTime;

    public static CameraProfile getByConfig(){
        CameraProfile profile = new CameraProfile();
        profile.id = Config.ID;
        profile.street = Config.STREET;
        profile.city = Config.CITY;
        profile.maxSpeed = Config.MAX_SPEED;
        return profile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
