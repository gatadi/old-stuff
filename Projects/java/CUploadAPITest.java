import HTTPClient.* ;
import java.net.URL ;

import javax.xml.parsers.DocumentBuilder ;
import javax.xml.parsers.DocumentBuilderFactory ;
import org.w3c.dom.Document ;
import org.w3c.dom.NodeList ;
import org.w3c.dom.Node ;
import org.w3c.dom.Element ;
import javax.xml.transform.stream.StreamResult ;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory ;
import javax.xml.transform.Transformer ;

import java.io.File ;
import java.io.StringWriter;

public class CUploadAPITest
{

    private static final String HOST = "-host" ;
    private static final String AUTHCODE = "-authcode" ;
    private static final String ALBUM_CAPTION = "-albumcaption" ;
    private static final String FILE_PATH = "-filepath" ;


    public static void main(String[] args) throws Exception
    {
        hpInternetConfig() ;

        String stgURL = "http://www.snapfish.com/externalapi" ;
        String[] loginResponse = CUploadAPITest.login(stgURL) ;

        String[] startSessionResponse = CUploadAPITest.startSession(loginResponse[3], loginResponse[0]);
        upload(loginResponse[3], startSessionResponse[0], startSessionResponse[1], startSessionResponse[2]);

        System.exit(0) ;

        //CUploadAPITest.basicUpload(loginResponse[3], loginResponse[0]);

    }   //end of main(String[])



    public static String[] login(String url) throws Exception
    {
        DocumentBuilder docBuilder = DocumentBuilderFactory.
            newInstance().newDocumentBuilder() ;
        Document doc = docBuilder.newDocument(); //doc.cdocBuilder.parse(new  File("e:/workarea/JavaProjects/JavaTest/login.xml")) ;

        Element subscriberid = doc.createElement("subscriberid") ;
        subscriberid.setAttribute("xsi:type", "xsd:string") ;
        subscriberid.appendChild(doc.createTextNode("1000000"))  ;

        Element email = doc.createElement("email") ;
        subscriberid.setAttribute("xsi:type", "xsd:string") ;
        email.appendChild( doc.createTextNode("vijay@snapfish.com") );

        Element password = doc.createElement("password") ;
        subscriberid.setAttribute("xsi:type", "xsd:string") ;
        password.appendChild(doc.createTextNode("vijay"));

        Element login = doc.createElement("namesp1:Login") ;
        Element body = doc.createElement("SOAP-ENV:Body") ;
        Element envelope = doc.createElement("SOAP-ENV:Envelope") ;

        login.appendChild(subscriberid);
        login.appendChild(email);
        login.appendChild(password);

        body.appendChild(login);
        envelope.appendChild(body) ;

        //doc.createProcessingInstruction("version=1.0", "encoding=UTF-8") ;
        doc.appendChild(envelope) ;

        StringWriter sw = new StringWriter();
        DOMSource source = new DOMSource(doc) ;
        StreamResult result = new StreamResult(sw) ;
        TransformerFactory.newInstance().newTransformer().transform(source, result) ;

        System.out.println(sw.toString());

        HTTPConnection con = new HTTPConnection( new URL(url) );
        con.setAllowUserInteraction(false);
        HTTPResponse rsp = con.Post(url, sw.toString());
        String response = rsp.getText() ;
        System.out.println(response);

        Document docResp = docBuilder.parse(new java.io.StringBufferInputStream(response));

        String authcode = docResp.getElementsByTagName("authcode").item(0).getChildNodes().item(0).getNodeValue() ;
        String podhost = docResp.getElementsByTagName("podhost").item(0).getChildNodes().item(0).getNodeValue() ;
        String adhost = docResp.getElementsByTagName("adhost").item(0).getChildNodes().item(0).getNodeValue() ;
        String smarthost = docResp.getElementsByTagName("smarthost").item(0).getChildNodes().item(0).getNodeValue() ;

        System.out.println("authcode=" + authcode);
        System.out.println("podhost=" + podhost);
        System.out.println("adhost=" + adhost);
        System.out.println("smarthost=" + smarthost);

        String[] loginResponse = new String[]{
            authcode, podhost, adhost, smarthost
        };

        return loginResponse;
    }



    public static String[] startSession(String uploadHost, String authcode) throws Exception
    {
        String url =  "/startsession.suup"
            + "?authcode=" + authcode
            + "&ExpectedImages=2"
            + "&Src=HTM"
            + "&AlbumCaption=Test Album" ;
        HTTPConnection con = new HTTPConnection( new URL("http://" + uploadHost) );
        con.setAllowUserInteraction(false);
        HTTPResponse rsp = con.Get(url);
        String response = rsp.getText() ;
        System.out.println(response);

        DocumentBuilder docBuilder = DocumentBuilderFactory.
            newInstance().newDocumentBuilder() ;
        Document docResp = docBuilder.parse(new java.io.StringBufferInputStream(response));

        authcode = docResp.getElementsByTagName("authcode").item(0).getChildNodes().item(0).getNodeValue() ;
        String sessionID = docResp.getElementsByTagName("SessionId").item(0).getChildNodes().item(0).getNodeValue() ;
        String albumID = docResp.getElementsByTagName("AlbumId").item(0).getChildNodes().item(0).getNodeValue() ;

        System.out.println("\nauthcode=" + authcode);
        System.out.println("sessionID=" + sessionID);
        System.out.println("albumID=" + albumID);

        return new String[]{authcode, sessionID, albumID};
    }   //end of startSession()


    public static void upload(String uploadHost, String authcode, String sessionID, String albumID) throws Exception
    {
        String filePath1 = "C:/gatadi/photos/sangini/05012006.jpg";
        String filePath2 = "C:/gatadi/photos/sangini/05012006.jpg";

        NVPair[] hdrs = new NVPair[1];
        NVPair[] files = {new NVPair("filename1", filePath1),
            new NVPair("filename2", filePath2) } ;
        byte[] data = Codecs.mpFormDataEncode(null, files, hdrs ) ;

        String url = "/uploadimage.suup"
            + "?authcode=" + authcode
            + "&SessionId=" + sessionID
            + "&AlbumID=" + albumID
            + "&Src=SFC"
            + "&SequenceNumber=2" ;

        System.out.println("connecting to http://" + uploadHost + "" + url);

        HTTPConnection con = new HTTPConnection(new URL("http://" + uploadHost));
        HTTPResponse rsp = con.Post(url, data, hdrs);

        System.out.println(rsp.getText());

    }   //end of upload()



    public static void basicUpload(String uploadHost, String authcode) throws Exception
    {
        String filePath = "C:/gatadi/photos/sangini/05012006.jpg";
        String url = "/uploadimagebasic.suup?"
            + "authcode=" + authcode + "&"
            + "Src=HTM" + "&"
            + "AlbumCaption=Album1" + "&"
            + "SequenceNumber=1" + "&"
            + "HOST_NAME=http://snapfish2.qa.snapfish.com" ;

        NVPair[] hdrs = new NVPair[1];
        NVPair[] files = {new NVPair("filename1", filePath),
            new NVPair("filename2", filePath)} ;
        byte[] data = Codecs.mpFormDataEncode(null, files, hdrs ) ;

        System.out.println("connecting to " + url);

        HTTPConnection con = new HTTPConnection(new URL("http://" + uploadHost));
        HTTPResponse rsp = con.Post(url, data, hdrs);

        System.out.println(rsp.getText());
    }



    public static void hpInternetConfig()
    {
        System.getProperties().put( "http.proxySet", "true" );
        System.getProperties().put( "http.proxyHost", "web-proxy" );
        System.getProperties().put( "http.proxyPort", "8088" );

        System.getProperties().put( "https.proxySet", "true" );
        System.getProperties().put( "https.proxyHost", "web-proxy" );
        System.getProperties().put( "https.proxyPort", "8088" );

        System.getProperties().put( "socksProxyHost", "socks-server" );
        System.getProperties().put( "socksProxyPort", "1080" );
    }
}       //end of CUploadAPITest
