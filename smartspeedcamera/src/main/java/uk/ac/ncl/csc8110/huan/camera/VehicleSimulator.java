package uk.ac.ncl.csc8110.huan.camera;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import uk.ac.ncl.csc8110.huan.camera.model.Vehicle;
import uk.ac.ncl.csc8110.huan.camera.model.CameraProfile;
import java.util.Date;
import java.util.Random;

/**
 * Created by huan on 2016/12/3.
 */
public class VehicleSimulator {
    public final static int CAR = 0;
    public final static int TRUCK = 1;
    public final static int MOTORCYCLE = 2;
    private final static Random random = new Random();



    public static Vehicle getRandomVehicle(CameraProfile profile){
        Vehicle vehicle = new Vehicle();
        vehicle.setReg(randomReg());
        vehicle.setType(randomType());
        vehicle.setSpeed(randomSpeed());
        vehicle.setDate(new Date());
        vehicle.setCameraProfile(profile);
        return vehicle;
    }

    private static String randomReg(){
        String p1 = RandomStringUtils.randomAlphabetic(2).toUpperCase();
        int p2 = RandomUtils.nextInt(10,100);
        String p3 = RandomStringUtils.randomAlphabetic(3).toUpperCase();
        return p1+p2+" "+p3;
    }
    private static int randomType(){
       return RandomUtils.nextInt(0,3);
    }

    private static int randomSpeed(){
        int re = (int)random.nextGaussian()*Config.GAUSSIAN_DEVIATION+Config.GAUSSIAN_MEAN;
        if(re<0) re = 0;
        return re;
    }

}
