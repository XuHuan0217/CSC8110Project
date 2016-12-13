package uk.ac.ncl.csc8110.huan.nosqlconsumer;

import com.google.gson.Gson;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.*;
import org.apache.qpid.jms.message.JmsBytesMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.csc8110.huan.nosqlconsumer.model.CameraProfile;
import uk.ac.ncl.csc8110.huan.nosqlconsumer.model.MsgType;
import uk.ac.ncl.csc8110.huan.nosqlconsumer.model.Vehicle;

import javax.jms.*;
import javax.jms.ExceptionListener;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by huan on 2016/12/5.
 */
public class TopicReceiver implements MessageListener{

    private final static Logger logger = LoggerFactory.getLogger(TopicReceiver.class.getName());
    private final Connection connection;
    private Session session;
    private TopicSubscriber subscriber;
    private final Topic topic;
    private final Gson gson;
    private ServiceBusContract service;
    private TableStorage tableStorage;
    private long count = 0;


    public TopicReceiver() throws NamingException, JMSException {
        logger.info("init Topic Receiver...");
        //env.put(Context.INITIAL_CONTEXT_FACTORY,"org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        //env.put(Context.PROVIDER_URL,"jndi.properties");
        //create AMQP Connection
        InitialContext context = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCONNECTIONFACTORY");
        this.topic = (Topic)context.lookup("TOPIC");
        this.connection = cf.createConnection(Config.KEY_NAME,Config.KEY_VALUE);
        this.connection.setExceptionListener(new MyExceptionListener());

        this.gson = new Gson();
        //create table storage connection.
        this.tableStorage = new TableStorage();
        initService().initTopic().initSubscriber();
    }
    private TopicReceiver initService(){
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
        return this;
    }

    private TopicReceiver initTopic() throws JMSException {
        logger.info("init Topic...");
        try {
            List<TopicInfo> list = service.listTopics().getItems();
            for (TopicInfo info : list) {
                if (info.getPath().equalsIgnoreCase(topic.getTopicName())) {
                    logger.info(" Topic: {} already exists", topic.getTopicName());
                    return this;
                }
            }
            logger.info("{} not exists : try to create", topic.getTopicName());
            TopicInfo topicInfo = new TopicInfo(topic.getTopicName());
            topicInfo.setEnableBatchedOperations(true);
            CreateTopicResult result = service.createTopic(topicInfo);
        }catch (ServiceException e){
            logger.error("Service Exception {}",e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return this;
    }

    private TopicReceiver initSubscriber() throws JMSException {
        logger.info("init Subscriber {}",Config.SUB_NAME);
        try {
            List<SubscriptionInfo> subs = service.listSubscriptions(topic.getTopicName()).getItems();
            for(SubscriptionInfo info :subs){
                if(info.getName().equalsIgnoreCase(Config.SUB_NAME)) {
                    logger.info("Subscriber: {} already exists", topic.getTopicName());
                    return this;
                }
            }
            SubscriptionInfo subInfo = new SubscriptionInfo(Config.SUB_NAME);
            service.createSubscription(topic.getTopicName(), subInfo);
        } catch (ServiceException e) {
            logger.error("Service Exception {}",e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return this;
    }

    public void runReceiverAsynchronous() throws JMSException {
        connection.setClientID("NoSQLConsumer");
        session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        subscriber = session.createDurableSubscriber(topic,Config.SUB_NAME);
        subscriber.setMessageListener(this);
        connection.start();

    }


    public void onMessage(Message message) {
        try {
            //read message
            JmsBytesMessage bytesMessage = (JmsBytesMessage) message;
            String type = bytesMessage.getStringProperty("Type");
            int length = new Long(bytesMessage.getBodyLength()).intValue();
            byte[] data = new byte[length];
            bytesMessage.readBytes(data, length);

            if(MsgType.valueOf(type) == MsgType.Profile) {
                CameraProfile profile = gson.fromJson(new String(data),CameraProfile.class);
                logger.info("receive camera profile:{}",profile.getId());
                CameraProfile pro = tableStorage.insertProfile(profile);
                while (pro!=null){
                    TimeUnit.SECONDS.sleep(1);
                    pro = tableStorage.insertProfile(profile);
                }
            }else if (MsgType.valueOf(type) == MsgType.Message){
                count++;
                if (count%100 == 0){
                    logger.info("count:"+count);
                }
                //int overSpeed = bytesMessage.getIntProperty("OverSpeed");
                Vehicle vehicle = gson.fromJson(new String(data), Vehicle.class);
                logger.debug("receive vehicle:{}",vehicle.getReg());
                tableStorage.insertVehicle(vehicle);
            }else {
                logger.info("invalid message");
            }
        } catch (JMSException e) {
            logger.error("JMS Exception.");
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            logger.error("InterruptedException.");
            logger.error(e.getMessage());
            System.exit(-1);
        }
    }

    private static class MyExceptionListener implements ExceptionListener {
        public void onException(JMSException exception) {
            logger.error("Connection ExceptionListener failed.");
            logger.error(exception.getMessage());
            System.exit(-1);
        }
    }
}
