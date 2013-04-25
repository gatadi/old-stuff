import java.rmi.*;

import net.jini.space.JavaSpace;
import net.jini.core.entry.Entry ;

import com.sun.jini.mahout.binder.RefHolder;
import com.sun.jini.mahout.Locator;
import com.sun.jini.outrigger.Finder;


public class CJiniTest
{
    public static void main(String[] args) throws java.lang.Exception
    {   
        String isRead = args[0] ;
        if( isRead == null ){
            isRead = "read" ;
        }
        
        try{
        
        System.out.println("CJiniTest.main()...");
        JavaSpace space = SpaceAccessor.getSpace() ;
        System.out.println(space);
        
        Entry entry = new DataObject() ;
        if(isRead.equalsIgnoreCase("read"))
        {
            Object o = space.read(entry, null, JavaSpace.NO_WAIT) ;
            System.out.println("Read value=" + o) ;
        }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}   //end of CJiniTest


class DataObject implements Entry
{
    public String value = "Value1" ;
}

class SpaceAccessor {
    public static JavaSpace getSpace(String name) {
        try {
            if (System.getSecurityManager() == null) {
                //System.setSecurityManager(new RMISecurityManager());
            }

            if (System.getProperty("com.sun.jini.use.registry")
                == null)
            {
                Locator locator =
                    new com.sun.jini.outrigger.DiscoveryLocator();
                Finder finder =
                    new com.sun.jini.outrigger.LookupFinder();
                return (JavaSpace)finder.find(locator, name);
            } else {
                RefHolder rh = (RefHolder)Naming.lookup(name);
                return (JavaSpace)rh.proxy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JavaSpace getSpace() {
        return getSpace("JavaSpaces");
    }

    public static JavaSpace getNotificationSpace() {
        return getSpace("NotificationSpace");
    }
}
