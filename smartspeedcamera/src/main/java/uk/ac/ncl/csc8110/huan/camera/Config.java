package uk.ac.ncl.csc8110.huan.camera;


import org.kohsuke.args4j.Option;

/**
 * Configuration File For SmartSpeedCamera
 * Created by huan on 2016/12/3.
 */
public class Config {
    @Option(name = "-i",required = true,usage = "unique id")
    public static String ID;
    @Option(name = "-s",required = true,usage = "Street of the camera")
    public static String STREET;
    @Option(name = "-c",required = true, usage = "city of the camera")
    public static String CITY;
    @Option(name = "-m",required = true,usage = "max speed limit")
    public static int MAX_SPEED;
    @Option(name = "-r",required = true,usage = "vehicle rate per min")
    public static int RATE;
    @Option(name = "-gm",required = false,usage = "gaussian mean for random generator")
    public static int GAUSSIAN_MEAN = 80;
    @Option(name = "-gd",required = false,usage = "gaussian deviation for random generator")
    public  static int GAUSSIAN_DEVIATION = 20;

    public static int RESEND_TIME = 500;

    final static String NAME_SPACE = "huantopic";
    final static String KEY_NAME = "RootManageSharedAccessKey";
    final static String KEY_VALUE = "Owd+jQu2a3ppWPbo6nM2NCO+qFyeuDE1GYWs8GyL3wg=";
    final static String SERVICE_URL=".servicebus.windows.net";

    public final static String TOPIC_NAME = "CameraTopic";
    static String getConfig(){
        return ID+","+STREET+","+CITY+","+MAX_SPEED+","+RATE;
    }
}
