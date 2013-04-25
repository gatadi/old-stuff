import java.sql.DriverManager ;
import java.sql.Connection ;
import java.sql.Statement ;
import java.sql.PreparedStatement ;
import java.sql.ResultSet ;
import java.sql.SQLException ;
import java.sql.ResultSetMetaData ;
import java.util.Date;


import java.util.Vector ;

public class CJDBCObject
{

    //if you want to connect via weblogic
    //public static String DRIVER = "weblogic.jdbc.jts.Driver" ;
    //public static String URL = "jdbc:weblogic:jts:oraclePool" ;

    public static String DRIVER = "oracle.jdbc.driver.OracleDriver" ;
    public static String URL = "jdbc:oracle:thin:@milano:1521:SFODEV01" ;
    public static String USER = "GATADI_WEB01" ; //acct_oid=500741
    public static String PASSWORD = "GATADI" ;

    private static Connection conn = null ;
    static
    {
        try
        {
            //URL = "jdbc:oracle:thin:@hawks:1521:SFOPRD01" ;
            //USER = "DEV01" ;
            //PASSWORD = "javafish" ;

            //URL = "jdbc:oracle:thin:@bigdb:1521:SFOSTG02" ;
            //USER = "WEB01" ;
            //PASSWORD = "WEB01STG02" ;

            Class.forName(DRIVER) ;


        }catch(ClassNotFoundException e)
        {
            e.printStackTrace() ;
        }   //end of try/catch

    }   //end of static

    public static Connection getConnection() throws SQLException
    {
        if( conn == null ){
            System.out.println("Trying to connecto to database...");
            conn = DriverManager.getConnection(URL, USER, PASSWORD)  ;
            System.out.println("Connection is established.");
        }
        return conn  ;
    }   //end of getConnection()


    public void test() throws Exception
    {
        Connection con = this.getConnection() ;
        System.out.println("Got connection.");
        Statement stmt = con.createStatement() ;
        String query
            = "SELECT t.TABLE_NAME FROM ALL_TAB_PRIVS_RECD t, ALL_TAB_COMMENTS c"
              + " WHERE t.OWNER='GATADI_APP' AND t.PRIVILEGE='SELECT'"
              + " AND t.TABLE_NAME=c.TABLE_NAME AND c.OWNER=t.OWNER" ;
        ResultSet rSet = stmt.executeQuery(query) ;
        Vector vTableNames = new Vector() ;
        while(rSet.next()){
            System.out.println( rSet.getString(1) );
        }   //end of while()
        rSet.close() ;
        con.close() ;
    }   //en dof test()


    public void generateObject() throws Exception
    {
        Connection con = this.getConnection() ;
        System.out.println("Got connection.");
        Statement stmt = con.createStatement() ;
        String query = "select t.column_name,t.data_type,t.data_scale,t.nullable,c.comments,t.data_length,t.data_precision from all_tab_columns t,all_col_comments c where t.owner='GATADI_APP' and t.table_name='ACCESS_LEVEL' and c.owner=t.owner and c.table_name=t.table_name and t.column_name=c.column_name" ;
        ResultSet rSet = stmt.executeQuery(query) ;
        ResultSetMetaData rsMetaData = rSet.getMetaData() ;
        int columnCount = rsMetaData.getColumnCount() ;

        CDBObject dbOjbect = new CDBObject("ACCESS_LEVEL");

        Vector vTableNames = new Vector() ;
        while(rSet.next())
        {
            for(int i=1; i<=columnCount ; i++)
            {
                String s = rSet.getString(i) ;
                if( s!= null)
                    System.out.print( s + "  " );
            }

            System.out.println();
            //dbOjbect.addColumn() ;
        }   //end of while()
        rSet.close() ;
        con.close() ;
    }

    public static void main(String args[]) throws Exception
    {
        System.out.println("Getting connection...");

        //CJDBCObject jdbcObj = new CJDBCObject() ;
        //jdbcObj.test() ;
        //jdbcObj.generateObject() ;
        Vector vAlbumOids = CHiresMgr.getAlbumOids("vijay@snapfish.com", 1001);
        for(int i=0; i<vAlbumOids.size(); i++)
        {
            String albumOid = vAlbumOids.elementAt(i).toString() ;
            Vector vPictOids = CHiresMgr.getPictOids( albumOid ) ;
            System.out.println("albumOid=" + albumOid) ;
            System.out.println("pictOids") ;
            //for(int j=0; j<vPictOids.size(); j++)
            {        int j=0 ;
                System.out.println("  " + vPictOids.elementAt(j));
                CHiresMgr.getFilePathObject(vPictOids.elementAt(j).toString());
            }
            break ;
        }
        CJDBCObject.getConnection().close() ;

    }   //end of main()

}   //en dof CJDBCObject


class CDBObject
{
    private String _name ;
    private Vector _columns ;

    CDBObject (String name)
    {
        this._name = name ;
        this._columns = new Vector() ;
    }

    public void addColumn(CColumn column)
    {
        this._columns.add(column) ;
    }
}

class CColumn
{
    public String _name ;
    public String _type ;

    CColumn(String name, String type)
    {
        this._name = name ;
        this._type = type ;
    }
}




