package uk.ac.ncl.csc8110.huan.camera;

import com.google.gson.Gson;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;

import com.microsoft.windowsazure.services.servicebus.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.csc8110.huan.camera.model.CameraProfile;
import uk.ac.ncl.csc8110.huan.camera.model.MsgType;
import uk.ac.ncl.csc8110.huan.camera.model.Vehicle;


import java.util.List;


/**
 * Created by huan on 2016/12/3.
 */
public class SendTopic {
    private final Logger logger = LoggerFactory.getLogger(SendTopic.class.getName());
    private ServiceBusContract service;
    private Gson gson;

    public SendTopic(){
        gson = new Gson();
        initService();
        initTopic();
    }

    // connect to  topics ervice
    private ServiceBusContract initService(){
        logger.info("init SendTopic...");
        if(service == null){
            Configuration configuration = ServiceBusConfiguration.configureWithSASAuthentication(
                    Config.NAME_SPACE,
                    Config.KEY_NAME,
                    Config.KEY_VALUE,
                    Config.SERVICE_URL
            );
            service = ServiceBusService.create(configuration);
        }
        logger.info("init SendTopic...success");
        return service;
    }

    // check topic exists.
    private void initTopic()  {
        logger.info("init Topic...");
        try {
            //service.listTopics().getItems();
            List<TopicInfo> list = service.listTopics().getItems();
            for (TopicInfo info : list) {
                if (info.getPath().equalsIgnoreCase(Config.TOPIC_NAME)) {
                    logger.info("init Topic: {} exists", Config.TOPIC_NAME);
                    return;
                }
            }
           // logger.info("{} not exists : try to create", Config.TOPIC_NAME);
            //TopicInfo topicInfo = new TopicInfo(Config.TOPIC_NAME);
            //topicInfo.setEnableBatchedOperations(true);
            //CreateTopicResult result = service.createTopic(topicInfo);
            throw new ServiceException("Topic not found");
        }catch (ServiceException e){
            logger.error("Service Exception {}",e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public BrokeredMessage sendCameraProfile(CameraProfile profile) {

        String msg = gson.toJson(profile);
        //logger.info(msg);
        BrokeredMessage message = new BrokeredMessage(msg);
        message.setProperty("Type", MsgType.Profile);
        try {
            service.sendTopicMessage(Config.TOPIC_NAME, message);
        } catch (ServiceException e) {
            logger.error("sendCameraProfile failed Service Exception {}",e.getMessage());
            return message;
        }
        return null;
    }

    public synchronized BrokeredMessage resend(BrokeredMessage message) {
        try {
            service.sendTopicMessage(Config.TOPIC_NAME, message);

        } catch (ServiceException e) {
            logger.error("resend failed Service Exception {}",e.getMessage());
            return message;
        }
        return null;
    }

    public synchronized BrokeredMessage sendVehicleMessage(Vehicle vehicle){
        String msg = gson.toJson(vehicle);
        BrokeredMessage message = new BrokeredMessage(msg);
        message.setProperty("Type", MsgType.Message);
        if(vehicle.getSpeed()> vehicle.getCameraProfile().getMaxSpeed()){
            message.setProperty("OverSpeed",vehicle.getSpeed()-vehicle.getCameraProfile().getMaxSpeed());
        }else {
            message.setProperty("OverSpeed",0);
        }
        try {
            service.sendTopicMessage(Config.TOPIC_NAME, message);
        } catch (ServiceException e) {
            logger.error("sendVehicleMessage failed Service Exception {}",e.getMessage());
            return message;
        }
        return null;
    }


}

