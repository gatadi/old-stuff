import java.net.URL ;
import java.net.URLConnection ;

import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.X509TrustManager;
import com.sun.net.ssl.internal.www.protocol.https.Handler;
import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnection;

import java.io.PrintWriter ;
import java.io.BufferedReader ;
import java.io.InputStreamReader ;

public class CSecurePost
{
    static String FIRST_NAME = "firstname" ;

    public static void main(String[] args) throws Exception
    {
        System.out.println("\nCTest.main()");

        disableCertificateValiadation() ;

        String url = "https://snapfish/default/testhttps.jsp" ;
        String content = "<xml><body>all elements goes here</body></xml>" ;

        doPOST(url, content) ;

     }   //end of main()

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

    


}  //End of CSecurePost class



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
        /*
        public void checkServerTrusted(java.security.cert.X509Certificate[] a,
            java.lang.String s)
        {
        }
    
        public void checkClientTrusted(java.security.cert.X509Certificate[] a,
            java.lang.String s)
        {
        } */
    
    }
