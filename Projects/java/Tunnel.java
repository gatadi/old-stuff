import java.io.*;
import java.net.*;

public class Tunnel
{

    public static void main(String a[])
        throws Exception
    {
        String rhost = a[0];
        int rport = Integer.parseInt(a[1]);

        int id=0;
        ServerSocket ss = new ServerSocket(Integer.parseInt(a[2]));
        while (true) {
            Socket clisock = ss.accept();
            System.out.println("Got client "+(++id));

            //Thread.sleep(1000);
            Socket psock = new Socket(rhost, rport);
            Thread t1 = new Thread(new CopyRunner(clisock, psock, id));
            Thread t2 = new Thread(new CopyRunner(psock, clisock, id));
            t1.start(); t2.start();
        }
    }

    private final static class CopyRunner implements Runnable
    {
        private final Socket m_src, m_dest;
        private final int m_id;
        CopyRunner(Socket src, Socket dst, int id) {
            m_src = src;
            m_dest = dst;
            m_id = id;
        }

        public void run() {
            byte buf[] = new byte[4096];
            try {
                InputStream inp = m_src.getInputStream();
                OutputStream out = m_dest.getOutputStream();
                int nread = 0;
                int totalBytes = 0;
                while ((nread = inp.read(buf)) > 0) {
                    String s = new String(buf, 0, nread);
                    if (s.indexOf("HTTP") >= 0) {
                        System.out.println("["+m_id+"] "+s);
                        System.out.flush();
                    }
                    else if( (s.indexOf("SOAP") >= 0) )
                    {
                        System.out.println("["+m_id+"] : "+s);
                    }
                    else {
                        //System.out.println("["+m_id+"] : "+nread);
                        //System.out.println("["+m_id+"] : "+s);
                    }
                    out.write(buf, 0, nread);
                    totalBytes = totalBytes+1;
                }
            }
            catch (Throwable th) {
            }
            finally {
                System.out.println("Closing "+m_id+"\n");
                try { m_src.close(); }
                catch (Throwable th) {}
                try { m_dest.close(); }
                catch (Throwable th) {}
            }
        }
    }
}
