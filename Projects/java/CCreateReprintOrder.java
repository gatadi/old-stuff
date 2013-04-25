
import java.net.URL ;
import java.net.URLConnection ;

//import com.sun.net.ssl.SSLContext;
//import com.sun.net.ssl.TrustManager;
//import com.sun.net.ssl.X509TrustManager;
//import com.sun.net.ssl.internal.www.protocol.https.Handler;
//import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnection;

import java.io.* ;


import java.util.Hashtable ;
import java.util.Random ;
import java.util.Vector ;
import java.net.URLDecoder ;
import java.util.StringTokenizer ;
import java.util.Enumeration ;


import com.snapfish.util.CUtils ;
import com.sun.xml.tree.XmlDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Node ;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList ;
import org.w3c.dom.NamedNodeMap ;

import com.snapfish.dbobjects.ProductTypeCodeRef ;
import com.snapfish.bizobj.order.ordermgr.CShoppingOrderManagerBean ;
import com.snapfish.bizobj.order.COrderLineValue ;

import com.snapfish.database.SFDatabase;
import com.snapfish.dbobjects.SFLookup;

import com.snapfish.ddb.util.CDBNode;
import com.snapfish.ddb.util.CDBNodeHints;
import com.snapfish.ddb.util.CDBNodeHintsState;
import com.snapfish.ddb.util.CNoCleanupHook;
import com.snapfish.sfejb.CContextImpl ;


import com.snapfish.extapi.CSoapXMLDocument ;
import com.snapfish.extapi.CExternalAPIManager ;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder ;

public class CCreateReprintOrder
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("\nCCreateReprintOrder.main()");

        java.io.FileInputStream fin = new  java.io.FileInputStream("e:/workarea/javaprojects/javatest/sharealbum.xml") ;
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fin);

        String Message = CSoapXMLDocument.getElementValue(
                    CSoapXMLDocument.getElement(doc, "Message"));
        System.out.println("Message=" + Message);
            //createReprintOrder();


     }   //end of main()

    /*
    public static void createReprintOrder() throws Exception
    {
        String xml = "<?xml version=\"1.0\" ?><xml><authcode>a&amp;lt;&amp;b</authcode><ReprintOrder><Pict id=\"44407274\" upc=\"reprint;4x6\" qty=\"1\" /><Pict id=\"44407275\" upc=\"reprint;5x7\" qty=\"1\" /></ReprintOrder></xml>" ;



        Vector addOLIs = new Vector() ;
        try
        {
            XmlDocument doc = XmlDocument.createXmlDocument(
                new StringBufferInputStream(xml), false);

            Element reprintOrderElement = CSoapXMLDocument.getElement(
                    doc, "ReprintOrder");
            NodeList nL = reprintOrderElement.getElementsByTagName( "Pict" ) ;
            if(nL.getLength()==0){
                throw new Exception("No Pict Elements...");
            }   //end of if

            for(int i=0; i<nL.getLength(); i++)
            {
                Element pictElement = (Element)nL.item(i);

                addOLIs.add(new COrderLineValue(
                    pictElement.getAttribute("id"),
                    null,
                    pictElement.getAttribute("upc"),
                    Integer.parseInt(pictElement.getAttribute("qty"))));

            }   //end of for


            String authcode = CSoapXMLDocument.getElementValue(
                    CSoapXMLDocument.getElement(doc, "authcode"));
            System.out.println("authcode=" + authcode);
            //validate authcode
            //String userInfo[] = CExternalAPIManager.decodeAuthCode(decodeAuthCode);
        }catch(Exception e)
        {
            e.printStackTrace();
            //redirect to error page with the message
            //"invalid xml format" + exception stack trace
        }

        //  start of Pict Validation
        //check for picture existance
        //if any of the pictures is invalid, show error page with invalid data
        //message "can not find this picture id"


        //end of Pict Validation


        startup();

        // start of Order Creation
        //new CShoppingOrderManagerBean().removeOpenShoppingCart(new Long("3085785"));
        //set dbnode hints CDBNode.getByAccountOid(userInfo[0]);
    
        new CShoppingOrderManagerBean().updateOrderInOpenShoppingCart(
                    new Long("3085785"),
                    ProductTypeCodeRef.REP,
                    new Hashtable(),
                    addOLIs, null, null ) ;
    



        //oCDBNodeHints.restoreState(oCurrentHintsState);

    }
    */


    public static void startup() throws Exception
    {
        System.setProperty("SF_CFG_DIR","E:/apps/DEV/snapcat/snapfish.ini");

        String ejbs = "com.snapfish.store.ejb.StoreManager,com.snapfish.emailservices.ejb.EmailSender,com.snapfish.image.notification.ejb.UserNotification,com.snapfish.image.ejb.ImageManager,com.snapfish.bizobj.order.ordermgr,com.snapfish.processor.york.agent,com.snapfish.album.ejb.AlbumManager,com.snapfish.image.pcom.PcomBean,com.snapfish.share.ejb.ShareManager,com.snapfish.tellafriend.ejb.TellAFriendManager,com.snapfish.core.ejb.ping,com.snapfish.bizobj.brm.brmmgr,com.snapfish.bizobj.accounting.ejb.CustomerAccounting,com.snapfish.bizobj.iabrm.iabrmmgr,com.snapfish.core.partner.Partner,com.snapfish.bizobj.order.york.ordermgr,com.snapfish.bizobj.promotion.ejb.PromotionsManager,com.snapfish.bizobj.order.basketmgr,com.snapfish.demgqstn.ejb.CDemgQstnBean" ;
        StringTokenizer ejbTokens = new StringTokenizer(ejbs, ", ");
        while( ejbTokens.hasMoreTokens() ){
            CContextImpl.addHome(ejbTokens.nextToken());
        }

        SFDatabase.open();
        SFLookup oLookup = new SFLookup();
        oLookup.startup(null, null);

        CDBNodeHints oCDBNodeHints = CDBNodeHints.getDBNodeHints();
        CDBNode oCurrentDBNode = oCDBNodeHints.getDBNode();
        CDBNodeHintsState oCurrentHintsState = oCDBNodeHints.saveState();
        oCDBNodeHints.setOrReplaceDBNode(CDBNode.getAllDBNodes()[0]);
        oCDBNodeHints.setCleanupHook(new CNoCleanupHook(CDBNode.getAllDBNodes()[0]));

    }



}
