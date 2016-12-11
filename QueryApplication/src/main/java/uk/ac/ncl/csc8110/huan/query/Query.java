package uk.ac.ncl.csc8110.huan.query;

import com.google.gson.Gson;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.csc8110.huan.query.model.CameraProfileEntity;
import uk.ac.ncl.csc8110.huan.query.model.OverSpeedVehicleEntity;
import uk.ac.ncl.csc8110.huan.query.model.VehicleEntity;


import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.*;


/**
 * Created by huan on 2016/12/5.
 */
public class Query {
    private final static Logger logger = LoggerFactory.getLogger(Query.class.getName());
    private static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;" +
                    "AccountName="+Config.STORAGE_NAME+";" +
                    "AccountKey="+Config.STORAGE_KEY;
    private final CloudTableClient cloudTableClient;
    private CloudTable policeTable;
    private CloudTable cameraTable;
    private CloudTable vehicleTable;
    private final Gson gson;

    private final String PARTITION_KEY = "PartitionKey";
    private final String ROW_KEY = "RowKey";
    private final String TIMESTAMP = "Timestamp";

    public Query(){
        logger.info("init Query ...");
        gson = new Gson();
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
        initTable();
        logger.info("init Query success...");
    }
    private void initTable(){
        logger.info("init Table");
        try {
            policeTable = cloudTableClient.getTableReference(Config.POLICE_TABLE_NAME);
            cameraTable = cloudTableClient.getTableReference(Config.CAMERA_TABLE_NAME);
            vehicleTable = cloudTableClient.getTableReference(Config.VEHICLE_TABLE_NAME);
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
    public void printAllCamera(){
        TableQuery<CameraProfileEntity> query = TableQuery.from(CameraProfileEntity.class);
        logger.info("print All Camera");
        for(CameraProfileEntity entity:cameraTable.execute(query)){
            Gson gson = new Gson();
            logger.info(gson.toJson(entity));
        }
    }

    public void printPriorityOverSpeedVehicle(){
        String partitionFilter = TableQuery.generateFilterCondition("Priority",
                TableQuery.QueryComparisons.EQUAL,
                true);
        TableQuery<OverSpeedVehicleEntity> partitionQuery = TableQuery.from(OverSpeedVehicleEntity.class)
                .where(partitionFilter);

        Map<OverSpeedVehicleEntity,List<VehicleEntity>> maps = new HashMap<OverSpeedVehicleEntity, List<VehicleEntity>>();

        for( OverSpeedVehicleEntity entity:policeTable.execute(partitionQuery)){
            String reg4Vehicle = entity.getReg();
            String pFilter = TableQuery.generateFilterCondition("Reg", TableQuery.QueryComparisons.EQUAL,
                    reg4Vehicle);
            TableQuery<VehicleEntity>  vehicleEntityTableQuery = TableQuery.from(VehicleEntity.class).where(pFilter);
            List<VehicleEntity> vehicleList = new ArrayList<VehicleEntity>();
            for(VehicleEntity vehicleEntity:vehicleTable.execute(vehicleEntityTableQuery)){
                vehicleList.add(vehicleEntity);
            }
            maps.put(entity,vehicleList);
        }
        int i = 0;

        for(Map.Entry<OverSpeedVehicleEntity,List<VehicleEntity>> pairs: maps.entrySet()){
            i++;
            System.out.println("Priority Over Speed Vehicle-- "+i);
            System.out.println(gson.toJson(pairs.getKey()));
            for(VehicleEntity entity:pairs.getValue()){
                System.out.println("History--->"+gson.toJson(entity));
            }

        }
    }



}
