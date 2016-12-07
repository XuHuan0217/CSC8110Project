package uk.ac.ncl.csc8110.huan.model;

import com.microsoft.azure.storage.table.TableServiceEntity;
import uk.ac.ncl.csc8110.huan.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by huan on 2016/12/5.
 */
public class VehicleEntity extends TableServiceEntity {

    private int type;
    private int speed;
    private Date date;
    private String cameraId;
    private int maxSpeed;
    private String street;
    private String city;
    private Date cameraStartDate;


    public VehicleEntity(String cameraDetial,String reg){
        this.partitionKey = cameraDetial;
        this.rowKey = reg;
    }
    private VehicleEntity(CameraProfile profile,String reg){
        this(profile.getId() +"--"+ Config.DATAFORMAT.format(profile.getStartTime()),reg);
        this.cameraId = profile.getId();
        this.maxSpeed = profile.getMaxSpeed();
        this.street = profile.getStreet();
        this.city = profile.getCity();
        this.cameraStartDate = profile.getStartTime();
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
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

    public Date getCameraStartDate() {
        return cameraStartDate;
    }

    public void setCameraStartDate(Date cameraStartDate) {
        this.cameraStartDate = cameraStartDate;
    }

    public static VehicleEntity transfer(Vehicle vehicle){
        VehicleEntity entity = new VehicleEntity(vehicle.getCameraProfile(),vehicle.getReg());
        entity.setSpeed(vehicle.getSpeed());
        entity.setType(vehicle.getType());
        entity.setDate(vehicle.getDate());
        return entity;
    }
}
