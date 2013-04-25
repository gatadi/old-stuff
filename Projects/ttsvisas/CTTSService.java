
import java.util.Vector ;
import java.io.PrintWriter ;
import java.util.Calendar ;
import java.io.File ;

public class CTTSService
{
    static Calendar _calendar = Calendar.getInstance();

    public static int getFirstAvailableIndex(String response)
    {
        if( response == null ){
            return -1 ;
        }   //end of if

        return response.indexOf("senddt('");

    }   //end of getFirstAvailableIndex(String)


    public static int getNextAvailableIndex(
        String response, int startIndex)
    {
        if( response == null && startIndex == -1){
            return -1 ;
        }   //end of if

        return response.indexOf("senddt('", startIndex );

    }   //end of getNextAvailableIndex(String, int, String)


    public static String getAvailableSlotFromIndex(
        String response,    int startIndex)
    {
        if( response == null && startIndex != -1 ){
            return null ;
        }

        String timeSlot = null ;
        int i1 = response.indexOf("senddt('", startIndex );
        int i2 = -1 ;

        if( i1 != -1 ){
            i2 = response.indexOf("')", i1);
        }
        if( i2 != -1 ){
            timeSlot = response.substring(i1+"senddt('".length(), i2) ;
        }

        return timeSlot ;
    }   //end of getAvailableSlotFromIndex(String, int)



    public static String getAvailableSlot(String response)
    {
        String timeSlot = null ;

        int i1 = response.indexOf("senddt('");
        int i2 = -1 ;
        if( i1 != -1 ){
            i2 = response.indexOf("')", i1);
        }
        if( i2 != -1 ){
            timeSlot = response.substring(i1+"senddt('".length(), i2) ;
        }

        return timeSlot ;

    }   //end of getAvailableSlot(String)



    public static Vector getAllAvailableSlots(String response)
    {
        Vector vTimeSlots = new Vector() ;
        int index = getFirstAvailableIndex(response);
        if( index == -1 ){
            return vTimeSlots ;
        }

        while( index != -1 )
        {
            //8/7/2003 12:00:00 AM
            String timeSlot = getAvailableSlotFromIndex(response, index) ;
            index = getNextAvailableIndex(response, index+1);

            timeSlot = timeSlot.substring(0, timeSlot.indexOf(" "));
            int i1 = timeSlot.indexOf("/") ;
            int i2 = timeSlot.indexOf("/", i1+1) ;
            int month = Integer.parseInt( timeSlot.substring(0, i1)) ;
            int day = Integer.parseInt(timeSlot.substring(i1+1, i2));
            int year = Integer.parseInt(timeSlot.substring(i2+1));
            Calendar cal = (Calendar)_calendar.clone();
            cal.set(Calendar.MONTH, month-1);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.YEAR, year);

            vTimeSlots.addElement(cal);
        }   //end of while

        return vTimeSlots ;

    }   //end of getAllAvailableSlots()



    public static String getValidAvailableSlot(
        String response, Calendar startTime, Calendar endTime)
    {
        Vector vTimeSlots = getAllAvailableSlots(response);

        for(int i=0; i<vTimeSlots.size(); i++)
        {
            Calendar availableTime = (Calendar) vTimeSlots.elementAt(i);

            if(availableTime.after(startTime) && availableTime.before(endTime))
            {
                return (1+availableTime.get(Calendar.MONTH))
                    + "/" + availableTime.get(Calendar.DAY_OF_MONTH)
                    + "/" + availableTime.get(Calendar.YEAR)
                    + " 12:00:00:AM" ;
            }
        }   //end of for

        return null ;
    }   //end of getValidAvailableSlot()




    public static String getTxtModifyHiddenValue(String response)
    {
        if( response == null ){
            return null ;
        }   //end of if
        String txtModify = null ;

        int i1 = response.indexOf("hidden") ;
        int i2 = -1 ;
        int i3 = -1 ;
        if( i1 != -1)
        {
            i2 = response.indexOf("value=\"", i1) ;
            if(i2 != -1 )
            {
                i3 = response.indexOf("\"", i2 + "value=\"".length());
                txtModify = response.substring(i2 + "value=\"".length(), i3 );
            }
        }
        return txtModify ;
    }   //end of getTxtModifyHiddenValue(String)



    /**************************************************************************
                            StartModify
    **************************************************************************/
    public static CHTTPSURLConnection StartModify() throws Exception
    {
        //https://www.ttsvisas.com/StartModify.aspx
        CHTTPSURLConnection httpsURLConn
            = new CHTTPSURLConnection(
                "https://www.ttsvisas.com/StartModify.aspx");
        httpsURLConn.doGET() ;

        return httpsURLConn;

    }   //end of StartModify()



    /**************************************************************************
                            checkModify
    **************************************************************************/
    public static CHTTPSURLConnection checkModify(
        CHTTPSURLConnection httpsURLConn,
        String txtDropboxNo, String txtEmail, String txtPassport) throws Exception
    {
        //https://www.ttsvisas.com/checkModify.aspx
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/checkModify.aspx",
            httpsURLConn.getHeader());

        httpsURLConn.doPOST(
            "txtDropboxNo=" + txtDropboxNo
            + "&txtEmail=" + txtEmail
            + "&txtPassport=" + txtPassport);

        return httpsURLConn ;

    }   //end of checkModify()



    /**************************************************************************
                            ModifyCalendar
    **************************************************************************/
    public static CHTTPSURLConnection ModifyCalendar(
        CHTTPSURLConnection httpsURLConn) throws Exception
    {
        //https://www.ttsvisas.com/ModifyCalendar.aspx
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/ModifyCalendar.aspx",
            httpsURLConn.getHeader());
        httpsURLConn.doGET();

        return httpsURLConn ;

    }   //end of ModifyCalendar()



    /**************************************************************************
                            ModifyTime
    **************************************************************************/
    public static CHTTPSURLConnection ModifyTime(
        CHTTPSURLConnection httpsURLConn, String timeSlot)  throws Exception
    {
        //https://www.ttsvisas.com/ModifyTime.aspx
        //optdate=8/21/2003 12:00:00 AM
        //String timeSlot ="8/22/2003 12:00:00 AM" ;
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/ModifyTime.aspx",
            httpsURLConn.getHeader());
        httpsURLConn.doPOST("optdate=" + timeSlot);

        return httpsURLConn ;
    }   //end of Modifytime()


    /**************************************************************************
                            SaveModifications
    **************************************************************************/
    public static CHTTPSURLConnection SaveModifications(
        CHTTPSURLConnection httpsURLConn)  throws Exception
    {
        //https://www.ttsvisas.com/SaveModifications.aspx
        //txtModify="Friday, 1 August 2003 (Time: 8:30 a.m - 8:45 a.m) to Thursday, 21 August 2003 (Time: 9:30 a.m - 9:45 a.m)">
        String response = httpsURLConn.getResponse();
        String txtModify = getTxtModifyHiddenValue(response);
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/SaveModifications.aspx",
            httpsURLConn.getHeader());
        //System.out.println("Scheduleding Appointment on=" + txtModify);
        httpsURLConn.doPOST("txtModify=" + txtModify);
        System.out.println("Appointment is Scheduled on=" + txtModify);

        return httpsURLConn ;

    }   //end of SaveModifications() ;


    public static void writeToFile(String response, String fileName) throws Exception
    {
        //Save Receipt
        String file = "e:/workarea/javaprojects/ttsvisas/" + fileName;
        java.io.PrintWriter pw = new java.io.PrintWriter(
            new java.io.FileOutputStream(file));
        pw.print(response);
        pw.flush();
        pw.close();

    }   //end of writeToFile(String, String)


    public static void deleteFiles()
    {
        File dir = new File("e:/workarea/javaprojects/ttsvisas/") ;
        String files[] = dir.list() ;
        String ModifyCalendar_FileName = "ModifyCalendar" ;
        String filesToDelete[] = new String[files.length];

        long currentTime = System.currentTimeMillis() ;
        int j=0 ;
        for(int i=0; i<files.length; i++)
        {
            String f = files[i] ;
            if( f.startsWith(ModifyCalendar_FileName) )
            {
                String timeAsString = f.substring(ModifyCalendar_FileName.length(),
                    f.indexOf("."));
                long time = Long.parseLong(timeAsString);
                if( Math.abs ( currentTime - time ) > 1000*60 ){
                    filesToDelete[j++] = f ;
                }
            }

        }   //end of for

        for(int i=0; i<j ; i++)
        {
            File f = new File( dir.getAbsolutePath()
                + File.separator +  filesToDelete[i] );
            f.delete();
        }
    }

    public static void modifyAppointment(
        String txtDropboxNo, String txtEmail, String txtPassport,
        Calendar startTime, Calendar endTime) throws Exception
    {
        //Time-Slot Format --> "8/22/2003 12:00:00 AM"
        String timeSlot = null ;

        CHTTPSURLConnection httpsURLConn = StartModify() ; //-->enter number & passport
        writeToFile(httpsURLConn.getResponse(), "a-appointment-details.html");

        httpsURLConn = checkModify(httpsURLConn, txtDropboxNo, txtEmail, txtPassport) ; //modify or cancel
        writeToFile(httpsURLConn.getResponse(), "b-modify-or-cancel.html");

        httpsURLConn = ModifyCalendar(httpsURLConn); // -->calendar page
        writeToFile(httpsURLConn.getResponse(), "c-calendar.html");

        timeSlot = getValidAvailableSlot(
            httpsURLConn.getResponse(), startTime, endTime);

        long t1 = System.currentTimeMillis() ;
        long i = 0;
        while( timeSlot == null )
        {
            System.out.println("Trying again[" + (i++) + "]") ;
            httpsURLConn = ModifyCalendar(httpsURLConn); // -->calendar page
            /*
            long t2 = System.currentTimeMillis() ;
            if( (t2-t1) > 1000*60*2 ) { //clean up files every 2 minutes
                deleteFiles();
                t1 = System.currentTimeMillis() ;
            }
            writeToFile(httpsURLConn.getResponse(), "d-calendar.html");
            */
            timeSlot = getValidAvailableSlot(
                httpsURLConn.getResponse(), startTime, endTime);
        }   //end of while

        writeToFile(httpsURLConn.getResponse(), "d-calendar.html");
        System.out.println("Available Time Slot= " + timeSlot);

        httpsURLConn = ModifyTime(httpsURLConn, timeSlot);//--confirm save
        writeToFile(httpsURLConn.getResponse(), "e-confirm.html");

        httpsURLConn = SaveModifications(httpsURLConn); //-->recipt
        //Save Receipt
        writeToFile(httpsURLConn.getResponse(), "f-receipt.html");

    }   //end of modifyAppointment()


    public static void modifyAppointment(
        String txtDropboxNo, String txtEmail, String txtPassport,
        String timeSlot) throws Exception
    {
        //Time-Slot Format --> "8/22/2003 12:00:00 AM"

        CHTTPSURLConnection httpsURLConn = StartModify() ; //-->enter number & passport
        httpsURLConn = checkModify(httpsURLConn, txtDropboxNo, txtEmail, txtPassport) ; //modify or cancel
        httpsURLConn = ModifyCalendar(httpsURLConn); // -->calendar page
        httpsURLConn = ModifyTime(httpsURLConn, timeSlot);//--confirm save
        httpsURLConn = SaveModifications(httpsURLConn); //-->recipt

    }   //end of modifyAppointment()


    public static void shashikala() throws Exception
    {
        //Sheshikala Dhondi F1  190620033754   E3158386    12 Aug  2003 at 9:00 a.m - 9:15
        String txtDropboxNo = "190620033754" ;
        String txtEmail = "vkgatadi@yahoo.com" ;
        String txtPassport= "E3158386" ;

        Calendar startTime = (Calendar)_calendar.clone();
        startTime.set(Calendar.MONTH, Calendar.AUGUST);
        startTime.set(Calendar.DAY_OF_MONTH, 13) ;  //after

        Calendar endTime = (Calendar)_calendar.clone();
        endTime.set(Calendar.MONTH, Calendar.AUGUST) ;
        endTime.set(Calendar.DAY_OF_MONTH, 15);//before

        modifyAppointment(txtDropboxNo, txtEmail,  txtPassport, startTime, endTime);
    }



    public static void sathya()  throws Exception
    {
        String txtDropboxNo = "250320041970" ;
        String txtEmail = "yarramsetty@hotmail.com" ;
        String txtPassport= "E7849189" ;

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 29) ;//after

        Calendar endTime = (Calendar)_calendar.clone();
        endTime.set(Calendar.DAY_OF_MONTH, 31);//before

        modifyAppointment(txtDropboxNo, txtEmail,  txtPassport, startTime, endTime);

    }   //end main()

    public static void main(String[] args)  throws Exception
    {
        CHTTPSURLConnection.disableCertificateValiadation();

        sathya() ;


    }   //end main()

}   //end of CTTSService

