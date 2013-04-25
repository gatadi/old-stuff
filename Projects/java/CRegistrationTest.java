import java.text.DecimalFormat ;
import java.io.* ;
import java.util.* ;
import java.sql.Timestamp ;
import java.text.DateFormat ;
import com.snapfish.adservices.SFAdManager ;
import java.net.URLEncoder ;
import java.net.URLDecoder ;
import com.snapfish.core.servlet.* ;
import javax.servlet.http.* ;
import java.text.DateFormat ;
import com.snapfish.paymentservices.CMoney;
import com.snapfish.core.* ;
import com.snapfish.core.servlet.* ;
import com.snapfish.user.* ;
import com.snapfish.util.* ;
import com.snapfish.album.shared.* ;
import com.snapfish.bizobj.order.* ;
import com.snapfish.bizobj.order.servlets.* ;
import com.snapfish.util.*;
import com.snapfish.bizobj.order.ordermgr.IShoppingOrderManagerHome;
import com.snapfish.bizobj.order.ordermgr.IShoppingOrderManager;
import javax.naming.Context ;
import com.snapfish.products.* ;
import com.snapfish.database.* ;
import com.snapfish.dbobjects.* ;
import com.snapfish.coupons.* ;
import javax.naming.* ;
import com.snapfish.bizobj.promotion.ejb.* ;
import com.snapfish.bizobj.promotion.*;
import com.snapfish.user.ejb.* ;
import com.snapfish.user.shared.* ;
import com.snapfish.user.servlet.*;
import com.snapfish.exception.* ;


import java.sql.Driver ;
import java.sql.DriverManager ;


public class CRegistrationTest implements IParameterConstants
{
    private static String nth = "5" ;

    public static void main(String[] args) throws Exception
    {
        System.out.println("CRegistrationTest.main()");
        System.setProperty("PROPERTIES_DIRECTORY", "e:/apps/snapserver");

        Context ctx = getInitialContext();
        IUsrMgmtHome userHome = (IUsrMgmtHome) ctx.lookup(
               CSnapfishJNDI.getURL(CSnapfishJNDI.USER_MANAGER) );
        IUsrMgmtRemote userBean = userHome.create();
        /*
        CRegistrationValues regValues = getCCValues(new CRegistrationValues());
        CDataManager dm = new CDataManager(regValues.getDataTable()) ;
        regValues.setUserOid("2909271");

        boolean isCCInfoValid = CUserProfileValidator.isCCInfoValid(dm) ;
        System.out.println("basic-isCCInfoValid=" + isCCInfoValid);
        if( isCCInfoValid ) {
            isCCInfoValid = CUserProfileValidator.validateCCwithCSI(dm) ;
            System.out.println("CSI-isCCInfoValid=" + isCCInfoValid);
        }   //end of if

        //userBean.updateAccount(getCCValues(regValues)) ;
       */
       //fullRegistration(userBean) ;
       liteRegistration(userBean) ;


    }   //end of main()


    public static void liteRegistration(IUsrMgmtRemote userBean) throws Exception
    {
        userBean.register(getLiteRegValues()) ;

    }   //end of liteRegisration()

    public static void mediumRegistration(IUsrMgmtRemote userBean) throws Exception
    {
        userBean.register(getMediumRegValues()) ;

    }   //end of liteRegisration()

    public static void fullRegistration(IUsrMgmtRemote userBean) throws Exception
    {
        userBean.register(getFullRegValues()) ;

    }   //end of liteRegisration()


    public static CRegistrationValues getLiteRegValues()
    {
        CRegistrationValues regValues = new CRegistrationValues() ;

        regValues.setCobrandOid(new Long(1000));
        regValues.setDisplayCobrandOid(new Long(1000));
        regValues.setFirstName("vijay" + nth) ;
        regValues.setLastName("gatadi" + nth);
        regValues.setEmailAddress("vijay" + nth + "@vijay.com");
        regValues.setPassword("vijay" + nth);

        return regValues ;
    }   //end of getLiteRegValues()


    public static CRegistrationValues getMediumRegValues()
    {
        return getMailingValues( getLiteRegValues() );

    }   //end of getMediumRegValues()


    public static CRegistrationValues getFullRegValues()
    {
        return getCCValues(getMediumRegValues()) ;

    }   //END of getFullRegValues() ;

    public static CRegistrationValues getMailingValues(CRegistrationValues regValues)
    {
        regValues.setMailingStreetAddress1(regValues.getFirstName() + " street1");
        regValues.setMailingStreetAddress2(regValues.getFirstName() + " street2");
        regValues.setMailingCity(regValues.getFirstName() + " city") ;
        regValues.setMailingState("CA") ;
        regValues.setMailingZipCode("94538") ;

        return regValues ;

    }   //end of getMailingValues() ;


    public static CRegistrationValues getCCValues(CRegistrationValues regValues)
    {
        //regValues.setZipCode("94539") ;

        regValues.setCreditCardNumber("4293232323232323") ;
        regValues.setCreditCardType(VISA) ;
        regValues.setExpirationDate("2002","0") ;
        regValues.setCardHoldersName("Vijay Gatadi");

        return regValues ;
    }   //END of getCCValues() ;


    public static InitialContext getInitialContext()
        throws NamingException
    {
        Properties p = new Properties();

        p.put(Context.INITIAL_CONTEXT_FACTORY ,
              "weblogic.jndi.T3InitialContextFactory");
        p.put(Context.PROVIDER_URL, "t3://delhi:80");

        return new InitialContext(p);
    }

}  //End of CTest class
