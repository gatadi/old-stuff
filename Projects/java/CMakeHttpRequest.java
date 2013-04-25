import java.text.DecimalFormat ;
import java.io.* ;
import java.util.* ;
import java.net.URLEncoder ;
import java.net.URLDecoder ;
import java.net.URL ;
import java.net.URLConnection ;
import java.net.HttpURLConnection ;
import javax.servlet.http.* ;
import java.net.Socket ;
import javax.net.ssl.SSLSocket ;
import javax.net.ssl.SSLSocketFactory ;
import CHTTPSocketConnection ;
import javax.servlet.http.Cookie ;


public class CMakeHttpRequest
{
    public static void main(String[] args) throws Exception
    {
        java.security.Security.addProvider( new com.sun.net.ssl.internal.ssl.Provider());
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");

        System.out.println("CMakeHttpRequest.main()") ;
        //testSocket() ;
        //test();
        
        CHTTPSocketConnection conn = new CHTTPSocketConnection("snapfish", 80) ;
        conn.makeGETRequest("/default/jsp/store/slimlineaol/splash.jsp");


    }   //end of main()


    public static void test() throws Exception
    {
        String host = "http://snapfish" ;
        CHTTPURLConnection http = new CHTTPURLConnection(host +
            "/login", null);
        String data = http.doGET();
        Vector vCookies = http.getCookies() ;

        String content = "emailaddress=" + URLEncoder.encode("vijay@snapfish.com")
                      + "&password=" + URLEncoder.encode("vijay")  ;
        http = new CHTTPURLConnection(host + "/loginsubmit");
        data = http.doPOST(content);

        http = new CHTTPURLConnection(host + "/editprofile");
        http.setCookies( vCookies ) ;
        http.doGET() ;

    }   //end of test()

    public static void testSocket()  throws Exception
    {
        CHTTPSocketConnection conn = new CHTTPSocketConnection("snapfish", 80) ;
        String content = "emailaddress=" + URLEncoder.encode("vijay@snapfish.com")
                      + "&password=" + URLEncoder.encode("vijay")  ;

        conn.makePOSTRequest("/loginsubmit",content);
        conn.makeGETRequest("/editprofile");
        conn.makeGETRequest("/getMailer");
    }



}  //End of CMakeHttpRequest class



