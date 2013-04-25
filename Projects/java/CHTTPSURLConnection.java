
import java.util.Vector ;
import java.net.URL ;
import java.net.Socket ;
import java.io.InputStream ;
import java.io.InputStreamReader ;
import java.io.BufferedReader ;
import java.io.BufferedInputStream;
import java.io.File ;
import java.io.FileOutputStream ;
import java.io.DataOutputStream ;
import java.io.PrintWriter ;
import java.net.URLEncoder ;
//import javax.net.ssl.HttpsURLConnection ;
//import javax.net.ssl.SSLContext ;
//import javax.net.ssl.X509TrustManager ;

import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.X509TrustManager;

import com.sun.net.ssl.HttpsURLConnection;
import com.sun.net.ssl.internal.www.protocol.https.Handler;

//import javax.net.ssl.*;


public class CHTTPSURLConnection
{
    private URL url = null ;
    private HttpsURLConnection urlConnection = null ;
    private Header header = null ;
    private String response = null ;


    public CHTTPSURLConnection(String url) throws Exception
    {
        this(url, null) ;
    }

    public CHTTPSURLConnection(String url, Header header) throws Exception
    {
        this.url = new URL(url) ;
        Object o = this.url.openConnection();

        this.urlConnection = (HttpsURLConnection)o ;
        this.urlConnection.setFollowRedirects(false);
        this.header = header ;

    }   //end of  CHTTPSURLConnection()


    public static void disableCertificateValiadation()
    {
        System.setProperty ("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        System.setProperty ("javax.net.ssl.trustStore", "E:/apps/jdk/jre/lib/security/cacerts");
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());


        // Create a trust manager that does not validate certificate chains
        //X509TrustManager[] trustAllCerts = new MyX509TrustManager[]{} ;

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




    public String doGET() throws Exception
    {
        print("\nRequest=https://" + this.url.getHost() + this.url.getFile());

        urlConnection.setRequestMethod("GET");
        setHeader(this.header) ;

        String redirectURL = urlConnection.getHeaderField("Location") ;
        if( redirectURL != null ){
            return redirect(redirectURL) ;
        }   //end of if

        return this.response = read() ;

    }   //end of doGET()


    public String doPOST(String content) throws Exception
    {
        print("\nRequest=http://" + this.url.getHost() + this.url.getFile());

        urlConnection.setDoOutput(true);
        setHeader(this.header) ;

        PrintWriter out = new PrintWriter( urlConnection.getOutputStream() ) ;
        out.print(content) ;
        out.flush();
        out.close() ;

        String redirectURL = urlConnection.getHeaderField("Location") ;
        if( redirectURL != null ){
            return redirect(redirectURL) ;
        }   //end of if

        return this.response = read() ;

    }   //end of doPOST()


    private String redirect(String urlAsString) throws Exception
    {
        urlAsString = urlAsString.trim() ;
        if( ! urlAsString.startsWith("http") )
        {
            String port = "" ;
            if(this.url.getPort() != -1 )
                port = ":" + this.url.getPort() ;
            urlAsString = "https://" + this.url.getHost() + port
                + urlAsString ;
        }   //end of if

        print("Redirecting to=" + urlAsString);

        CHTTPSURLConnection http
            = new CHTTPSURLConnection(urlAsString, this.header) ;

        return this.response = http.doGET() ;

    }   //end of recirect()


    public String read() throws Exception
    {
        this.header = readHeader();

        BufferedReader reader = new BufferedReader(
            new InputStreamReader( urlConnection.getInputStream() ) );
        String line = null ;
        String data = "" ;

        while( (line=reader.readLine()) != null ){
            data +=  line + "\n";
        }   //end  of while

        reader.close() ;

        return data ;
    }   //end of read()


    public Header readHeader()
    {
        // create input stream
        Vector vCookies = new Vector() ;

        String cookie = urlConnection.getHeaderField("Set-Cookie") ;
        if(cookie!=null && cookie.trim().length() != 0){
            vCookies.add(cookie);
        }

        String eTag = urlConnection.getHeaderField("ETag") ;

        if( this.header == null ){
            return new Header(vCookies, eTag) ;
        }   //end of if

        this.header.vCookies.addAll(vCookies);
        if( eTag != null && eTag.trim().length() != 0){
            this.header.eTag = eTag ;
        }

        return this.header ;
    }   //end of readCookies()

    public Header getHeader()
    {
        return this.header ;
    }

    public void setHeader(Header header)
    {
        if( header == null)
            return;

        for(int i=0; i<header.vCookies.size(); i++)
        {
            Object o = header.vCookies.elementAt(i) ;
            urlConnection.setRequestProperty("Cookie",
                header.vCookies.elementAt(i).toString());
        }

        if( header.eTag != null ){
            urlConnection.setRequestProperty("ETag", header.eTag);
        }

    }   //end of setCookies()


    private void print(String str)
    {
        //System.out.println(str);
    }



    //url -> http://snapfish/dm1//hr/testdata/Upload_full1//3243675Xpic1hr.jpg
    public static byte[] getFile(String url) throws Exception
    {
        URL _url = new URL(url) ;
        HttpsURLConnection urlConnection = (HttpsURLConnection)_url.openConnection() ;
        urlConnection.setFollowRedirects(false);
        urlConnection.setRequestMethod("GET");

        String redirectURL = urlConnection.getHeaderField("Location") ;
        BufferedInputStream buffInpuStream = new BufferedInputStream(
            new BufferedInputStream( urlConnection.getInputStream() ) );


        System.out.print("Downloadig file..." + urlConnection.getContentLength()
            + " bytes");
        byte[] data = new byte[urlConnection.getContentLength()];
        byte[] buffer = new byte[512];
        int numberOfBytesToRead = 512;
        int index = 0 ;
        int n = -1 ;
        while( (n = buffInpuStream.read(buffer, 0, numberOfBytesToRead)) != -1  ){
            System.arraycopy(buffer, 0, data, index ,n);
            index += n ;
        }
        if( index != data.length ){
            System.out.println("\nCould not download file \ncontent-length=" + urlConnection.getContentLength()
                + "\ndownloaded byte-length=" + index ) ;
        }
        buffInpuStream.close() ;
        System.out.println("\nFile download is complete.");
        return data ;
    }   //end of getFile(String url)

    public static void saveFile(byte[] data, String dirname, String filename)
        throws Exception
    {
        java.io.File dir = new java.io.File("./HIRES/" + dirname) ;
        dir.mkdirs() ;
        java.io.File file = new java.io.File("./HIRES/" + dirname + "/" + filename) ;
        java.io.FileOutputStream fout = new java.io.FileOutputStream(file);
        fout.write(data) ;
        fout.close() ;
    }


    class Header
    {
        Vector vCookies = new Vector();
        String eTag = null ;

        Header(Vector vCookies, String eTag)
        {
            this.vCookies = vCookies ;
            this.eTag = eTag ;
        }

        public String toString()
        {
            String cookies = "" ;
            for(int i=0; i<vCookies.size(); i++)
            {
                cookies += vCookies.elementAt(i).toString();
            }

            /*
            for(int i=0; i<10; i++)
            {
                String key = urlConnection.getHeaderFieldKey(i);
                String value = urlConnection.getHeaderField(key);
                System.out.println(key + "=" + value);
            }
            */

            return cookies + "\n" + eTag;
        }
    }   //end of class Headers



    public String getResponse()
    {
        return this.response ;
    }


    public static void main(String[] args)  throws Exception
    {
        disableCertificateValiadation();

    }   //end main()


}   //end of CHTTPSURLConnection


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

