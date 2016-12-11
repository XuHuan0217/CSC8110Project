package uk.ac.ncl.csc8110.huan.query.model;

import com.microsoft.azure.storage.table.TableServiceEntity;
import uk.ac.ncl.csc8110.huan.query.Config;


import java.util.Date;

/**
 * Created by huan on 2016/12/5.
 */
public class CameraProfileEntity extends TableServiceEntity {
    private String street;
    private String city;
    private int maxSpeed;


    public CameraProfileEntity(String id,Date startTime){
        this.partitionKey = id;
        this.rowKey = Config.DATAFORMAT.format(startTime);
    }
    public CameraProfileEntity(){

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

    public static CameraProfileEntity transfer(CameraProfile profile){
        CameraProfileEntity entity = new CameraProfileEntity(profile.getId(),profile.getStartTime());
        entity.setCity(profile.getCity());
        entity.setMaxSpeed(profile.getMaxSpeed());
        entity.setStreet(profile.getStreet());
        return entity;
    }

}
