
import java.util.Date ;
import java.util.GregorianCalendar ;
import java.util.Calendar ;

public class CSmokeTest
{
    public static void main(String[] args) throws Exception
    {

        Calendar cal = GregorianCalendar.getInstance() ;
        int hour = cal.get(GregorianCalendar.HOUR_OF_DAY) ;
        int minutes = cal.get(GregorianCalendar.MINUTE)  ;
        while( true )
        {
            if(  (hour == 8 && minutes > 30) )
            {
                System.out.println(hour + ":" + minutes);
                Runtime.getRuntime().exec("e:\\apps\\perl\\bin\\perl e:\\workarea\\bin\\smoketest.pl");
                break ;
            }else
            {
                Thread.sleep(10*60*1000); //every 10 minutes
                cal = GregorianCalendar.getInstance() ;
                hour = cal.get(GregorianCalendar.HOUR_OF_DAY) ;
                minutes = cal.get(GregorianCalendar.MINUTE)  ;
                System.out.println(hour + ":" + minutes);
            }
        }


    }   //end of main()


}  //End of CSmokeTest class
