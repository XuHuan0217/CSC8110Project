package uk.ac.ncl.csc8110.huan.camera;

import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.csc8110.huan.camera.model.CameraProfile;
import uk.ac.ncl.csc8110.huan.camera.model.Vehicle;


import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by huan on 2016/12/3.
 */
public class CameraSimulator implements Runnable{
    private final Logger logger = LoggerFactory.getLogger(CameraSimulator.class.getName());
    private final int rate;
    private final int period;
    private final ScheduledExecutorService scheduledExecutorService;
    private final BlockingQueue<BrokeredMessage> queue;
    private final SendTopic service;
    private boolean isProfileSent;
    private CameraProfile profile;
    private boolean offlineTest = false;

    public CameraSimulator(boolean offlineTest){
        this.offlineTest = offlineTest;
        //calculate period for sending.
        this.rate = Config.RATE;
        this.period = (60*1000)/this.rate;
        //create send topic service
        service = new SendTopic();
        // init scheduled executor service
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // init local queue for message storage
        queue = new LinkedBlockingQueue<BrokeredMessage>();
        isProfileSent = false;
    }

    public void startCamera(){
        initCamera();
        startResendMonitor();
        scheduledExecutorService.scheduleAtFixedRate(this,0,period, TimeUnit.MILLISECONDS);
    }
    //send camera information to topic
    private void initCamera(){
        if(!isProfileSent){
            logger.info("Sending Camera profile to Topic");
            profile = CameraProfile.getByConfig();
            profile.setStartTime(new Date());
            BrokeredMessage message = service.sendCameraProfile(profile);
            while (message!=null){
                logger.info("Communication Fail try to send again");
                try {
                    TimeUnit.MILLISECONDS.sleep(Config.RESEND_TIME);
                } catch (InterruptedException e) {
                    logger.error("sleep interrupted Exception");
                    System.exit(0);
                }
                message = service.resend(message);
            }
            isProfileSent = true;
        }
    }

    //send vehicle information to topic
    //store vehicle information if off-line
    public void run() {
        Vehicle vehicle = VehicleSimulator.getRandomVehicle(this.profile);
        logger.info("send vehicle: {} to Topic",vehicle.getReg());
        BrokeredMessage message = service.sendVehicleMessage(vehicle,this.offlineTest);
        if (message != null) {
            logger.info("sending failed..");
            try {
                queue.put(message);
            } catch (InterruptedException e) {
                logger.error("Queue interrupted Exception {}",e.getMessage());
                System.exit(-1);
            }
        }
    }

    // resend local stored message
    private void startResendMonitor(){
        logger.info("start Resend Monitor");
        new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        BrokeredMessage msg = queue.take();
                        logger.info("ResendMonitor: resending message...");
                        BrokeredMessage message = service.resend(msg);
                        TimeUnit.MILLISECONDS.sleep(Config.RESEND_TIME);
                        while (message != null) {
                            logger.info("ResendMonitor: resend failed...");
                            logger.info("ResendMonitor: resending message...");
                            message = service.resend(msg);
                            TimeUnit.MILLISECONDS.sleep(Config.RESEND_TIME);
                        }
                        logger.info("ResendMonitor: resend success ");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

