package uk.ac.ncl.csc8110.huan.camera;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App {
    private final static Logger logger = LoggerFactory.getLogger(App.class.getName());
    public static void main( String[] args ) {
        // parse arguments to Config
        logger.info("CMD Argument Parsing...");
        CmdLineParser parser = new CmdLineParser(new Config());
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            logger.error("Cmd line Parser failed {}",e.getMessage());
            parser.printUsage(System.err);
            System.exit(-1);
        }
        logger.info(Config.getConfig());
        logger.info("CameraSimulator Start...");
        //start camera
        CameraSimulator cameraSimulator = new CameraSimulator(false);
        cameraSimulator.startCamera();
    }

}
