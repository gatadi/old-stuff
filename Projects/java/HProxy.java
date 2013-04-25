import java.io.*;
import java.net.*;

public class HProxy
{

    public static void main(String a[])
        throws Exception
    {
        String serverSocket = "80" ;
        String serverToConnect = null ;
        if(a.length>=2){
            serverSocket = a[1] ;
            serverToConnect = a[0].trim() ;
        }
        System.out.println("Listening on port=" + serverSocket);
        ServerSocket ss = new ServerSocket(Integer.parseInt(serverSocket));
        while (true)
        {
            Socket clisock = ss.accept();
            System.out.println("Request accepted");

            DataInputStream din = new DataInputStream(clisock.getInputStream());
            boolean isJSFile = false ;
            String line = din.readLine();
            System.out.println(line);

            int idx = line.indexOf(" ");
            int lidx = line.indexOf(" ", idx+1);
            String method = line.substring(0, idx);
            String url = line.substring(idx, lidx);
            url = "http://" + serverToConnect + url.trim() ;
            
            URL u = new URL(url);

            int port = u.getPort();
            if (port == -1) {
                port = 80;
            }

            Thread.sleep(1000);
            line = method+" "+u.getFile()+" HTTP/1.0";
            Socket psock = new Socket(u.getHost(), port);
            Thread t1 =
                new Thread
                (new CopyRunner(line, din, psock.getOutputStream(),
                                clisock, psock, isJSFile));
            Thread t2 =
                new Thread
                (new CopyRunner(null, psock.getInputStream(),
                                clisock.getOutputStream(),
                                clisock, psock, isJSFile));
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            try { psock.close(); } catch (Throwable th){}
            try { clisock.close(); } catch (Throwable th){}
        }
    }

    private final static class CopyRunner implements Runnable
    {
        private final InputStream m_in;
        private final OutputStream m_out;
        private final Socket m_s1, m_s2;
        private String m_fline;
        private boolean isJSFile = false;
        CopyRunner(String line, InputStream in, OutputStream out,
                   Socket s1, Socket s2, boolean isJSFile)
        {
            m_in = in;
            m_out = out;
            m_fline = line;
            m_s1 = s1;
            m_s2 = s2;
            this.isJSFile = isJSFile ;
        }

        public void run()
        {
            try
            {
                if (m_fline != null)
                {
                    DataInputStream din = (DataInputStream)m_in;
                    PrintStream out = new PrintStream(m_out);
                    out.print(m_fline);
                    out.print("\r\n");
                    out.flush();
                    while ((m_fline = din.readLine()) != null)
                    {
                        System.out.println(m_fline);
                        if (!m_fline.startsWith("Proxy")) {
                            out.print(m_fline);
                            out.print("\r\n"); out.flush();
                        }
                        if (m_fline.length()==0) {
                            break;
                        }
                    }
                    byte buf[] = new byte[4096];
                    int nread;
                    while ((nread = din.read(buf)) > 0) {
                        String s = new String(buf, 0, nread);
                        System.out.println(">> "+s);
                        out.write(buf, 0, nread); out.flush();
                    }
                }
                else {
                    long start = System.currentTimeMillis();
                    int cnt = 0;
                    byte buf[] = new byte[4096];
                    int nread;
                    while ((nread = m_in.read(buf)) > 0) {
                        if(true){
                            System.out.write(buf, 0, nread);
                            System.out.flush();
                        }

                        m_out.write(buf, 0, nread);
                        m_out.flush();
                        cnt+=nread;
                    }
                    long end = System.currentTimeMillis();
                    System.out.println("\t"+(end-start)+"msec\t\t"+cnt+" bytes");
                    try {m_s1.close();}catch(Throwable t){}
                    try {m_s2.close();}catch(Throwable t){}
                }
            } catch (Throwable th) { }
        }
    }
}
