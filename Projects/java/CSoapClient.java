import javax.xml.soap.*;
import javax.xml.messaging.*;
import java.io.*;
import java.util.*;

public class CSoapClient
{
    public void sendRequest() throws Exception
    {
        SOAPConnectionFactory scFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection con = scFactory.createConnection();

        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();

        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        SOAPHeader header = envelope.getHeader();
        SOAPBody body = envelope.getBody();
        
        header.detachNode();

        Name bodyName = envelope.createName("getQuote",
                            "n", "urn:xmethods-delayed-quotes");
        SOAPBodyElement gltp = body.addBodyElement(bodyName);

        Name name = envelope.createName("symbol");
        SOAPElement symbol = gltp.addChildElement(name);
        symbol.addTextNode("SUNW");

        URLEndpoint endpoint = new URLEndpoint("http://snapfish/externalapi");
        SOAPMessage response = con.call(message, endpoint);
        con.close();

        SOAPPart sp = response.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();
        SOAPBody sb = se.getBody();
        Iterator it = sb.getChildElements();
        while(it.hasNext())
        {
            SOAPBodyElement bodyElement = (SOAPBodyElement)it.next();
            Iterator it2 = bodyElement.getChildElements();
            while(it2.hasNext())
            {
                SOAPElement element2 = (SOAPElement)it2.next();
                Object lastPrice = element2.getValue();
                System.out.print("The last price is ");
                System.out.println(lastPrice);
            }
        }   //end of while()
        
    }   //end of sendRequest()
    
    
    public static void main(String[] args) throws Exception
    {
        System.out.println("\nCSoapClient.main()...\n");
        new CSoapClient().sendRequest();
    }
}   //end of clas CSoapClient
