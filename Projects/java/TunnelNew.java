import java.io.*;
import java.net.*;
import java.util.*;

public class TunnelNew
    implements Runnable
{
    public static void main(String a[])
        throws Exception
    {
        String rhost = a[0];
        int rport = Integer.parseInt(a[1]);

        ServerSocket ss = new ServerSocket(Integer.parseInt(a[2]));

        int id = 1;

        Socket client;
        while (true) {
            client = ss.accept();

            InputStream cin =
                client.getInputStream();
            OutputStream cout =
                client.getOutputStream();

            Socket remote = new Socket(rhost, rport);
            InputStream rin =
                remote.getInputStream();
            OutputStream rout =
                remote.getOutputStream();

            new Thread(new TunnelNew(cin, rout, ">>>", id)).start();
            new Thread(new TunnelNew(rin, cout, "<<<", -id)).start();
            id++;
        }
    }

    TunnelNew(InputStream in, OutputStream out,
           String pfx, int id)
    {
        m_in = in;
        m_out = out;
        m_pfx = pfx;
        m_id = id;
    }


    void logit(String s)
    {
        Date t = new Date(); //        long t = System.currentTimeMillis();
        if (m_id > 0) {
            System.out.println(m_id+" :"+t+" :"+m_pfx+" "+s);
        }
        else {
            System.out.println(-m_id+" :"+t+" :"+m_pfx+" "+s);
        }
    }

    public void run()
    {
        byte b[] = new byte[8192];
        byte tt[] = new byte[8192];
        int nread;
        try {
            while ((nread = m_in.read(b)) > 0) {
                System.arraycopy(b, 0, tt, 0, nread);
                try {
                    //Thread.sleep(250);
                } catch (Throwable th) {}

                for (int x=0; x<nread; x++) {
                    int c = tt[x];
                    if ((c >= 7) && (c <= 13)) {
                    }
                    else if ((c >= 32) && (c <= 126)) {
                    }
                    else {
                        tt[x] = '.';
                    }
                }

                if (m_id > 0) 
                {
                    String r = new String(tt, 0, 0, nread);
                    logit(r);
                }
                else 
                {
                    String r = new String(tt, 0, 0, nread);
                    logit(r);                    
                }
                m_out.write(b, 0, nread);
            }
        }
        catch (Throwable th) {
            logit("closed");
        }
        finally {
            try {m_in.close();}
            catch (Throwable x) {}
            try {m_out.close();}
            catch (Throwable x) {}
        }
    }

    private final  InputStream m_in;
    private final  OutputStream m_out;
    private final String m_pfx;
    private final int m_id;
}
