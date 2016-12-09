package uk.ac.ncl.csc8110.huan.nosqlconsumer;

import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws JMSException, NamingException {
        TopicReceiver receiver = new TopicReceiver();
        receiver.runReceiverAsynchronous();
    }
}
