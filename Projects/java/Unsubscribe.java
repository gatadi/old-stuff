import javax.jms.*;
import java.io.*;
import fiorano.jms.rtl.*;
import java.net.*;

public class Unsubscribe
{
    public static void main (String args[])
    {

        // Set up client-side digital certificate vector. This vector is passed
        // to the function FioranoInitialContext(), which uses it to establish
        // an authenticated connection with the server
        // Initialize all SSL parameters
        //
        if( args.length != 3 ){
            System.out.println("Enter host name & Durable subscriber name");
            System.exit(1) ;
        }   //end of if

        try
        {
            // 1. Create the initial context string to lookup the topic connection
            //    factory. Be sure to pass the SECURITY INFORMATION VECTOR, "params"
            //    to the FioranoInitialContext class.  Without this parameter, a
            //    connection will be refused.
            //
            InetAddress serverHost = InetAddress.getByName (args[0]);
            FioranoInitialContext ic = null;

            ic = new FioranoInitialContext ();
            ic.bind (serverHost, 2001);

            // 1.1  Lookup Connection Factory and Topic names
            //
            TopicConnectionFactory tcf =
                        (TopicConnectionFactory) ic.lookup ("primarytcf");
            Topic topic = (Topic)ic.lookup("primarytopic");
            

            // 1.2  Dispose the InitialContext resources
            //
            ic.dispose();

            // 2. create and start a topic connection
            System.out.println("Creating topic connection with " + tcf);
            TopicConnection tc = tcf.createTopicConnection();
                    tc.setClientID (args[2]);

            // 3. create topic session on the connection just
            //    created
            System.out.println("Creating topic session: not trans, auto ack");
            TopicSession ts = tc.createTopicSession(false,1);

            // 4. create a Durable Subscriber on the topic session
            System.out.println("Unsubscribing the durable subscription");
            // 4.1  create the durable subscriber with a given name
            ts.unsubscribe(args[1]);

        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }
    } // main
} // Subscriber

