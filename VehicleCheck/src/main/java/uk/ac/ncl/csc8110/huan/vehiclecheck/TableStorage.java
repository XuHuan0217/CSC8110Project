package uk.ac.ncl.csc8110.huan.vehiclecheck;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableBatchOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.csc8110.huan.vehiclecheck.model.OverSpeedVehicleEntity;
import uk.ac.ncl.csc8110.huan.vehiclecheck.model.Vehicle;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by huan on 2016/12/5.
 */
public class TableStorage {
    private final static Logger logger = LoggerFactory.getLogger(TableStorage.class.getName());
    private static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;" +
                    "AccountName="+Config.STORAGE_NAME+";" +
                    "AccountKey="+Config.STORAGE_KEY;
    private final CloudTableClient cloudTableClient;
    private CloudTable policeTable;
    private ConcurrentHashMap<String,ConcurrentLinkedQueue<OverSpeedVehicleEntity>> cache;
    private TableBatchOperation tableOperations;

    public TableStorage(){
        logger.info("init TableStorage ...");
        CloudStorageAccount account = null;
        try {
            account = CloudStorageAccount.parse(storageConnectionString);
        } catch (URISyntaxException e) {
            logger.error("URISyntaxException");
            logger.error(e.getMessage());
            System.exit(-1);
        } catch (InvalidKeyException e) {
            logger.error("InvalidKeyException");
            logger.error(e.getMessage());
            System.exit(-1);
        }
        cloudTableClient = account.createCloudTableClient();
        this.cache = new ConcurrentHashMap<String, ConcurrentLinkedQueue<OverSpeedVehicleEntity>>();
        tableOperations = new TableBatchOperation();
        initTable();
        logger.info("init TableStorage success...");
    }
    private void initTable(){
        logger.info("init Table");
        try {
            policeTable = cloudTableClient.getTableReference(Config.VC_TABLE_NAME);
            policeTable.createIfNotExists();
        } catch (URISyntaxException e) {
            logger.error("URISyntaxException");
            logger.error(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        } catch (StorageException e) {
            logger.error("StorageException");
            logger.error(e.getMessage());
            logger.error(e.getExtendedErrorInformation().getErrorMessage());

            System.exit(-1);
        }
    }

//    public synchronized CameraProfile insertProfile(CameraProfile profile){
//        logger.debug("insert profile {}", profile.getId()+profile.getStartTime());
//        CameraProfileEntity entity = CameraProfileEntity.transfer(profile);
//        TableOperation operation = TableOperation.insertOrReplace(entity);
//        try {
//            cameraTable.execute(operation);
//        } catch (StorageException e) {
//            logger.info("StorageException... Retry");
//            logger.info(e.getMessage());
//            return profile;
//        }
//        return null;
//    }

    public void insertVehicle(Vehicle vehicle){
        //logger.debug("insert Vehicle {}",vehicle.getReg());
        OverSpeedVehicleEntity entity = OverSpeedVehicleEntity.transfer(vehicle);
        if (entity.isPriority()){
            logger.info("<PRIORITY>"+"--"+entity.getRowKey()+", "+entity.getSpeed()+" > "+entity.getMaxSpeed());
        }else {
            logger.info("<NON-PRIORITY>"+"--"+entity.getRowKey()+", "+entity.getSpeed()+" > "+entity.getMaxSpeed());
        }
        if(!cache.containsKey(entity.getPartitionKey())){
            cache.put(entity.getPartitionKey(),new ConcurrentLinkedQueue<OverSpeedVehicleEntity>());
        }
        ConcurrentLinkedQueue<OverSpeedVehicleEntity> queue = cache.get(entity.getPartitionKey());
        queue.offer(entity);
        if(queue.size()>=Config.BATCH_SIZE){
            for(OverSpeedVehicleEntity entity1: queue){
                tableOperations.insertOrReplace(entity1);
            }
            try {
                policeTable.execute(tableOperations);
                tableOperations.clear();
                queue.clear();
            } catch (StorageException e) {
                logger.info("StorageException... Retry");
                logger.info(e.getMessage());
            }
        }
    }

    public static void main(String []args){
        TableStorage tableStorage = new TableStorage();

    }


}
