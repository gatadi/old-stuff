
import java.util.Vector ;
import java.net.URL ;
import java.net.HttpURLConnection ;
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

public class CHTTPURLConnection
{
    private URL url = null ;
    private String file = null ;
    private HttpURLConnection urlConnection = null ;
    private Vector vCookies = new Vector();

    public CHTTPURLConnection(String url) throws Exception
    {
        this(url, null, null) ;
    }

    public CHTTPURLConnection(String url, String file) throws Exception
    {
        this(url, null, file) ;
    }

    public CHTTPURLConnection(String url, Vector cookies) throws Exception
    {
        this(url, cookies, null);
    }

    public CHTTPURLConnection(String url, Vector cookies, String file) throws Exception
    {
        this.url = new URL(url) ;
        this.urlConnection = (HttpURLConnection)this.url.openConnection() ;
        this.urlConnection.setFollowRedirects(false);
        if( cookies != null ) this.vCookies = cookies ;
        this.file = file ;

    }   //end of  CHTTPURLConnection()

    public String doGET() throws Exception
    {
        print("\nRequest=http://" + this.url.getHost() + this.url.getFile());

        urlConnection.setRequestMethod("GET");
        sendCookies() ;

        print("ResponseCode=" + urlConnection.getResponseCode());
        print("ResponseMessage=" + urlConnection.getResponseMessage());
        print("ContentLength="+ urlConnection.getContentLength());
        String cookies = urlConnection.getHeaderField("Set-Cookie") ;
        print("Cookies = "+ cookies);
        setCookies(parseCookieString(cookies));

        String redirectURL = urlConnection.getHeaderField("Location") ;
        if( redirectURL != null ){
            return redirect(redirectURL) ;
        }   //end of if

        return read() ;


    }   //end of doGET()


    public String   doPOST(String content) throws Exception
    {
        print("\nRequest=http://" + this.url.getHost() + this.url.getFile());

        urlConnection.setDoOutput(true);
        sendCookies() ;

        PrintWriter out = new PrintWriter( urlConnection.getOutputStream() ) ;
        out.print(content) ;
        out.flush();
        out.close() ;

        print("ResponseCode=" + urlConnection.getResponseCode());
        print("ResponseMessage=" + urlConnection.getResponseMessage());
        print("ContentLength="+ urlConnection.getContentLength());

        String redirectURL = urlConnection.getHeaderField("Location") ;
        if( redirectURL != null ){
            return redirect(redirectURL) ;
        }   //end of if

        return read() ;

    }   //end of doPOST()

    private String redirect(String urlAsString) throws Exception
    {
        urlAsString = urlAsString.trim() ;
        if( ! urlAsString.startsWith("http") )
        {
            String port = "" ;
            if(this.url.getPort() != -1 )
                port = ":" + this.url.getPort() ;
            urlAsString = "http://" + this.url.getHost() + port
                + urlAsString ;
        }   //end of if

        print("\nRedirecting to=" + urlAsString);

        String cookies = urlConnection.getHeaderField("Set-Cookie") ;
        print("Cookies = "+ cookies);
        setCookies(parseCookieString(cookies));

        CHTTPURLConnection http
            = new CHTTPURLConnection(urlAsString, this.vCookies) ;

        return http.doGET() ;
    }   //end of recirect()


    public String read() throws Exception
    {

        java.io.BufferedWriter out = null ;

        if( this.file != null )
        {
            out = new java.io.BufferedWriter(
               new java.io.FileWriter(new java.io.File(this.file)));
        }
        BufferedReader reader = new BufferedReader(
            new InputStreamReader( urlConnection.getInputStream() ) );
        String line = null ;
        StringBuffer data = new StringBuffer() ;
        while( (line=reader.readLine()) != null )
        {
            data.append(line + "\n");
            if( out != null ){
                out.write(line + "\n");
            }
        }   //end  of while

        if( out != null ){
            out.close() ;
        }

        reader.close() ;

        return data.toString() ;
    }   //end of read()


    public Vector parseCookieString(String cookies)
    {
        Vector v = new Vector(2);
        if( cookies != null ){
            v.add(cookies);
        }

        return v ;
    }


    public void setCookies(Vector cookies)
    {
        this.vCookies = cookies ;
    }   //end of setCookies()

    public Vector getCookies()
    {

        return vCookies  ;
    }   //end of getCookies()

    public void sendCookies()
    {
        print("Sending cookies to server...");
        for(int i=0; i<vCookies.size() ; i++)
        {
            String cookie = vCookies.elementAt(i).toString() ;
            if( cookie.indexOf("secure") == -1 ){
                urlConnection.setRequestProperty("Cookie", cookie.trim());
                print(cookie);
            }   //end of if

        }   //end of for
        print("\n");

    }   //end of setCookies()

    private void print(String str)
    {
        System.out.println(str);
    }



    //url -> http://snapfish/dm1//hr/testdata/Upload_full1//3243675Xpic1hr.jpg
    public static byte[] getFile(String url) throws Exception
    {
        URL _url = new URL(url) ;
        HttpURLConnection urlConnection = (HttpURLConnection)_url.openConnection() ;
        urlConnection.setFollowRedirects(false);
        urlConnection.setRequestMethod("GET");

        System.out.println("ResponseCode=" + urlConnection.getResponseCode());
        System.out.println("ResponseMessage=" + urlConnection.getResponseMessage());

        String redirectURL = urlConnection.getHeaderField("Location") ;
        System.out.println("RedirectURL=" + redirectURL);

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



    public Vector testCookies()  throws Exception
    {
        Socket socket = new Socket(this.url.getHost(),
            (this.url.getPort() == -1 ) ? 80 : this.url.getPort()) ;
        DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
        // start of building header
        String data = "GET " + this.url.getFile() + " HTTP/1.0\r\n";
        data += "User-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0;)"  + "\r\n";
        //data += "User-Agent: Java1.2.2"  + "\r\n";
        data += "Host: " + this.url.getHost() + "\r\n";
        data += "Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2" + "\r\n" ;
        //data += "Content-type: application/x-www-form-urlencoded" + "\r\n" ;
        //data += "Connection: Keep-Alive" + "\r\n" ;

        print("Cookies= " ) ;

        for(int i=0; i<vCookies.size() ; i++)
        {
            String cookie = vCookies.elementAt(i).toString() ;
            if( cookie.indexOf("secure") == -1 ){
                data += "Cookie: " + cookie + "\r\n";
                print(cookie);
            }
        }   //end of for
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
                vCookies.add(thisLine.substring("set-cookie: ".length()).trim());
                print(thisLine);
            }
        }   //end of while

        socket.close() ;
        outStream.close();
        in.close();

        return vCookies ;

    }   //end of testCookies()




    public static void main(String[] args)  throws Exception
    {
    /*
        String baseURL = "http://snapfish/default/jsp/User/welcome.jsp" ;
        String filePath = ""; //"/dm1/hr/testdata/Upload_full1/3243675Xpic1hr.jpg" ;
        String url = baseURL + filePath ;

        3243675_44407274=/dm1//hr/testdata/Upload_full1//3243675Xpic1hr.jpg
        3243675_44407275=/dm1//hr/testdata/Upload_full1//3243675Xpic2hr.jpg
        3243675_44407276=/dm1//hr/testdata/Upload_full1//3243675Xpic3hr.jpg
        3243675_44407277=/dm1//hr/testdata/Upload_full1//3243675Xpic4hr.jpg
        3243675_44407278=/dm1//hr/testdata/Upload_full1//3243675Xpic5hr.jpg
        3243675_44407279=/dm1//hr/testdata/Upload_full1//3243675Xpic6hr.jpg
        3243681_44407305=/dm1//hr/testdata/Upload_full1//3243675Xpic2hr.jpg

        byte[] data = CHTTPURLConnection.getFile(url) ;
        //CHTTPURLConnection.saveFile(data, "3243675", "Picture1.jpg");

      */

        String str = new CHTTPURLConnection("http://acharya.iitm.ac.in/multi_sys/aditya.txt").doGET() ;
        str =  str.substring(0, 100)   ;
        java.util.Locale locale = new java.util.Locale("HI", "IN");
        System.out.println(str);
        java.text.BreakIterator iterator = java.text.BreakIterator.getCharacterInstance(locale) ;
        iterator.setText(str) ;

        char[] cs = str.toCharArray() ;

        int boundary = iterator.first();

        int i=0;
        java.text.CharacterIterator iter = iterator.getText();
        for(char c = iter.first(); c != java.text.CharacterIterator.DONE; c = iter.next()) {
             System.out.println((int)c + "=" + c + "=" + cs[i++]);
        }


    }

}   //end of CHTTPURLConnection

