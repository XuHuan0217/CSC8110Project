package uk.ac.ncl.csc8110.huan.vehiclecheck;

import com.google.gson.Gson;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.csc8110.huan.vehiclecheck.model.Vehicle;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.concurrent.TimeUnit;

/**
 * Created by huan on 2016/12/12.
 */
public class QueueReceiver {
    private final static Logger logger = LoggerFactory.getLogger(QueueReceiver.class.getName());
    private static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;" +
                    "AccountName="+Config.STORAGE_NAME+";" +
                    "AccountKey="+Config.STORAGE_KEY;
    private CloudQueue queue;
    private Gson gson;

    public QueueReceiver(){
        logger.info("init Queue Receiver...");
        gson = new Gson();
        try {
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudQueueClient cloudQueueClient = storageAccount.createCloudQueueClient();
            queue = cloudQueueClient.getQueueReference(Config.QUEUE_NAME);
            queue.createIfNotExists();
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        } catch (StorageException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        logger.info("init Queue Receiver success...");
    }
    private Vehicle receiveMessage(){
        try {
            queue.downloadAttributes();
            // Retrieve the newly cached approximate message count.
            long cachedMessageCount = queue.getApproximateMessageCount();
            logger.info("message count-{}",cachedMessageCount);
            if (cachedMessageCount == 0) return null;
            CloudQueueMessage message = queue.retrieveMessage();
            if(message==null) return null;
            queue.deleteMessage(message);
            return gson.fromJson(message.getMessageContentAsString(),Vehicle.class);
        } catch (StorageException e) {
            logger.error("StorageException");
            logger.error(e.getMessage());
        }
        return null;
    }

    public static boolean isVehicleStolen(String vehicleRegistration) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (Math.random() < 0.95);
    }

    public void startQueueReceiver(){
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    Vehicle vehicle = receiveMessage();
                    if (vehicle != null) {
                        boolean stolen = isVehicleStolen(vehicle.getReg());
                        logger.info(vehicle.getReg() + "---" + stolen);
                    } else {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

}
