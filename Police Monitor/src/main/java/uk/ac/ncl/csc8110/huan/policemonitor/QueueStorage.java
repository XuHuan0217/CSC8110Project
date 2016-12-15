package uk.ac.ncl.csc8110.huan.policemonitor;

import com.google.gson.Gson;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.csc8110.huan.policemonitor.model.OverSpeedVehicleEntity;
import uk.ac.ncl.csc8110.huan.policemonitor.model.Vehicle;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by huan on 2016/12/12.
 */
public class QueueStorage {
    private final static Logger logger = LoggerFactory.getLogger(QueueStorage.class.getName());
    private static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;" +
                    "AccountName="+Config.STORAGE_QUEUE_NAME+";" +
                    "AccountKey="+Config.STORAGE_QUEUE_KEY;
    private CloudQueue queue;
    private Gson gson;

    public QueueStorage(){
        logger.info("init Queue Storage...");
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
        logger.info("init Queue Storage success...");
    }

    public CloudQueueMessage sendMessage(Vehicle entity){
        CloudQueueMessage message = new CloudQueueMessage(gson.toJson(entity));
        try {
            queue.addMessage(message);
        } catch (StorageException e) {
            logger.error(e.getMessage());
            return message;
        }
        return null;
    }

}
