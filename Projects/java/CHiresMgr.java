import java.sql.DriverManager ;
import java.sql.Connection ;
import java.sql.Statement ;
import java.sql.PreparedStatement ;
import java.sql.ResultSet ;
import java.sql.SQLException ;
import java.sql.ResultSetMetaData ;
import java.util.Date;

import java.net.URL ;
import java.net.HttpURLConnection ;
import java.io.BufferedInputStream;
import java.io.File ;
import java.io.FileOutputStream ;

import java.util.Vector ;
import java.util.Properties ;

//import com.snapfish.ddb.util.CDBNode ;
//import com.snapfish.ddb.util.CDBNodeHints ;
//import com.snapfish.ddb.driver.CDriver ;
//import com.snapfish.ddb.util.CDBNodeHintsState;
//import com.snapfish.ddb.util.CNoCleanupHook;

public class CHiresMgr
{
    static String IMAGE_SERVER = "snapfish" ;
    static String USER     = "web01" ;
    static String PASSWORD = "scxfpweb01" ;
    static String SID      = "SFOPRD01" ;
    static String DBHOST   = "localhost" ;
    static String PORT     = "1521" ;
    static String URL      = "jdbc:oracle:thin:@" + DBHOST + ":" + PORT + ":" + SID ;

    //staging_image_erver = "alphapix.snapfish.com" ;
    //production_image_server = "images.snapfish.com" ;

    static Connection _conn = null ;

    static
    {
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver") ;
        }catch(ClassNotFoundException e){
            e.printStackTrace() ;
        }   //end of try/catch

    }   //end of static


    public static Connection getConnection() throws SQLException
    {
        if( _conn == null )
        {
            return getConnection(URL, USER, PASSWORD) ;
        }

        return _conn ;

    }   //end of getConnection()

    public static Connection getConnection(
        String url, String user, String password) throws SQLException
    {
        if( _conn == null)
        {
            //CDBNodeHints.getDBNodeHints().setDBNode(CDBNode.getAllDBNodes()[0]);

            Properties p = new Properties();
            p.put("user", user);
            p.put("password", password);
            p.put("debug.ddb", "true");
            System.out.println("Trying to connecto to database...");
            _conn = DriverManager.getConnection(url,p);
            //_conn = DriverManager.getConnection(url, user, password)  ;
            System.out.println("Connection is established.");
        }

        return _conn;

    }   //end of getConnection(String, String, String)


    //url -> http://snapfish/dm1//hr/testdata/Upload_full1//3243675Xpic1hr.jpg
    public static byte[] getFile(String url) throws Exception
    {
        URL _url = new URL(url) ;
        HttpURLConnection urlConnection = (HttpURLConnection)_url.openConnection() ;
        urlConnection.setFollowRedirects(false);
        urlConnection.setRequestMethod("GET");


        //System.out.println("ResponseCode=" + urlConnection.getResponseCode());
        //System.out.println("ResponseMessage=" + urlConnection.getResponseMessage());

        //String redirectURL = urlConnection.getHeaderField("Location") ;
        //System.out.println("RedirectURL=" + redirectURL);

        BufferedInputStream buffInpuStream = new BufferedInputStream(
            new BufferedInputStream( urlConnection.getInputStream() ) );


        //System.out.print("Downloadig file..." + urlConnection.getContentLength() + " bytes");
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
            buffInpuStream.close() ;
            throw new Exception("\nCould not download file \ncontent-length="
                + urlConnection.getContentLength()
                + "\ndownloaded byte-length=" + index ) ;
        }

        //System.out.println("\nFile download is complete.");
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


    public static void downloadAllAlbumsHiresFiles(
        String emailaddress, int albumACL) throws SQLException
    {
        Vector vAlbumOids = CHiresMgr.getAlbumOids(emailaddress, albumACL);
        for(int i=0; i<vAlbumOids.size(); i++)
        {
            downloadAlbumHiresFiles(vAlbumOids.elementAt(i).toString(),albumACL) ;
        }   //end of for

        CHiresMgr.getConnection().close() ;

    }   //end of downloadAllAlbumsHiresFiles(String, int, int )


    public static void downloadAlbumHiresFiles(
        String albumOid, int albumACL) throws SQLException
    {
        Vector vPictOids = CHiresMgr.getPictOids( albumOid ) ;
        for(int j=0; j<vPictOids.size(); j++)
        {
            String pictOid = vPictOids.elementAt(j).toString() ;
            String filepath = CHiresMgr.getHiresFilePath(pictOid); ;
            String line = albumOid + "_" + pictOid + "=" + filepath;
            try
            {
                System.out.println("http://" + IMAGE_SERVER + filepath);
                byte[] data = CHiresMgr.getFile("http://" + IMAGE_SERVER +   filepath) ;
                CHiresMgr.saveFile(data, albumOid, pictOid + ".jpg") ;
            }catch(Exception e)
            {
                System.out.println("Couldnot download the file:" + line);
            }
        }



    }   //end of downloadAlbumHiresFiles(String, int, int)


    public static Vector getAlbumOids(String emailAddress, int accessLevel)
        throws SQLException
    {
        Vector vAlbumOids = new Vector() ;
        PreparedStatement pstmt = getConnection().prepareStatement(GET_ALBUM_OIDS) ;
        pstmt.setString(1, emailAddress) ;
        ResultSet rSet = pstmt.executeQuery() ;

        while(rSet.next()){
            vAlbumOids.add(rSet.getString("ALBUM_OID") ) ;
        }   //end of while

        rSet.close() ;
        pstmt.close() ;

        return vAlbumOids ;
    }   //end of getAlbumOids(String, int)


    public static Vector getPictOids(String albumOid) throws SQLException
    {
        Vector vPictOids = new Vector() ;
        PreparedStatement pstmt = getConnection().prepareStatement(GET_PICT_OIDS) ;
        pstmt.setLong(1, Long.parseLong(albumOid)) ;
        ResultSet rSet = pstmt.executeQuery() ;

        while(rSet.next()){
            vPictOids.add(rSet.getString("PICTURE_OID") ) ;
        }   //end of while

        rSet.close() ;
        pstmt.close() ;

        return vPictOids ;
    }   //end of getPictOids()


    public static String getHiresFilePath(String pictOid) throws SQLException
    {
        PreparedStatement pstmt = getConnection().prepareStatement(GET_FILE_PATH_OBJECT) ;
        pstmt.setLong(1, Long.parseLong(pictOid)) ;
        ResultSet rSet = pstmt.executeQuery() ;

        rSet.next() ;

        String hireFileURL = "/" + rSet.getString("MOUNT_POINT")
            + "/" + rSet.getString("HR_DIRECTORY_PATH")
            + "/" + rSet.getString("HR_IMAGE_ID");

        rSet.close() ;
        pstmt.close() ;

        return hireFileURL ;
    }   //end of getHiresFilePath(String)


    public static String encode(String s)
    {
        final char SLASH = '/';
        final char SEMICOLON = ';';

        // Replace all "/" with ";"
        s = s.replace(SLASH, SEMICOLON);

        // Prepend timestamp + "|" before encoded string
        Date date = new Date();

        long dateValue = date.getTime();

        dateValue /= 10;
        dateValue *= 10;

        s = String.valueOf(dateValue) + "|" + s;

        // Encode the string
        StringBuffer sb = new StringBuffer(s);
        for(int i=0; i<sb.length(); i++)
        {
            char c = sb.charAt(i);
            int value = (int)c;
            value++;
            value++;
            if((i % 2 > 0))
            value++;
            sb.setCharAt(i, (char)value);
        }

        String out = sb.toString();
        out = java.net.URLEncoder.encode(out);

        return out;
    }


    public static String decode(String s)
    {
         try{
            s = java.net.URLDecoder.decode(s) ;
         }catch(Exception e){
            e.printStackTrace();
         }

         StringBuffer sb = new StringBuffer(s) ;

         for(int i=0; i<s.length(); i++)
         {
            char c = s.charAt(i) ;
            int value = (int) c ;
            if( i%2 > 0 ){
                value-- ;
            }
            value--; value-- ;
            c = (char) value ;
            sb.setCharAt(i,c);
         }

         s = sb.toString() ;
         System.out.println(s);
         int index = s.indexOf("|") ;
         if( index != -1){
            s = s.substring(1+index) ;
         }
         s = s.replace(';', '/');

         return s ;
    }


    public static final String GET_ALBUM_OIDS
        =  "select ALBUM_ACL.ALBUM_OID from ALBUM_ACL where "
           + "ALBUM_ACL.ACCOUNT_OID = ( select ACCT.OID from ACCT "
           + "where ACCT.COBRAND_ACCOUNT_ID = ? "
           + "and ACCT.SUBSCRIBER_OID = 1000000 "
           + "and ( ALBUM_ACL.ACCESS_LEVEL_OID = 1001 ) ) "
           + "and ALBUM_ACL.DISPLAY_ORDER > -1" ;

    public static final String GET_PICT_OIDS = "select oid as PICTURE_OID from pict p where  p.ALBUM_OID = ?";


    public static final String GET_FILE_PATH_OBJECT
        = "select s.MOUNT_POINT, p.HR_DIRECTORY_PATH, p.HR_IMAGE_ID from pict p, storage_cell s where p.HR_STORAGE_CELL_OID = s.oid and p.OID = ?"  ;



    public static void main(String[] args) throws SQLException
    {

        //production
        USER     = "DEV01" ;
        PASSWORD = "GOLDJAWS" ;
        SID      = "SFOPRD01" ;
        DBHOST   = "localhost" ;
        PORT     = "1501" ;
        URL      = "jdbc:oracle:thin:@" + DBHOST + ":" + PORT + ":" + SID ;
        IMAGE_SERVER ="images1.snapfish.com" ;
        String albumId [] = {"48433977", "48683817"};

        //CHiresMgr.downloadAlbumHiresFiles("4578271", CHiresMgr.STAGING, 1000 );
        try
        {
            for (int i = 0 ; i < albumId.length; i++)
            {
                CHiresMgr.downloadAlbumHiresFiles(albumId[i], 1000 );
            }
        }
        finally
        {
            CHiresMgr.getConnection().close() ;
        }
//        CHiresMgr.downloadAllAlbumsHiresFiles("samba@jessops.com", 1007);


    }   //end main(String[])




}   //end of CHiresMgr

