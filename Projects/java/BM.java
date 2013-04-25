import java.io.*;
import java.net.*;
import java.util.*;

public class BM
{
    public static void main(String a[])
        throws IOException
    {
        String wwwhost = a[0];
        String uplhost = a[1];

        mark();
        System.out.println(new Date());
        Socket wh = new Socket(wwwhost, 80);
        PrintStream whps = new PrintStream(wh.getOutputStream(), true);
        DataInputStream win = new DataInputStream(new BufferedInputStream(wh.getInputStream()));

        whps.println("GET / HTTP/1.0\r");
        whps.println("User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0; Q312461)\r");
        whps.println("Host: "+wwwhost+"\r");
        whps.println("\r");
        String line;
        String cookie=null;
        while ((line = win.readLine()) != null) {
            if (line.startsWith("Set-cookie: ")) {
                String ck = line.substring("Set-cookie: ".length(),
                                           line.length());
                int id = ck.indexOf(";");
                if (id >= 0) {
                    ck = ck.substring(0, id);
                }
                if (!ck.endsWith("=") &&
                    !ck.startsWith("SF_CisForCookie")) {
                    if (cookie == null) {
                        cookie = ck;
                    }
                    else {
                        cookie += "; ";
                        cookie += ck;
                    }
                }
            }
            //System.out.println(line);
        }
        wh.close();

        //System.out.println("Cookie: "+cookie+"\r");
        endMark("Home");

        mark();
        wh = new Socket(wwwhost, 80);
        whps = new PrintStream(wh.getOutputStream());
        win = new DataInputStream(new BufferedInputStream(wh.getInputStream()));
        String loginstr = "emailaddress=kbs@snapfish.com&password=kbs%2Bcindy&log+in.x=0&log+in.y=0";
        //System.out.println("login len = "+loginstr.length());
        whps.println("POST /loginsubmit/t_=0 HTTP/1.0\r");
        whps.println("Content-type: application/x-www-form-urlencoded\r");
        whps.println("User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0; Q312461)\r");
        whps.println("Host: "+wwwhost+"\r");
        whps.println("Content-Length: "+loginstr.length()+"\r");
        whps.println("Cookie: "+cookie+"\r");
        whps.println("\r");
        whps.print(loginstr);
        whps.flush();
        while ((line = win.readLine()) != null) {
            if (line.startsWith("Set-cookie: SF_SESSION_COOKIE")) {
                String ck = line.substring("Set-cookie: ".length(),
                                           line.length());
                ck = ck.substring(0, ck.indexOf(";"));
                cookie += ("; "+ck);
            }
        }
        wh.close();
        endMark("Login");

        mark();
        wh = new Socket(wwwhost, 80);
        whps = new PrintStream(wh.getOutputStream());
        win = new DataInputStream(new BufferedInputStream(wh.getInputStream()));

        whps.println("GET /accesscache HTTP/1.0");
        whps.println("Cookie: "+cookie);
        whps.println("User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0; Q312461)");
        whps.println("Host: "+wwwhost);
        whps.println();
        line=null;
        while ((line = win.readLine()) != null) {
            if (line.indexOf("This cache is on") >= 0) {
                System.out.println(line);
            }
        }
        whps.close();
        endMark("Identify machine");

        mark();
        wh = new Socket(wwwhost, 80);
        whps = new PrintStream(wh.getOutputStream());
        win = new DataInputStream(new BufferedInputStream(wh.getInputStream()));

        whps.println("GET /dragdropupload/AlbumID=3260644/AlbumCaption=album%201020/t_=500021 HTTP/1.0\r");
        whps.println("User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0; Q312461)\r");
        whps.println("Host: "+wwwhost+"\r");
        whps.println("Cookie: "+cookie+"\r");
        whps.println("\r");
        String uid = null;
        while ((line = win.readLine()) != null) {
            if (line.startsWith("      uploadID = \"")) {
                uid =
                    line.substring("      uploadID = \"".length(),
                                   line.length());
                uid = uid.substring(0, uid.indexOf("\""));
                //System.out.println("UploadID="+uid);
            }
            //System.out.println(line);
        }
        wh.close();
        endMark("get upload id");

        wh = new Socket(wwwhost, 80);
        whps = new PrintStream(wh.getOutputStream());
        win = new DataInputStream(new BufferedInputStream(wh.getInputStream()));

        whps.println("GET /dragdropupload/AlbumID=3260644/AlbumCaption=album%201020/UploadManager.psp?Cmd=StartUpload&NumImages=1&U=dev&P=&PD=USER_OID=500021|AlbumID=3260644|UploadId="+uid+" HTTP/1.0\r");
        whps.println("User-Agent: PWComm\r");
        whps.println("Host: "+wwwhost+"\r");
        whps.println("Cookie: "+cookie+"\r");
        whps.println("\r");
        while ((line = win.readLine()) != null) {
            //System.out.println(line);
        }
        wh.close();
        endMark("Initialize");

        mark();
        wh = new Socket(wwwhost, 80);
        whps = new PrintStream(wh.getOutputStream());
        win = new DataInputStream(new BufferedInputStream(wh.getInputStream()));

        whps.println("GET /dragdropupload/AlbumID=3260644/AlbumCaption=album%201020/UploadManager.psp?Cmd=StartTransfer&NumImages=1&U=dev&P=&ID="+uid+"&PD=USER_OID=500021|AlbumID=3260644|UploadId="+uid+" HTTP/1.0\r");
        whps.println("User-Agent: PWComm\r");
        whps.println("Host: "+wwwhost+"\r");
        whps.println("Cookie: "+cookie+"\r");
        whps.println("\r");
        String targid = null;
        while ((line = win.readLine()) != null) {
            if (line.startsWith("        <File>")) {
                targid = line.substring("        <File>".length(),
                                        line.length());
                targid=targid.substring(0, targid.indexOf("</File>"));
            }
            //System.out.println(line);
        }
        //System.out.println("Targid = "+targid);
        wh.close();
        endMark("Get image path");

        mark();
        Socket uh = new Socket(uplhost, 80);
        PrintStream uhps = new PrintStream(uh.getOutputStream());
        DataInputStream uin = new DataInputStream(uh.getInputStream());

        byte buffer[] = new byte[4096];
        FileInputStream fin = new FileInputStream(a[2]);
        int filelen = fin.available();
        uhps.println("PUT /"+targid+" HTTP/1.0\r");
        uhps.println("Host: "+uplhost+"\r");
        uhps.println("Content-Length: "+filelen+"\r");
        uhps.println("Authorization: Basic "+
                     base64Encode("uploader:snapf1sh")+"\r");
        //System.out.println(base64Encode("uploader:snapf1sh"));
        uhps.println("\r");
        int len;
        while ((len = fin.read(buffer)) > 0) {
            uhps.write(buffer, 0, len);
        }
        uhps.flush();
        fin.close();

        while ((line = uin.readLine()) != null) {
            //System.out.println(line);
        }
        uh.close();
        endMark("upload");

        mark();
        wh = new Socket(wwwhost, 80);
        whps = new PrintStream(wh.getOutputStream());
        win = new DataInputStream(new BufferedInputStream(wh.getInputStream()));

        String pc = "Cmd=EndTransfer&PD=USER_OID=500021|AlbumID=3260644|UploadId="+uid+"&TransferInfo=<?xml+version=\"1.0\" ?><EndTransfer><Version>1.0.0</Version><User>dev</User><Password></Password><UploadID>"+uid+"</UploadID><NumFiles>1</NumFiles><UploadFile><UploadName>"+targid+"</UploadName><UserFileName>"+a[2]+"</UserFileName><FileSize>"+filelen+"</FileSize></UploadFile></EndTransfer>";
        whps.println("POST /dragdropupload/AlbumID=3260644/AlbumCaption=album%201020/UploadManager.psp HTTP/1.0\r");
        whps.println("User-Agent: PWComm\r");
        whps.println("Content-Type: application/x-www-form-urlencoded\r");
        whps.println("Content-Length: "+pc.length()+"\r");
        whps.println("Host: "+wwwhost+"\r");
        whps.println("Cookie: "+cookie+"\r");
        whps.println("\r");
        whps.print(pc);
        whps.flush();
        while ((line = win.readLine()) != null) {
            //System.out.println(line);
        }
        wh.close();
        endMark("albumize");

        mark();
        wh = new Socket(wwwhost, 80);
        whps = new PrintStream(wh.getOutputStream());
        win = new DataInputStream(new BufferedInputStream(wh.getInputStream()));

        whps.println("GET /dragdropupload/AlbumID=3260644/AlbumCaption=album%201020/UploadManager.psp?Cmd=EndUpload&U=dev&P=&ID="+uid+"&PD=USER_OID=500021|AlbumID=3260644|UploadId="+uid+" HTTP/1.0\r");
        whps.println("User-Agent: PWComm\r");
        whps.println("Host: "+wwwhost+"\r");
        whps.println("Cookie: "+cookie+"\r");
        whps.println("\r");
        while ((line = win.readLine()) != null) {
            //System.out.println(line);
        }
        wh.close();
        endMark("Notify end");

        mark();
        wh = new Socket(wwwhost, 80);
        whps = new PrintStream(wh.getOutputStream());
        win = new DataInputStream(new BufferedInputStream(wh.getInputStream()));

        whps.println("GET /dragdropuploadconfirm/UploadId="+uid+"/AlbumID=3260644/AlbumCaption=album+1020/t_=500021/UPLOAD_ERROR_CODE=1 HTTP/1.0\r");
        whps.println("User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 4.0; Q312461)\r");
        whps.println("Host: "+wwwhost+"\r");
        whps.println("Cookie: "+cookie+"\r");
        whps.println("\r");
        boolean all_succ = false;
        while ((line = win.readLine()) != null) {
            if (line.indexOf("blank-upload-all-success_SF_TRACK") >=0) {
                all_succ = true;
            }
            //System.out.println(line);
        }
        wh.close();
        endMark("Confirm status: "+(all_succ?"successful":"failed"));
        System.exit(0);
    }



    public final static String base64Encode(String strInput) {
        if (strInput == null)  return  null;


        byte byteData[] = new byte[strInput.length()];
        strInput.getBytes(0, strInput.length(), byteData, 0);
        return new String(base64Encode(byteData), 0);
    }

    public final static byte[] base64Encode(byte[] byteData) {
        if (byteData == null)  return  null;
        int iSrcIdx;      // index into source (byteData)
        int iDestIdx;     // index into destination (byteDest)
        byte byteDest[] = new byte[((byteData.length+2)/3)*4];

        for (iSrcIdx=0, iDestIdx=0; iSrcIdx < byteData.length-2; iSrcIdx += 3) {
            byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx] >>> 2) & 077);
            byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx+1] >>> 4) & 017 |
                                           (byteData[iSrcIdx] << 4) & 077);
            byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx+2] >>> 6) & 003 |
                                           (byteData[iSrcIdx+1] << 2) & 077);
            byteDest[iDestIdx++] = (byte) (byteData[iSrcIdx+2] & 077);
        }

        if (iSrcIdx < byteData.length) {
            byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx] >>> 2) & 077);
            if (iSrcIdx < byteData.length-1) {
                byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx+1] >>> 4) & 017 |
                                               (byteData[iSrcIdx] << 4) & 077);
                byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx+1] << 2) & 077);
            }
            else
                byteDest[iDestIdx++] = (byte) ((byteData[iSrcIdx] << 4) & 077);
        }

        for (iSrcIdx = 0; iSrcIdx < iDestIdx; iSrcIdx++) {
            if      (byteDest[iSrcIdx] < 26)  byteDest[iSrcIdx] = (byte)(byteDest[iSrcIdx] + 'A');
            else if (byteDest[iSrcIdx] < 52)  byteDest[iSrcIdx] = (byte)(byteDest[iSrcIdx] + 'a'-26);
            else if (byteDest[iSrcIdx] < 62)  byteDest[iSrcIdx] = (byte)(byteDest[iSrcIdx] + '0'-52);
            else if (byteDest[iSrcIdx] < 63)  byteDest[iSrcIdx] = (byte) '+';
            else                              byteDest[iSrcIdx] = (byte) '/';
        }

        for ( ; iSrcIdx < byteDest.length; iSrcIdx++)
            byteDest[iSrcIdx] = (byte) '=';

        return byteDest;
    }

    private final static void mark()
    { s_start = System.currentTimeMillis(); }

    private final static void endMark(String msg)
    {
        s_end = System.currentTimeMillis();
        System.out.println(msg+": "+(s_end-s_start)/1000.0+" sec");
    }
    private static long s_start = 0;
    private static long s_end = 0;
}
