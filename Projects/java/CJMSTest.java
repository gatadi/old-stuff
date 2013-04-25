import fiorano.jms.rtl.FioranoInitialContext;
import com.snapfish.messageservices.* ;
import com.snapfish.messaging.*;
import javax.jms.*;
import com.snapfish.processor.york.* ;
import java.net.InetAddress ;



public class CJMSTest
{
    private int jmsPort = 2001 ;
    private String QUEUE_LOOKUP = "primaryqcf" ;

    private String server = null ;
    private String queueName = null ;
    private int messageCount = 0 ;
    private String sendReceive = null ;
    
    /*
    private String jmsServer = "krakow" ;
    private String jmsServerOtherside = "trojans" ;
    public static String SEND_QUEUE_NAME = "YORK_processor_sendq" ;
    public static String RECEIVE_QUEUE_NAME = "YORK_processor_receiveq";
    public static String SEND_QUEUE_NAME_OTHERSIDE = "SNAPFISH_processor_sendq" ;
    public static String RECEIVE_QUEUE_NAME_OTHERSIDE = "SNAPFISH_processor_receiveq";
    */

    public CJMSTest(String server, String queueName,
        int messageCount, String sendReceive)
    {
        this.server = server ;
        this.queueName = queueName ;
        this.messageCount = messageCount ;
        this.sendReceive = sendReceive ;
    }
    

    public void send(int startIndex) throws Exception
    {   
        try
        {
        
            System.out.println("\nCJMSTest.send()...");
            FioranoInitialContext ic = new FioranoInitialContext();
            //sender queue configureation
            System.out.println("Bind Initial Context : "
                + this.server + ":" + this.jmsPort);
            ic.bind(InetAddress.getByName(this.server), this.jmsPort);
            QueueConnectionFactory tcf
                = (QueueConnectionFactory) ic.lookup (QUEUE_LOOKUP);
            Queue sendQueue = (Queue)ic.lookup(queueName);
            ic.dispose() ;
            QueueConnection queueSendConnection = tcf.createQueueConnection();
            QueueSession queueSendSession = queueSendConnection.createQueueSession(
                false , Session.AUTO_ACKNOWLEDGE);
    
            QueueSender queueSender = queueSendSession.createSender(sendQueue);
            
            for(int i=startIndex; i<=this.messageCount ; i++)
            {
                try
                {
                    ObjectMessage msg = queueSendSession.createObjectMessage();
                    CReleaseOrHoldMessage o = new CReleaseOrHoldMessage();
                    o.setID(i) ;
                    msg.setObject(o);
                    msg.setStringProperty("messagetype", o.getType());
            
                    queueSender.send(msg, DeliveryMode.PERSISTENT, 5, 0);
                    System.out.println("Sending message ... " + i + "  done");                                                  
                }catch(JMSException e)
                {
                    e.printStackTrace() ;
                    Thread.sleep(5) ;
                    send(i) ;
                }
                    
            }   //end of for
            
            if( queueSendConnection != null ){
                queueSendConnection.close();
            }   //end of if
            
            if( queueSendSession != null ){
                queueSendSession.close() ;
            }   //end of if
            
        }catch(JMSException e)
        {
            e.printStackTrace() ;
            Thread.sleep(5000) ;
            send(startIndex) ;
        }
        

    }   //end of send()
    
    
    public void receive() throws Exception
    {   
        try
        {       
            FioranoInitialContext ic = new FioranoInitialContext();
            //sender queue configureation
            System.out.println("Bind Initial Context : "
                + this.server + ":" + this.jmsPort);
            ic.bind(InetAddress.getByName(this.server), this.jmsPort);
            QueueConnectionFactory tcf
                = (QueueConnectionFactory) ic.lookup (QUEUE_LOOKUP);
            Queue receiveQueue = (Queue)ic.lookup(queueName);
            ic.dispose() ;
            
            QueueConnection queueReceiveConnection = tcf.createQueueConnection();
            queueReceiveConnection.start();
            QueueSession queueReceiveSession = queueReceiveConnection.createQueueSession(
                false , Session.AUTO_ACKNOWLEDGE);
            QueueReceiver queueReceiver = queueReceiveSession.createReceiver(receiveQueue);
    
            //for(int i=1; i<=this.messageCount ; i++)
            while(true)
            {
                try
                {
                    Message msg = queueReceiver.receive(5000) ;
                
                    if(msg != null)
                    {
                        ObjectMessage om = (ObjectMessage)msg;
                        if(om != null){
                            IMessageObject mo = (IMessageObject)om.getObject();
                            System.out.println("Received message : " + mo);
                        }   //end of if (om!=null)
                        
                    }
                    {
                        continue ;
                    }
                }catch(Exception e){
                    e.printStackTrace() ;
                    Thread.sleep(5000) ;
                    receive() ;
                }
                
            }   //end of while
            
        }   //end of try/catch
        catch(JMSException e)
        {
            e.printStackTrace() ;
            Thread.sleep(5000) ;
            receive() ;
        }
        
        /*
        if( queueReceiveConnection != null ){
            queueReceiveConnection.close() ;
        }   //end of if
        
        if( queueReceiveSession != null ){
            queueReceiveSession.close() ;
        }   //end of if
        */
        
    }   //end of send()
    
    
    public void peek()  throws Exception
    {
        FioranoInitialContext ic = new FioranoInitialContext();
        //sender queue configureation
        System.out.println("Bind Initial Context : "
            + this.server + ":" + this.jmsPort);
        ic.bind(InetAddress.getByName(this.server), this.jmsPort);
        QueueConnectionFactory tcf
            = (QueueConnectionFactory) ic.lookup (QUEUE_LOOKUP);
        Queue receiveQueue = (Queue)ic.lookup(queueName);
        ic.dispose() ;
        
        QueueConnection queueReceiveConnection = tcf.createQueueConnection();
        queueReceiveConnection.start();
        QueueSession queueReceiveSession = queueReceiveConnection.createQueueSession(
            true , Session.AUTO_ACKNOWLEDGE);
        QueueReceiver queueReceiver = queueReceiveSession.createReceiver(receiveQueue);

        for(int i=0; i<6 ; i++)
        {   
            QueueBrowser m_browser = queueReceiveSession.createBrowser(receiveQueue);
            java.util.Enumeration messages = m_browser.getEnumeration();
            if ( messages.hasMoreElements() )
            {
                ObjectMessage om = (ObjectMessage)messages.nextElement();
                if(om != null)
                {
                    IMessageObject mo = (IMessageObject)om.getObject();
                    System.out.println("Received message : " + mo);
                }   //end of if (om!=null)
            }
            m_browser.close();
            
        }

    }
    
    public static void main(String[] args) throws Exception
    {
        System.out.println("\nCJMSTest.main()...");
        if( args.length !=4 )
        {
            System.out.println("Usage :");
            System.out.println("compileExecute CJMSTest HostName QueueName numberOfMessages send(or)receive");
            System.exit(1);
        }   //end of if
        
        CJMSTest jmsTest = new CJMSTest(args[0], args[1],
            Integer.parseInt(args[2]), args[3] ) ;
            
        if( args[3].toLowerCase().equalsIgnoreCase("send") ){
            jmsTest.send(1);
        }else if( args[3].toLowerCase().equalsIgnoreCase("receive") ){
            jmsTest.receive();
        }else if(args[3].toLowerCase().equalsIgnoreCase("peek") ){
            jmsTest.peek() ;
        }else{
            System.out.println("You entered " + args[3]) ;
            System.out.println("Enter send (or) receive");
        }

    }   //end of main()
    
}   //end of class CJMSTest


class CJMSTestMessageListener implements MessageListener
{
    public void onMessage(Message m)
    {
        try
        {
            System.out.println("CJMSTestMessageListener.onMessage()") ;
            System.out.println("Receiving message...");
            ObjectMessage om = (ObjectMessage)m;
            IMessageObject mo = (IMessageObject)om.getObject();
            System.out.println("Message : " + mo) ;
            System.out.println("Receiving message...done");
        }catch(JMSException e)
        {
            e.printStackTrace();
        }   //end of try/catch block

    }   //end of onMessage()

}   //end fo class CJMSTestMessageListener

