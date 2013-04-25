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
import javax.net.ssl.*;



public class CHTTPSocketConnection
{
    public static void main(String[] args)  throws Exception
    {
        /*
        CHTTPSocketConnection httpSocketConn = new CHTTPSocketConnection(
            "delhi", 80 ) ;
        String url = "/exuploadimages?authcode=1110822466731755:3f24982a6ab32:full1@snapfish.com&NumberImages=1&sourcecode=SFM&file=hiresfile";

        String file = "e:/workarea/javatest/hiresfile.jpg" ;
        httpSocketConn.uploadFile(url, file);


        CHTTPSocketConnection httpSocketConn = new CHTTPSocketConnection("snapfish", 80 ) ;
        System.out.print("Client:");
        String response = httpSocketConn.makePOSTRequest(
            "/loginsubmit", "emailaddress=full1@snapfish.com&password=snappy");

        */
        disableCertificateValiadation();
        //retrieveCertificates() ;
        
        CHTTPSocketConnection httpSocketConn = new CHTTPSocketConnection(
            "www.ttsvisas.com", 443, true ) ;
        
        // https://www.ttsvisas.com/StartModify.aspx
        String response = httpSocketConn.makeGETRequest("/StartModify.aspx") ;
        //System.out.println(response);
        
        //checkModify.aspx
        //txtDropboxNo=190620033648
        //txtEmail=vkgatadi@yahoo.com
        //txtPassport=E3129516
        
        /*
        String response = httpSocketConn.makePOSTRequest("/checkModify.aspx",
            "txtDropboxNo=190620033648&txtEmail=vkgatadi@yahoo.com&txtPassport=E3129516");
          */

        
    }   //end of main(String[])


   public static void disableCertificateValiadation()
   {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
        
        
    
    }   //end of disableCertificateValiadation()
            

    public static java.security.cert.Certificate[] retrieveCertificates()
    {
        java.security.cert.Certificate[] serverCerts = null ;
        try
        {
            // Create the client socket
            int port = 443;
            String hostname = "www.ttsvisas.com";
            SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
            SSLSocket socket = (SSLSocket)factory.createSocket(hostname, port);
    
            // Connect to the server
            socket.startHandshake();
    
            // Retrieve the server's certificate chain
            serverCerts = socket.getSession().getPeerCertificates();
    
            System.out.println("serverCerts.length=" + serverCerts.length);
            for(int  i=0; i<serverCerts.length; i++)
            {
                System.out.print(serverCerts[i]);
                System.out.print("\n************************\n");
            }
            // Close the socket
            socket.close();
        }
        catch (SSLPeerUnverifiedException e)
        {
            e.printStackTrace();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    
        return serverCerts;
    }   //end of retrieveCertificate()
    

    public CHTTPSocketConnection(String host, int port, boolean isSSL)
    {
        this.host = host ;
        this.port = port ;
        this.isSSL = isSSL ;
        if( isSSL )
        {
            java.security.Security.addProvider( new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            
            //System.setProperty ("javax.net.ssl.trustStore", "keystorefiles");
            //System.setProperty ("javax.net.ssl.trustStorePassword", "keypass");
        }




    }   //end of CHTTPSocketConnection

    public void setUserAgent(String userAgent)
    {
        this.userAgent = userAgent ;
    }

    public void setDefaultUserAgent()
    {
        this.userAgent = DEFAULT_USER_AGENT ;
    }

    public String makeGETRequest(String file) throws Exception
    {
        //System.out.println("-----------------------------------------");
        //System.out.println("          GET-Request=" + file);
        //System.out.println("-----------------------------------------");

        this.location = null ;
        Socket socket = createSocket() ;

        //Create an output stream to write  the request header
        DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());

        // start of building header
        String data = "GET " + file + " HTTP/1.0\r\n";
        System.out.println(data);
        //String data = "GET http://" + url.getHost() + url.getFile() + " HTTP/1.0\r\n";
        data += "Accept: Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, Application/vnd.ms-excel, application/msword, */*" + "\r\n";
        data += "User-Agent: " + userAgent + "\r\n";
        data += "Host: " + this.host + "\r\n";
        //data += "Connection: Keep-Alive" + "\r\n"; ;
        //data += "Cookie: " + this.cookies + "\r\n";

        //System.out.println("\nSending cookies to server ...\n");
        if( this.sessionCookie != null ) {
            data += this.sessionCookie + "\r\n" ;
            //System.out.println(this.sessionCookie) ;
        }   //end of if

        if( this.cookies != null ){
            data += this.cookies + "\r\n" ;
            //System.out.println(this.cookies + "\r\n") ;
        }   //end of if
        // end header
        data += "\r\n";

        outStream.writeBytes(data) ;
        outStream.flush() ;
        String responseString = readServerData(socket) ;

        socket.close() ;

        if( this.location != null )
        {
            if(!this.location.startsWith("http") ){
                this.location = "http://" + this.host + this.location ;
            }
            redirectTo(this.location) ;
        }   //end of if

        return responseString ;
    }   //end of makeGETRquest(String)



    public String makePOSTRequest(String file, String content) throws Exception
    {
        //System.out.println("-----------------------------------------");
        //System.out.println("           POST-Request=" + file );
        //System.out.println("-----------------------------------------");

        this.location = null ;
        Socket socket = createSocket() ;

        //Create an output stream to write  the request header
        DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
        String data = "POST " + file + "  HTTP/1.0\r\n";
        System.out.println(data);
        //start of header
        //data += "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, Application/vnd.ms-excel, application/msword, */*" + "\r\n";
        data += "User-Agent: " + userAgent + "\r\n";
        data += "Host: " + this.host + "\r\n";
        data += "Content-type: application/x-www-form-urlencoded\r\n";
        data += "Content-length: " + content.length() + "\r\n";
        //data += "Connection: Keep-Alive" + "\r\n"; ;
        //data += "Cookie: " + this.cookies + "\r\n";

        //System.out.println("\nSending cookies to server ...\n");
        if( this.sessionCookie != null ) {
            data += this.sessionCookie + "\r\n" ;
            //System.out.println(this.sessionCookie + "\r\n") ;
        }   //end of if

        if( this.cookies != null ){
            data += this.cookies + "\r\n" ;
            //System.out.println(this.cookies + "\r\n") ;
        }   //end of if

        //end of header
        data += "\r\n";

        //Write content information
        data += content + "\r\n";
        //end content
        data += "\r\n";

        outStream.writeBytes(data) ;
        outStream.flush();
        String responseString = readServerData(socket) ;
        socket.close();

        if( this.location != null )
        {
            if(!this.location.startsWith("http") ){
                this.location = "http://" + this.host + this.location ;
            }
            redirectTo(this.location) ;
        }   //end of if

        return responseString ;
    }   //end of makePOSTRequest(String, String)


    public String uploadFile(String url, String file) throws Exception
    {
        System.out.println("-----------------------------------------");
        System.out.println("           Upload local-file=" + file );
        System.out.println("-----------------------------------------");

        this.location = null ;
        Socket socket = createSocket() ;

        //Create an output stream to write  the request header
        DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
        String data = "POST " + url + " HTTP/1.0\r\n";
        //start of header
        data += "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, Application/vnd.ms-excel, application/msword, */*" + "\r\n";
        data += "User-Agent: " + userAgent + "\r\n";
        data += "Host: " + this.host + "\r\n";
        data += "Content-type: multipart/form-data; boundary=\"AABBCC\"\r\n";
        data += "Content-length: " + 335301 + "\r\n";
        //data += "Connection: Keep-Alive" + "\r\n"; ;
        //data += "Cookie: " + this.cookies + "\r\n";

        //end of header
        data += "\r\n";
        outStream.writeBytes(data) ;



        //start boundary [MIME HEADER]
        data =  "--AABBCC\r\n";
        data += "content-disposition: form-data; filename=\"hiresfile.jpg\"\r\n" ;
        //data += "Content-type: image/jpeg\r\n";
        //data += "Content-Transfer-Encoding: binary\r\n";
        data += "\r\n";
        outStream.writeBytes(data) ;

        //Write content [MIME DATA]
        BufferedInputStream bin = new BufferedInputStream(
            new FileInputStream(file)) ;
        byte[] buffer = new byte[1024*64] ; //10KB  335301
        int n = -1 ;
        while( (n=bin.read(buffer)) != -1 )
        {
            outStream.write( buffer, 0, n) ;
        }
        outStream.flush();
        data ="--AABBCC";
        data.getBytes() ;
        //data += "\r\n" ;
        outStream.write(data.getBytes()) ;
        outStream.flush() ;
        //readServerData(socket) ;
        socket.close();


        if( this.location != null )
        {
            if(!this.location.startsWith("http") ){
                this.location = "http://" + this.host + this.location ;
            }
            System.out.println(this.location);
            //redirectTo(this.location) ;
        }   //end of if

        return null ;
    }   //end of makePOSTRequest(String, String)


    private void redirectTo(String urlString) throws Exception
    {
        URL url = new URL(urlString) ;
        makeGETRequest(url.getFile());

    }   //end of redirectTo(String)

    private Socket createSocket() throws Exception
    {
        if(this.isSSL){
            return createSSLSocket() ;
        }else{
            return new Socket(this.host, this.port );
        }   //end of if

    }   //end of createSocket(String)


    private Socket createSSLSocket() throws Exception
    {
    /*
        SSLSocketFactory sslFact
            = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket)sslFact.createSocket(
            this.host, this.port );
        //socket.startHandshake();
        socket.setSoTimeout(timeOutSecs);
    */
        SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
        SSLSocket socket = (SSLSocket)factory.createSocket(this.host, this.port);
    

        return socket ;
    }   //end of createSSLSocket(String)



    private String readServerData(Socket socket) throws Exception
    {
        // create input stream
        InputStream in = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        //System.out.println("-----------------------------------------");
        //System.out.println("     Reading cookies from server    ");
        //System.out.println("-----------------------------------------");
        PrintWriter fWriter = new PrintWriter(new FileOutputStream("e:/temp/welcome.html"));
        StringBuffer buff = new StringBuffer();
        String thisLine = null ;
        String tempCookies = "" ;

        while ((thisLine = br.readLine()) != null)
        {
            //System.out.println(thisLine);
            // sometimes the server may send two headers
            //we wish to discard the  first
            if (thisLine.equalsIgnoreCase("HTTP/1.0 100 Continue"))
            {
                // read empty lines till we get to the end of this header
                while ((br.readLine()).length() != 0) continue;
            }

            //first extract our desired values from the header
            if (thisLine.startsWith("Location:"))
            {
                System.out.println(thisLine);
                this.location = thisLine.substring(this.LOCATION.length());
                this.location = this.location.trim();
            }
            else
            {
                if (thisLine.toLowerCase().startsWith("set-cookie:"))
                {
                    //System.out.println(thisLine);
                    String cookie = thisLine.substring(this.SET_COOKIE.length());
                    if( this.sessionCookie == null && thisLine.toLowerCase().indexOf("weblogicsession") != -1 )
                        this.sessionCookie = this.COOKIE + cookie ;
                    else
                        tempCookies += this.COOKIE + cookie  + "\r\n";

                }   //end of if(thisLine.startsWith...)
                else
                {
                    fWriter.println(thisLine);
                    buff.append(thisLine+"\n");
                }
            }   //end of if/else

        }   //end of while
        fWriter.close();

        in.close();

        if( tempCookies != null && tempCookies.length() > 1 ) {
            this.cookies = tempCookies ;
        }   //end of if

        return buff.toString() ;
    }   //end of readServerData(Socket)


    public String getSessionCookie()
    {
        return this.sessionCookie ;
    }   //end of getSessionCookie()

    public Vector getCookies()   throws Exception
    {
        Socket socket = createSocket() ;
        DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
        // start of building header
        String data = "GET " + "/" + " HTTP/1.0\r\n";
        data += "User-Agent: " + userAgent  + "\r\n";
        data += "Host: " + this.host + "\r\n";
        data += "\r\n";

        outStream.writeBytes(data) ;
        outStream.flush() ;

        // create input stream
        InputStream in = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String thisLine = null ;
        Vector vCookies = new Vector() ;

        while ((thisLine = br.readLine()) != null)
        {
            if (thisLine.toLowerCase().startsWith("set-cookie:"))
            {
                vCookies.add(thisLine.substring("set-cookie: ".length()));
            }
        }   //end of while

        socket.close() ;
        outStream.close();
        in.close();

        return vCookies ;

    }   //end of getCookies()


    public static String[] parseResponse(String repsonseData)
    {
        String parsedData[] = new String[2] ;
        StringBuffer headers = new StringBuffer() ;
        StringBuffer data = new StringBuffer() ;
        try{
            BufferedReader bR = new BufferedReader(new StringReader(repsonseData));
            String line = null ;
            int emptyLineCount = 0 ;

            //skip all empty lines
            while((line=bR.readLine()) != null){
                 if(line.trim().length() == 0){
                    continue ;
                 }else{
                    break ;
                 }
            }    //end of while

            while( (line=bR.readLine()) != null)
            {
                if(line.trim().length() == 0){
                    emptyLineCount++ ;
                }
                if(emptyLineCount>=1){
                    data.append(line + "\n") ;
                }else{
                    headers.append(line + "\n");
                }

            }   //end of while
        }catch(Exception e){
            e.printStackTrace();
        }
        parsedData[0] = headers.toString() ;
        parsedData[1] = data.toString();

        return parsedData ;
    }   //end of parseResponse(String)



    private String sessionCookie = null ;
    private Vector vCookies = new Vector() ;
    private String host = null ;
    private int port = 80 ; //default port
    private boolean isSSL = false ;
    private String HTTP_PROTOCOL = "http" ;
    private String HTTPS_PROTOCOL = "https" ;
    private String SET_COOKIE = "Set-Cookie:" ;
    private String COOKIE = "Cookie:" ;
    private String LOCATION = "Location:" ;
    private int timeOutSecs = 1000*10 ; //default 10 secs
    private String userAgent = "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0;)" ;
    public static final String DEFAULT_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0;)" ;


    private String location = null ;
    private String cookies = null ;
}   //end of class CHTTPSocketConnection
