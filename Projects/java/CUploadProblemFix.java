import java.io.*;
import java.net.*;
import java.util.*;

import com.sun.xml.tree.XmlDocument ;
import com.snapfish.upload.CPixamiUploadHandler ;
import javax.naming.Context ;

import org.w3c.dom.Document;
import org.w3c.dom.Node ;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList ;
import com.snapfish.extapi.CSoapXMLDocument ;

public class CUploadProblemFix
{
    String host = "delhi" ;
    String uploadHost = "delhi" ;
    String emailaddress = "full1@snapfish.com" ;
    String password = "snappy" ;
    String userOid = "3085785" ;
    String uploadID = (new Long(System.currentTimeMillis())).toString();
    String albumOid = "3243724" ;
    int numOfImages = 1 ;
    long fileSize = 0 ;
    String targetUploadFileName = "NOT-INITIALIZED" ;
    String fileName = "e:\\temp\\aish1.jpg" ;// "e:\\gatadi\\images\\AishwaryaRai1\\aish1.jpg"  ;
    CHTTPSocketConnection httpSocketConn = null ;
    
    
    
    public static void main(String[] args)  throws Exception
    {
        CUploadProblemFix uploader = new CUploadProblemFix() ;
        uploader.start() ;
    }
    
    
    public CUploadProblemFix()
    {
        httpSocketConn = new CHTTPSocketConnection(host, 80 ) ;
        
    }   //end of CUploadProblemFix()
    
    
    public void start() throws Exception
    {
        login() ;
        StartUpload() ;
        StartTransfer(1);
        for(int i=1; i<=20; i++)
        {
            //StartTransfer(i);
            if( i == 10 || i==15) ;
            else
                UploadFile(i) ;
            EndTransfer(i) ;
        }
        
        EndUpload() ;
        
    }   //end of start()
    
    public void login() throws Exception
    {
        //*********************************************************************
        //Logging to the web site
        printClient("loginsubmit");
        String response = httpSocketConn.makePOSTRequest(
            "/loginsubmit", "emailaddress=" + URLEncoder.encode(emailaddress)
            + "&" + "password=" + URLEncoder.encode(password));
        printServer(httpSocketConn.parseResponse(response)[0]); //header

    }   //end of login()
    
    
    public void StartUpload() throws Exception
    {
        /**********************************************************************
                               StartUpload
        **********************************************************************/
        printClient("StartUpload");
        httpSocketConn.setUserAgent("PWComm") ;
        String url = getURL("StartUpload") ;
        String response = httpSocketConn.makeGETRequest(url);
        String xmlResponse = httpSocketConn.parseResponse(response)[1] ; //data
        printServer(xmlResponse); //data

     }  //end of StartUpload()
    
    
     public void StartTransfer(int fileNumber) throws Exception
     {
        /**********************************************************************
                               StartTransfer
        **********************************************************************/
        printClient("StartTransfer");
        String url = getURL("StartTransfer") ;
        String response = httpSocketConn.makeGETRequest(getURL("StartTransfer"));
        String xmlResponse = httpSocketConn.parseResponse(response)[1] ; //data
        printServer(xmlResponse); //data
        
        XmlDocument doc = XmlDocument.createXmlDocument(
            new java.io.StringBufferInputStream(trimNewLines(xmlResponse)), false  );

        Vector fileElements = CSoapXMLDocument.getChildElements(
            CSoapXMLDocument.getElement(doc, "FileNames")) ;
        String[] targetFileNames = new String[fileElements.size()] ;
        for(int i=0; i<fileElements.size() ; i++)
        {
            targetFileNames[i]
                = CSoapXMLDocument.getElementValue(
                    (Element)fileElements.elementAt(i)) ;
        }   //end of for
        targetUploadFileName = targetFileNames[0]  ;
        
     }  //end of StartTransfer()
    
    
     public void UploadFile(int fileNumber) throws Exception
     {
        /**********************************************************************
                               Upload a File (copying locally)
        **********************************************************************/
        printClient("upload(copying file)");
        FileInputStream fin = new FileInputStream(fileName);
        fileSize = fin.available();
        
        FileOutputStream foutTarget = new FileOutputStream(
            "e:\\temp\\" + targetUploadFileName + "_" + fileNumber);
        int b ;
        while( (b=fin.read()) != -1 ){
            foutTarget.write(b) ;
        }
        System.out.println("local file copying done.\n");
        //response = uploadFile(fileName, targetUploadFileName +  "_" + fileNumber);
        //printServer(httpSocketConn.parseResponse(response)[1]); //header
        
    }   //end of UploadFile()
    
    
    public void EndTransfer(int fileNumber) throws Exception
    {
        /**********************************************************************
                               EndTransfer
        **********************************************************************/
        printClient("EndTransfer" + "\n");
        Socket wh = new Socket(host, 80);
        DataOutputStream whps = new DataOutputStream(wh.getOutputStream());
        DataInputStream win = new DataInputStream(new BufferedInputStream(wh.getInputStream()));

        String data = "" ;
        String pc = "Cmd=EndTransfer&PD=USER_OID=" + userOid
            + "|AlbumID=" + albumOid + "|UploadId="
            + uploadID + "&TransferInfo=<?xml+version=\"1.0\" ?><EndTransfer><Version>1.0.0</Version><User>dev</User><Password></Password><UploadID>"+uploadID+"</UploadID><NumFiles>1</NumFiles><UploadFile><UploadName>"+(targetUploadFileName + "_" + fileNumber)+"</UploadName><UserFileName>"+(fileNumber + "_" + fileName)+"</UserFileName><FileSize>"+fileSize+"</FileSize></UploadFile></EndTransfer>";
        data += "POST /dragdropupload/AlbumID=" + albumOid + "/AlbumCaption=TestAlbum/UploadManager.psp HTTP/1.0\r\n" ;
        data += "User-Agent: PWComm\r\n" ;
        data += "Content-Type: application/x-www-form-urlencoded\r\n";
        data += "Content-Length: "+pc.length()+"\r\n";
        data += "Host: "+"delhi"+"\r\n";
        data += "Cookie: "+httpSocketConn.getSessionCookie()+"\r\n";
        data += "\r\n";
        data += pc;
        whps.writeBytes(data);
        whps.flush();
        String line = null ;
        String response = "" ;
        while ((line = win.readLine()) != null) {
            response += line + "\n";
        }   //end of while
        wh.close();
        
        printServer(httpSocketConn.parseResponse(response)[1]); //data
        
    }   //end of EndTransfer()
    
    
    public void EndUpload() throws Exception
    {
        /**********************************************************************
                               EndUpload
        **********************************************************************/
        printClient("EndUpload");
        String url = getURL("EndUpload") ;
        String response = httpSocketConn.makeGETRequest(getURL("EndUpload"));
        String xmlResponse = httpSocketConn.parseResponse(response)[1] ; //data
        printServer(xmlResponse); //data
    
    }   //end of EndUpload()


    private String getURL(String command)
    {
         return "/dragdropupload/AlbumID=" + albumOid
            + "/AlbumCaption=Test+Album/UploadManager.psp?"
            + "Cmd=" + command + "&NumImages=" + numOfImages
            + "&U=dev&P=&ID=" + uploadID + "&PD=USER_OID=" + userOid
            + "|AlbumID=" + albumOid + "|UploadId=" + uploadID ;
    
    }    //end of getURL(String)
    
    
    
    private static String trimNewLines(String str)
    {
        StringBuffer buffer = new StringBuffer() ;
        try{
            BufferedReader bR = new BufferedReader(new StringReader(str));
            String line = null ;
            //skip all empty lines
            while((line=bR.readLine()) != null){
                 if(line.trim().length() == 0){
                    continue ;
                 }else{
                    buffer.append(line);
                 }
            }    //end of while
        }catch(Exception e){
            e.printStackTrace() ;   
        }
        
        return buffer.toString();
        
    }   //end of trimNewLines(String)
    
    
    private String uploadFile(String file, String targetUploadFileName)
        throws Exception
    {
        Socket uh = new Socket(host, 80);
        DataOutputStream uhps = new DataOutputStream(uh.getOutputStream());
        DataInputStream uin = new DataInputStream(uh.getInputStream());

        byte buffer[] = new byte[4096];
        FileInputStream fin = new FileInputStream(file);
        int filelen = fin.available();
        String data = "" ;
        data += "PUT /" + targetUploadFileName + " HTTP/1.0\r\n" ;
        data += "Host: " + uploadHost + "\r\n";
        data += "Content-Length: " + filelen + "\r\n" ;
        data += "Authorization: Basic " + base64Encode("uploader:snapf1sh") + "\r\n";
        //System.out.println(base64Encode("uploader:snapf1sh"));
        data += "\r\n";
        int len;
        while ((len = fin.read(buffer)) > 0) {
            uhps.write(buffer, 0, len);
        }
        uhps.flush();
        fin.close();
        String response = "" ;
        String line = null ;
        while ((line = uin.readLine()) != null) {
            response += line ;
        }
        uh.close();
        return response ;
        
    }   //end of uploadFile(String, String)
    
    private static void printServer(String str)
    {
        System.out.println("SERVER>>>>>>>>>>>>>>>>\n" + str) ;
    }   //end of print(String)
    
    private static void printClient(String str)
    {
        System.out.println("CLIENT>>>>>>>>>>>>>>>>\n" + str) ;
    }   //end of print(String)
    
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
}   //end of class CUploadProblemFix.java



/*

<?xml version="1.0" ?>
<StartUploadResponse>
    <Version>1.0.0</Version>
    <UploadID>1037234911080</UploadID>
    <Status>
        <Code>0</Code>
    </Status>
    <ProtocolInfo>
        <UploadURL>ftp://delhi/ftproot/uploadroot</UploadURL>
        <Passive>No</Passive>
        <User>dev</User>
        <Password>th3b0x</Password>
        <SecondURL>http://</SecondURL>
        <SecondUser>uploader</SecondUser>
        <SecondPwd>snapf1sh</SecondPwd>
    </ProtocolInfo>
    <ImageInfo>
        <MaxPixelSize>3200, 3200</MaxPixelSize>
        <MinPixelSize>160, 120</MinPixelSize>
        <CanScale>MaxOnly</CanScale>
    </ImageInfo>
    <FormatInfo>
        <Formats>JPEG</Formats>
    </FormatInfo>
</StartUploadResponse>





<?xml version="1.0" ?>
<StartTransferResponse>
    <Version>1.0.0</Version>
    <UploadID>1037237626234</UploadID>
    <NumFiles>1</NumFiles>
    <Status>
        <Code>0</Code>
    </Status>
    <FileNames>
        <File>IMG_1037237626234_1_m9e5908e_f17f2211b2_m7ffe.3085785</File>
    </FileNames>
</StartTransferResponse>



<?xml version="1.0" ?>
<EndTransfer>
  <Version>1.0.0</Version>
  <User>dev</User>
  <Password></Password>
  <UploadID>1037307439861</UploadID>
  <NumFiles>1</NumFiles>
  <UploadFile>
    <UploadName>IMG_1037307439861_1_4621b6b6_f18456bc19_m7fff.3085785</UploadName>
    <UserFileName>aish.jpg</UserFileName>
    <FileSize>0</FileSize>
  </UploadFile>
</EndTransfer>
*/
