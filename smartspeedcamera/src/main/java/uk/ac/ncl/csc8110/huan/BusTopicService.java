package uk.ac.ncl.csc8110.huan;

import com.google.gson.Gson;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;

import com.microsoft.windowsazure.services.servicebus.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.csc8110.huan.model.CameraProfile;
import uk.ac.ncl.csc8110.huan.model.MsgType;
import uk.ac.ncl.csc8110.huan.model.Vehicle;


import java.beans.ExceptionListener;
import java.util.Date;
import java.util.List;


/**
 * Created by huan on 2016/12/3.
 */
public class BusTopicService {
    private final Logger logger = LoggerFactory.getLogger(BusTopicService.class.getName());
    private ServiceBusContract service;
    private Gson gson;

    public BusTopicService(){
        gson = new Gson();
        initService();
        initTopic();
    }

    private ServiceBusContract initService(){
        logger.info("init BusTopicService...");
        if(service == null){
            Configuration configuration = ServiceBusConfiguration.configureWithSASAuthentication(
                    Config.NAME_SPACE,
                    Config.KEY_NAME,
                    Config.KEY_VALUE,
                    Config.SERVICE_URL
            );
            service = ServiceBusService.create(configuration);
        }
        logger.info("init BusTopicService...success");
        return service;
    }

    private void initTopic()  {
        logger.info("init Topic...");
        try {
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
            message.setProperty("OverSpeed",vehicle.getCameraProfile().getMaxSpeed()-vehicle.getSpeed());
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

