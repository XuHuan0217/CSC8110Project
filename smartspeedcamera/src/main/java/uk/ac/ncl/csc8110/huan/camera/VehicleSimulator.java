package uk.ac.ncl.csc8110.huan.camera;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.csc8110.huan.camera.model.RegPlate;
import uk.ac.ncl.csc8110.huan.camera.model.RegPlates;
import uk.ac.ncl.csc8110.huan.camera.model.Vehicle;
import uk.ac.ncl.csc8110.huan.camera.model.CameraProfile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import java.util.Random;

/**
 * Created by huan on 2016/12/3.
 */
public class VehicleSimulator {
    private static final Logger logger = LoggerFactory.getLogger(VehicleSimulator.class.getName());
    public final static int CAR = 0;
    public final static int TRUCK = 1;
    public final static int MOTORCYCLE = 2;
    public static RegPlates regPlates;
    private final static Random random = new Random();



    public static Vehicle getRandomVehicle(CameraProfile profile){

        Vehicle vehicle = new Vehicle();
        randomRegAndType(vehicle);
        randomSpeed(vehicle);
        vehicle.setDate(new Date());
        vehicle.setCameraProfile(profile);
        return vehicle;
    }

    private static void randomRegAndType(Vehicle vehicle){

        if(Config.REG_FILE == null) {
            String p1 = RandomStringUtils.randomAlphabetic(2).toUpperCase();
            int p2 = RandomUtils.nextInt(10, 100);
            String p3 = RandomStringUtils.randomAlphabetic(3).toUpperCase();
            vehicle.setReg(p1 + p2 + " " + p3);
            vehicle.setType(RandomUtils.nextInt(0,3));
        }else {
            if(regPlates == null) {
                try {
                    Gson gson = new Gson();
                    JsonReader reader = new JsonReader(new FileReader(Config.REG_FILE));
                    regPlates = gson.fromJson(reader, RegPlates.class);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
            RegPlate regPlate = regPlates.getRandomReg();
            vehicle.setReg(regPlate.getReg());
            vehicle.setType(regPlate.getType());
        }
    }


    private static int randomSpeed(Vehicle vehicle){
        int re =(int)(random.nextGaussian()*Config.GAUSSIAN_DEVIATION+Config.MAX_SPEED-10);
        if(re<0) re = 0;
        vehicle.setSpeed(re);
        return re;
    }
    public static void main(String [] args){
        int max = 80;
        for(int i = 0;i<20;i++) {
            int result = (int) (random.nextGaussian() *15+ max-10);
            System.out.println(result);
        }
    }

}
