package uk.ac.ncl.csc8110.huan.vehiclecheck;


import java.text.SimpleDateFormat;

/**
 * Created by huan on 2016/12/3.
 */
public class Config {

    final static String NAME_SPACE = "huantopic";
    final static String KEY_NAME = "RootManageSharedAccessKey";
    final static String KEY_VALUE = "Owd+jQu2a3ppWPbo6nM2NCO+qFyeuDE1GYWs8GyL3wg=";
    final static String SERVICE_URL=".servicebus.windows.net";

    final static String SUB_NAME="VehicleCheck";
    final static String QUEUE_NAME="policemonitorqueue";

    final static String STORAGE_NAME="huanclassic";
    final static String STORAGE_KEY="dgxvkzsy6SQGml1uDWq6WKFA2qpMV8zxEFBzLxfwUVsviaUFyA22nohP6JkKa8Wo995S30WgoPc+ns9PptVMtA==";

//    final static String CAMERA_TABLE_NAME="tablecamera";
//    final static String VEHICLE_TABLE_NAME="tablevehicle";
//    final static String POLICE_TABLE_NAME="tablepolice";
    final static String VC_TABLE_NAME="tablevehiclecheck";

   // final static int BATCH_SIZE = 2;
    public final static SimpleDateFormat DATAFORMAT = new SimpleDateFormat("yyyy.MM.dd - hh:mm:ss.SSS");
    //public final static String TOPIC_NAME = "CameraTopic";
}
