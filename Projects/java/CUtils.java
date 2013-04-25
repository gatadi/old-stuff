//toHashtable(String)
import java.util.Hashtable ;
import java.util.StringTokenizer ;
import java.util.Date ;

//import com.aol.shopping.msdk20.AQCMSDK ;
//import com.aol.shopping.msdk20.AQCMSDKException ;
//import com.sun.xml.tree.XmlDocument ;

import java.io.* ;

import java.net.URL ;
import java.net.URLEncoder ;
import java.net.URLConnection ;
import javax.net.ssl.HttpsURLConnection ;
import javax.net.ssl.SSLContext ;
import javax.net.ssl.X509TrustManager ;
import javax.net.ssl.TrustManager ;
//import com.sun.net.ssl.TrustManager;
//import com.sun.net.ssl.X509TrustManager;
import com.sun.net.ssl.internal.www.protocol.https.Handler;

import com.snapfish.database.SFDatabase;
import com.snapfish.dbobjects.SFLookup;

import com.snapfish.ddb.util.CDBNode;
import com.snapfish.ddb.util.CDBNodeHints;
import com.snapfish.ddb.util.CDBNodeHintsState;
import com.snapfish.ddb.util.CNoCleanupHook;
import com.snapfish.sfejb.CContextImpl ;


import com.snapfish.user.campaign.CCampaignMgr ;
import com.snapfish.user.campaign.ICampaignInfo ;

import com.snapfish.util.CDecrypter;


public class CUtils
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("CUtils.main()");
        
        //String pw = "DES+0[-N5)'<N;?6R@$5RD5UW:```\n`\nend" ;
        //System.out.println("Password=" + decrypt(pw));


    }   //end of main(String[])


    public static void printClassPath()
    {
        String COMMON_LIB = "/apps/DEV/snapcat/common/lib/" ;
        String COMMON_LIB_JARS = "" ;

        java.io.File file = new java.io.File(COMMON_LIB);
        String list[] = file.list() ;
        for(int i=0; i<list.length; i++)
        {
            COMMON_LIB_JARS = COMMON_LIB_JARS + COMMON_LIB + list[i] + ";" ;
            //System.out.println(COMMON_LIB + list[i]);
        }

        String LIB = "/apps/DEV/snapcat/webapps/ROOT/WEB-INF/lib/" ;
        String LIB_JARS = "" ;
        file = new java.io.File(LIB);
        list = file.list() ;
        for(int i=0; i<list.length; i++)
        {
            LIB_JARS = LIB_JARS + LIB + list[i] + ";" ;
        }


        String SERVER_LIB = "/apps/DEV/snapcat/server/lib/" ;
        String SERVER_LIB_JARS = "" ;
        file = new java.io.File(SERVER_LIB);
        list = file.list() ;
        for(int i=0; i<list.length; i++)
        {
            SERVER_LIB_JARS = SERVER_LIB_JARS + LIB + list[i] + ";" ;
        }

        System.out.println(COMMON_LIB_JARS +  SERVER_LIB_JARS + LIB_JARS);
    }


    public static void startup() throws Exception
    {
        System.setProperty("SF_CFG_DIR","E:/apps/DEV/snapcat/snapfish.ini");

        String ejbs = "com.snapfish.store.ejb.StoreManager,com.snapfish.emailservices.ejb.EmailSender,com.snapfish.image.notification.ejb.UserNotification,com.snapfish.image.ejb.ImageManager,com.snapfish.bizobj.order.ordermgr,com.snapfish.processor.york.agent,com.snapfish.album.ejb.AlbumManager,com.snapfish.share.ejb.ShareManager,com.snapfish.tellafriend.ejb.TellAFriendManager,com.snapfish.core.ejb.ping,com.snapfish.bizobj.brm.brmmgr,com.snapfish.bizobj.accounting.ejb.CustomerAccounting,com.snapfish.bizobj.iabrm.iabrmmgr,com.snapfish.core.partner.Partner,com.snapfish.bizobj.order.york.ordermgr,com.snapfish.bizobj.promotion.ejb.PromotionsManager,com.snapfish.bizobj.order.basketmgr,com.snapfish.demgqstn.ejb.CDemgQstnBean" ;
        StringTokenizer ejbTokens = new StringTokenizer(ejbs, ", ");
        String ejb = null ;
        while( ejbTokens.hasMoreTokens() ){
            CContextImpl.addHome(ejb = ejbTokens.nextToken());
            System.out.println("adding " + ejb + " to Context");
        }

        System.out.println("Opening Database connection...");
        SFDatabase.open();
        SFLookup oLookup = new SFLookup();
        System.out.println("Starting SFLookup...");
        oLookup.startup(null, null);

        CDBNodeHints oCDBNodeHints = CDBNodeHints.getDBNodeHints();
        CDBNode oCurrentDBNode = oCDBNodeHints.getDBNode();
        CDBNodeHintsState oCurrentHintsState = oCDBNodeHints.saveState();
        oCDBNodeHints.setOrReplaceDBNode(CDBNode.getAllDBNodes()[0]);
        oCDBNodeHints.setCleanupHook(new CNoCleanupHook(CDBNode.getAllDBNodes()[0]));

    }



    //builds hashtable from a string in the format of
    //{key1=value1, key2=value2}
    public static Hashtable toHashtable(String str) throws Exception
    {
        Hashtable h = new Hashtable() ;
        if(str == null){
            return h ;
        }
        str = str.trim() ;
        if( !str.startsWith("{") || !str.endsWith("}") ){
            throw new Exception("Illigal format, missing '{' or '}'") ;
        }   //end of if

        str = str.substring(1, str.length()-1) ;

        StringTokenizer tokens = new StringTokenizer(str, ",") ;
        while(tokens.hasMoreTokens())
        {
            String token = tokens.nextToken() ;
            StringTokenizer _tokens = new StringTokenizer(token, "=") ;
            if( _tokens.countTokens() != 2 ){
                throw new Exception("Illigal format, missing '=' ") ;
            }   //end of if
            h.put(_tokens.nextToken().trim(), _tokens.nextToken().trim()) ;
        }   //end of while

        return h ;
    }   //end of toHashtable(String)


        //*************** uploafile ********************************
    /*
        String file = "e:/workarea/javatest/hiresfile.jpg" ;
        NVPair[] hdrs = new NVPair[1];
        NVPair[] files = {new NVPair("file1", file), new NVPair("file2", file)} ;
        byte[] data = Codecs.mpFormDataEncode(null, files, hdrs ) ;

        String url = "/exuploadimages?authcode=1110822431141413:3f249806d0a20:vijay@snapfish.com&albumname=Soap+Album&numimages=1&sourcecode=SFM&file=";
        System.out.println("connecting to url");
        HTTPConnection con = new HTTPConnection("alpha.corp.snapfish.com");
        HTTPResponse rsp = con.Post(url, data, hdrs);
        System.out.println(rsp);
    */



    public static String encode(String s)
    {
        final char SLASH = '/';
        final char SEMICOLON = ';';

        // Replace all "/" with ";"
        s = s.replace(SLASH, SEMICOLON);

        // Prepend timestamp + "|" before encoded string
        Date date = new Date();


        long dateValue = date.getTime();

        dateValue /= 10;
        dateValue *= 10;

        s = String.valueOf(dateValue) + "|" + s;

        // Encode the string
        StringBuffer sb = new StringBuffer(s);
        for(int i=0; i<sb.length(); i++)
        {
            char c = sb.charAt(i);
            int value = (int)c;
            value++;
            value++;
            if((i % 2 > 0))
            value++;
            sb.setCharAt(i, (char)value);
        }

        String out = sb.toString();
        out = java.net.URLEncoder.encode(out);



        return out;
    }


    public static String decode(String s)
    {
         try{
            s = java.net.URLDecoder.decode(s) ;
         }catch(Exception e){
            e.printStackTrace();
         }

         StringBuffer sb = new StringBuffer(s) ;

         for(int i=0; i<s.length(); i++)
         {
            char c = s.charAt(i) ;
            int value = (int) c ;
            if( i%2 > 0 ){
                value-- ;
            }
            value--; value-- ;
            c = (char) value ;
            sb.setCharAt(i,c);
         }

         s = sb.toString() ;
         System.out.println(s);
         int index = s.indexOf("|") ;
         if( index != -1){
            s = s.substring(1+index) ;
         }
         s = s.replace(';', '/');

         return s ;
    }


    public static void doPOST(String urlString, String content) throws Exception
    {
        URL url = new URL(urlString) ;
        URLConnection httpsURLConnection = url.openConnection();

        //httpsURLConnection.setFollowRedirects(false);
        System.out.println("\nRequest=" + url.getProtocol() + "://"
            + url.getHost() + url.getFile() );


        httpsURLConnection.setDoOutput(true);

        PrintWriter out = new PrintWriter( httpsURLConnection.getOutputStream() ) ;
        out.print(content) ;
        out.flush();
        out.close() ;


        BufferedReader reader = new BufferedReader(
            new InputStreamReader( httpsURLConnection.getInputStream() ) );
        String line = null ;
        String data = "" ;

        while( (line=reader.readLine()) != null ){
            data +=  line + "\n";
        }   //end  of while

        reader.close() ;

        System.out.println(data) ;

    }   //end of doPOST()

    public static void disableCertificateValiadation()
    {
        System.setProperty ("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        System.setProperty ("javax.net.ssl.trustStore", "E:/apps/jdk/jre/lib/security/cacerts");
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        // Install the all-trusting trust manager
        try {

            X509TrustManager tm = new MyX509TrustManager();
            TrustManager []tma = {tm};
            //sslContext.init(null,tma,new java.security.SecureRandom());// slowwwww

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, tma, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {}

    }   //end of disableCertificateValiadation()

    public static String decrypt(String encryptedString) throws Exception
    {
        CDecrypter decrypt = new CDecrypter();
        return decrypt.decrypt(encryptedString);
    }


    public static void hpInternetConfig()
    {
        System.getProperties().put( "http.proxySet", "true" );
        System.getProperties().put( "http.proxyHost", "web-proxy" );
        System.getProperties().put( "http.proxyPort", "8088" );

        System.getProperties().put( "socksProxyHost", "socks-server" );
        System.getProperties().put( "socksProxyPort", "1080" );
    }

}   //end of class CUtils



class MyX509TrustManager implements X509TrustManager
{
    public boolean isClientTrusted(java.security.cert.X509Certificate a[])
    {
        return true ;
    }

    public boolean isServerTrusted(java.security.cert.X509Certificate a[])
    {
        return true ;
    }

    public java.security.cert.X509Certificate[] getAcceptedIssuers()
    {
        return null ;
    }


    public void checkServerTrusted(java.security.cert.X509Certificate[] a, java.lang.String s)
    {}

    public void checkClientTrusted(java.security.cert.X509Certificate[] a, java.lang.String s)
    {}

}   //end of  MyX509TrustManager
