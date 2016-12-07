package uk.ac.ncl.csc8110.huan;

import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.csc8110.huan.model.CameraProfile;
import uk.ac.ncl.csc8110.huan.model.Vehicle;


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
    private final BusTopicService service;
    private boolean isProfileSent;
    private CameraProfile profile;

    public CameraSimulator(){
        this.rate = Config.RATE;
        this.period = (60*1000)/this.rate;
        //
        service = new BusTopicService();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        queue = new LinkedBlockingQueue<BrokeredMessage>();
        isProfileSent = false;
    }

    public void startCamera(){
        initCamera();
        startResendMonitor();
        scheduledExecutorService.scheduleAtFixedRate(this,0,period, TimeUnit.MILLISECONDS);
    }
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

    public void run() {
        Vehicle vehicle = VehicleSimulator.getRandomVehicle(this.profile);
        logger.info("send vehicle: {} to Topic",vehicle.getReg());
        BrokeredMessage message = service.sendVehicleMessage(vehicle);
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

    private void startResendMonitor(){
        logger.info("start Resend Monitor");
        new Thread(new Runnable() {
            public void run() {
                try {
                    BrokeredMessage msg = queue.take();
                    logger.info("ResendMonitor: resending message...");
                    BrokeredMessage message = service.resend(msg);
                    TimeUnit.MILLISECONDS.sleep(Config.RESEND_TIME);
                    while(message!=null) {
                        logger.info("ResendMonitor: resend failed...");
                        logger.info("ResendMonitor: resending message...");
                        message = service.resend(msg);
                        TimeUnit.MILLISECONDS.sleep(Config.RESEND_TIME);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

