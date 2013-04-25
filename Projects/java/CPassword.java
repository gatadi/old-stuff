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

import com.snapfish.util.CDecrypter;


public class CPassword
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("\nCTest.main() : " ) ;

        String USER     = "stg7_csr" ;
        String PASSWORD = "stg7" ;
        String SID      = "SFOSTG05" ;
        String DBHOST   = "localhost" ;
        String PORT     = "1535" ;
        String URL      = "jdbc:oracle:thin:@" + DBHOST + ":" + PORT + ":" + SID ;
        /*
        //production
        String USER     = "web01" ;
        String PASSWORD = "scxfpweb01" ;
        String SID      = "SFOPRD01" ;
        String DBHOST   = "localhost" ;
        String PORT     = "1541" ;
        String URL      = "jdbc:oracle:thin:@" + DBHOST + ":" + PORT + ":" + SID ;
        */
        
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver") ;
        }catch(ClassNotFoundException e){
            e.printStackTrace() ;
        }   //end of try/catch

        Properties p = new Properties();
        p.put("user", USER);
        p.put("password", PASSWORD);
        p.put("debug.ddb", "true");
        System.out.println("Trying to connecto to database...");
        Connection conn = DriverManager.getConnection(URL,p);
        //_conn = DriverManager.getConnection(url, user, password)  ;
        System.out.println("Connection is established.");
        



        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM REP") ;
        ResultSet rSet = pstmt.executeQuery() ;

        while(rSet.next())
        {
            String status = rSet.getString("STATUS_CODE").trim() ;
            if (status.equalsIgnoreCase("ACT"))
            {
                System.out.println( rSet.getString("NAME") + " = " + rSet.getString("STATUS_CODE")
                                + " = " + new CDecrypter().decrypt(rSet.getString("PSWD")) ) ;
            }
        }   //end of while

        rSet.close() ;
        pstmt.close() ;

        conn.close();
    }
}
