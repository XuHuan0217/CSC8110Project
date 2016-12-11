package uk.ac.ncl.csc8110.huan.vehiclecheck.model;

import com.microsoft.azure.storage.table.TableServiceEntity;
import uk.ac.ncl.csc8110.huan.vehiclecheck.Config;

import java.util.Date;

/**
 * Created by huan on 2016/12/5.
 */
public class OverSpeedVehicleEntity extends TableServiceEntity {
    private int type;
    private int speed;
    private Date date;
    private String reg;

    private String cameraId;
    private int maxSpeed;
    private String street;
    private String city;
    private Date cameraStartDate;



    private boolean priority;
    private int overSpeed;



    public OverSpeedVehicleEntity(String cameraDetail,String regAndTime){
        this.partitionKey = cameraDetail;
        this.rowKey = regAndTime;
    }

    private OverSpeedVehicleEntity(CameraProfile profile,String reg,Date date){
        this(profile.getId() +"-"+ Config.DATAFORMAT.format(profile.getStartTime())
                ,reg+"-"+Config.DATAFORMAT.format(date));
        this.cameraId = profile.getId();
        this.maxSpeed = profile.getMaxSpeed();
        this.street = profile.getStreet();
        this.city = profile.getCity();
        this.cameraStartDate = profile.getStartTime();
    }
    public OverSpeedVehicleEntity(){}

    public int getOverSpeed() {
        return overSpeed;
    }

    public void setOverSpeed(int overSpeed) {
        this.overSpeed = overSpeed;
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

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }
    public boolean getPriority(){
        return priority;
    }

    public static OverSpeedVehicleEntity transfer(Vehicle vehicle){
        boolean pri;
        if ((double)vehicle.getSpeed()>(double)vehicle.getCameraProfile().getMaxSpeed()*1.1){
            pri = true;
        }else {
            pri = false;
        }
        OverSpeedVehicleEntity entity = new OverSpeedVehicleEntity(vehicle.getCameraProfile(),
                vehicle.getReg(),vehicle.getDate());
        entity.setSpeed(vehicle.getSpeed());
        entity.setType(vehicle.getType());
        entity.setDate(vehicle.getDate());
        entity.setReg(vehicle.getReg());
        entity.setOverSpeed(entity.getSpeed() - entity.getMaxSpeed() );
        entity.setPriority(pri);
        return entity;
    }
}
