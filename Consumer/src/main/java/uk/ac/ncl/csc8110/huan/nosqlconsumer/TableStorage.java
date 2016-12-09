package uk.ac.ncl.csc8110.huan.nosqlconsumer;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableBatchOperation;
import com.microsoft.azure.storage.table.TableOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.csc8110.huan.nosqlconsumer.model.CameraProfile;
import uk.ac.ncl.csc8110.huan.nosqlconsumer.model.CameraProfileEntity;
import uk.ac.ncl.csc8110.huan.nosqlconsumer.model.Vehicle;
import uk.ac.ncl.csc8110.huan.nosqlconsumer.model.VehicleEntity;

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
    private CloudTable cameraTable;
    private CloudTable vehicleTable;
    private ConcurrentHashMap<String,ConcurrentLinkedQueue<VehicleEntity>> cache;
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
        this.cache = new ConcurrentHashMap<String, ConcurrentLinkedQueue<VehicleEntity>>();
        tableOperations = new TableBatchOperation();
        initTable();
        logger.info("init TableStorage success...");
    }
    private void initTable(){
        logger.info("init Table");
        try {
            cameraTable = cloudTableClient.getTableReference(Config.CAMERA_TABLE_NAME);
            cameraTable.createIfNotExists();
            vehicleTable = cloudTableClient.getTableReference(Config.VEHICLE_TABLE_NAME);
            vehicleTable.createIfNotExists();
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

    public synchronized CameraProfile insertProfile(CameraProfile profile){
        logger.debug("insert profile {}", profile.getId()+profile.getStartTime());
        CameraProfileEntity entity = CameraProfileEntity.transfer(profile);
        TableOperation operation = TableOperation.insertOrReplace(entity);
        try {
            cameraTable.execute(operation);
        } catch (StorageException e) {
            logger.info("StorageException... Retry");
            logger.info(e.getMessage());
            return profile;
        }
        return null;
    }

    public void insertVehicle(Vehicle vehicle){
        logger.debug("insert Vehicle {}",vehicle.getReg());
        VehicleEntity entity = VehicleEntity.transfer(vehicle);
        if(!cache.containsKey(entity.getPartitionKey())){
            cache.put(entity.getPartitionKey(),new ConcurrentLinkedQueue<VehicleEntity>());
        }

        ConcurrentLinkedQueue<VehicleEntity> queue = cache.get(entity.getPartitionKey());
        queue.offer(entity);
        if(queue.size()>=Config.BATCH_SIZE){
            for(VehicleEntity entity1: queue){
                tableOperations.insertOrReplace(entity1);
            }
            try {
                vehicleTable.execute(tableOperations);
                tableOperations.clear();
                queue.clear();
            } catch (StorageException e) {
                logger.info("StorageException... Retry");
                logger.info(e.getMessage());
            }
        }
    }



}
