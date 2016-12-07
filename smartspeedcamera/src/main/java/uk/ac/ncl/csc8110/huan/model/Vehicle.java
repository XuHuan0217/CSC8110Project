package uk.ac.ncl.csc8110.huan.model;

import uk.ac.ncl.csc8110.huan.Config;

import java.util.Date;

/**
 * Created by huan on 2016/12/3.
 */
public class Vehicle {

    private String reg;
    private int type;
    private int speed;
    private Date date;
    private CameraProfile cameraProfile;


    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public CameraProfile getCameraProfile() {
        return cameraProfile;
    }

    public void setCameraProfile(CameraProfile cameraProfile) {
        this.cameraProfile = cameraProfile;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
