import java.io.*;
import java.net.*;
import java.util.*;

public class CProxyServer
{
    static String proxyHost = "debian.usa.hp.com" ;
    static int proxyPort = 3128 ;
    static boolean useProxy = true ;

    static boolean logRequest = false ;
    static boolean logallRequestResponseHeaders = false ;
    static boolean logTimings = true ;

    static int id = 0;

    public static void main(String a[])
        throws Exception
    {

        CProxyServer proxy = new CProxyServer();

        proxy.startProxyServer();
    }

    public void startProxyServer() throws Exception
    {
        ServerSocket server = new ServerSocket(3128);
        while(true){
            id++ ;
            new Thread(new ProcessRequest(server.accept(), id)).start() ;
        }
    }



    private class ProcessRequest implements Runnable
    {
        private Socket m_src, m_dest;
        int m_id ;

        ProcessRequest(Socket src, int id)
        {
            m_src = src;
            m_id = id ;
        }

        public void run()
        {
            try
            {
                int n ;
                byte[] buf = new byte[4096];

                InputStream m_in = m_src.getInputStream() ;
                n = m_in.read(buf) ;
                String orinalRequestString = new String(buf, 0, n) ;

                if(logallRequestResponseHeaders){
                    System.out.println("\n[Request:" + m_id + "]\n" + orinalRequestString);
                }
                String reqLines = orinalRequestString ;

                Request r = parseRequest(orinalRequestString) ;


                if(logRequest && !logallRequestResponseHeaders){
                    System.out.println("\n[Request:" + m_id + "] => " + reqLines);
                }
                if( useProxy ){
                    m_dest = new Socket(proxyHost, proxyPort);
                }else{
                    m_dest = new Socket(r.host, 80);
                }

                OutputStream m_out = m_dest.getOutputStream() ;
                m_out.write(reqLines.getBytes());
                m_out.flush();

                r.time = System.currentTimeMillis() ;
                r.id = m_id ;

                Thread t1 = new Thread(new CopyRequest(m_in, m_out, m_src, m_dest));
                Thread t2 = new Thread(new CopyResponse(m_dest.getInputStream(), m_src.getOutputStream(),
                    m_src, m_dest, r));
                t1.start();
                t2.start();
            }
            catch(Throwable th)
            {
                th.printStackTrace();
            }
        }


        public Request parseRequest(String req)
        {
            StringBuffer buf = new StringBuffer() ;

            //split request in to multiple lines and parse the first line
            //the first line is expected to be in following format
            //METHOD uri httpversion
            //GET http://www.snapfish.com/snapfish/store/storecards2
            //GET http://www.snapfish.com/snapfish/fe/p/test/HelloWorld

            String uri = null ;
            String method = null ;
            String httpVersion = null ;
            String[] reqParts = null ;

            String[] reqLines = req.split("\n");
            if (reqLines != null & reqLines.length > 0)
            {
                reqParts = reqLines[0].split(" ");
                if(reqParts != null & reqParts.length > 2)
                {
                    method = reqParts[0] ;
                    uri = reqParts[1];
                    httpVersion = reqParts[2];
                }
            }

            //parse the uri "http://www.snapfish.com/snapfish/fe/p/test/HelloWorld" to
            // host: www.snapfish.com
            // cobrand: snapfish
            // remainingPart: /fe/p/test/HelloWorld

            String s = uri, host = null, cobrand = null, remainingPart = null ;
            String updatedUri ;

            int i1, i2=-1, i3 = -1 ;
            i1 = s.indexOf("/") ;
            if( i1 != -1 ){
                i2 = s.indexOf("/", i1+2) ;
            }
            if( i2 != -1 ){
                i3 = s.indexOf("/", i2+1) ;
            }
            if( i3 != -1 )
            {
                host = s.substring(i1+2, i2) ;
                cobrand = s.substring(i2+1, i3);
                remainingPart = s.substring(i3+1) ;
            }else if(i2 != -1)
            {
                host = s.substring(i1+2, i2) ;
                cobrand = s.substring(i2+1);
            }else if(i1 != -1)
            {
                host = s.substring(i1+2) ;
            }



            //Build new request without host, this is required if the request is not sent
            //through proxy server.
            updatedUri = method
                + (cobrand != null ? (" /" + cobrand ) : (remainingPart != null ? "" : "/"  ) )
                + (remainingPart != null ? ("/" + remainingPart + " ") : " ")
                + httpVersion ;


            buf.append(updatedUri + "\n") ;
            for(int i=1; i<reqLines.length; i++){
                buf.append(reqLines[i] + "\n") ;
            }
            buf.append("\n");

            Request r = new Request();
            r.requestLines = buf.toString() ;
            r.isSwiftUrl = ( (remainingPart != null) && remainingPart.startsWith("fe") )
                || ( (cobrand != null) && cobrand.startsWith("fe") )            ;
            r.host = host ;
            r.requestFirstLine = updatedUri;

            String pathInfo = (remainingPart != null) ? remainingPart : "" ;
            int i4 = pathInfo.indexOf("?") ;
            if( i4 != -1){
                pathInfo = pathInfo.substring(i4);
            }
            r.isPageRequest = (!pathInfo.endsWith(".gif"))
                && (!pathInfo.endsWith(".jpg"))
                && (!pathInfo.endsWith(".png"))
                && (!pathInfo.endsWith(".js"));

            return r ;
        }
    }

    private class Request
    {
        String requestLines ;
        String requestFirstLine ;
        String host ;
        boolean isSwiftUrl ;
        boolean isPageRequest ;
        long time ;
        int id ;
    }

    private class CopyRequest implements Runnable
    {
        private InputStream m_in ;
        private OutputStream m_out ;
        private Socket m_src, m_dest ;

        CopyRequest(InputStream src, OutputStream dest, Socket client, Socket remote) {
            m_in = src;
            m_out = dest;
            m_src = client ;
            m_dest = remote ;
        }

        public void run()
        {
            byte buf[] = new byte[1028];
            try
            {
                int n ;
                while ((n = m_in.read(buf)) > 0)
                {
                    m_out.write(buf, 0, n) ;
                    String str = new String(buf, 0, n) ;                    
                }
            }
            catch (Throwable th) {
                //th.printStackTrace();
            }
            finally {
                try { m_src.close(); }
                catch (Throwable th) {th.printStackTrace();}
                try { m_dest.close();  }
                catch (Throwable th) {th.printStackTrace();}
            }
        }
    }


    private class CopyResponse implements Runnable
    {
        private InputStream m_in ;
        private OutputStream m_out ;
        private Socket m_src, m_dest ;
        private Request m_r ;

        CopyResponse(InputStream src, OutputStream dest, Socket client, Socket remote, Request r) {
            m_in = src;
            m_out = dest;
            m_src = client ;
            m_dest = remote ;
            m_r = r;
        }

        public void run()
        {
            byte buf[] = new byte[512*10];
            try
            {
                int n = m_in.read(buf) ;
                String resp = new String(buf, 0, n) ;

                if(logallRequestResponseHeaders){
                    System.out.println("\n[Response:" + m_r.id + "]\n" + resp);
                }

                if( (resp.indexOf("Content-Type: image/gif") != -1)
                    || (resp.indexOf("Content-Type: image/jpeg") != -1)
                    || (resp.indexOf("Content-Type: image/png") != -1))
                {
                    m_out.write(buf, 0, n);
                }else
                {
                    m_out.write(resp.getBytes());
                }


                while ((n = m_in.read(buf)) > 0)
                {
                    m_out.write(buf, 0, n) ;
                }
                if(logTimings){
                    long t2 = System.currentTimeMillis()  ;
                    System.out.println("\n[Request:" + m_r.id + "]- (" + m_r.host + ") "
                      + m_r.requestFirstLine + "\n --> " + (t2-m_r.time)/1000.0 + " secs ");
                }
            }
            catch (Throwable th) {
                //th.printStackTrace();
            }
            finally {
                try { m_src.close(); }
                catch (Throwable th) {th.printStackTrace();}
                try { m_dest.close();  }
                catch (Throwable th) {th.printStackTrace();}
            }
        }
    }
}
