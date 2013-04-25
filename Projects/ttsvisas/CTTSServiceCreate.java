
import java.util.Vector ;
import java.io.PrintWriter ;
import java.util.Calendar ;
import java.io.File ;
import java.io.FileInputStream ;
import java.util.Properties ;
import java.util.Enumeration ;

public class CTTSServiceCreate
{

    static String RESPONSE_FILES_DIR
        = "e:/workarea/javaprojects/ttsvisasnewappointments/" ;
    static int appontMentCount = 1 ;



    public static void main(String[] args) throws Exception
    {

        CHTTPSURLConnection.disableCertificateValiadation();

        String zone = "Zone=1" ;  //1:chennai 2:delhi 3:kolkatta
        String visaType = "Visa_Type=16" ; //F1=16, H1B=23, B1/B2=64
        String optVisit = "optVisit=Y"; //has your visa rejected in last 12 months

        Calendar _calendar = Calendar.getInstance();

        Calendar startTime = (Calendar)_calendar.clone() ;
        startTime.add(Calendar.YEAR, 1) ;
        startTime.set(Calendar.MONTH, Calendar.JANUARY) ;
        startTime.set(Calendar.DAY_OF_MONTH, 9) ;
        //startTime.add(Calendar.DAY_OF_MONTH, 1) ;

        Calendar endTime = (Calendar)_calendar.clone();
        endTime.add(Calendar.YEAR, 1) ;
        endTime.set(Calendar.MONTH, Calendar.JANUARY) ;
        endTime.set(Calendar.DAY_OF_MONTH, 15);

        boolean gotOppointment = false ;
        while(!gotOppointment)
        {
            try{
                createNewAppointment(zone, visaType, optVisit, startTime, endTime);
                gotOppointment = true ;
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }   //end of while



    }   //end main()


    public static void createNewAppointment(
        String zone, String visaType, String optVisit,
        Calendar startTime, Calendar endTime) throws Exception
    {
        CHTTPSURLConnection httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/ChooseZone.aspx?type=A");
        httpsURLConn.doGET();
        writeToFile(httpsURLConn.getResponse(), "a-main.html");

        httpsURLConn = webApplication(httpsURLConn);
        writeToFile(httpsURLConn.getResponse(), "b-choose-zone.html");

        httpsURLConn = saveZone(httpsURLConn, zone);
        writeToFile(httpsURLConn.getResponse(), "c-choose-visa-type.html");

        httpsURLConn = saveVisaType(httpsURLConn, visaType);
        writeToFile(httpsURLConn.getResponse(), "d-is-visa-rejected.html");

        httpsURLConn = SavePersonalInterview(httpsURLConn, optVisit);
        writeToFile(httpsURLConn.getResponse(), "e-shipping-address.html");

        httpsURLConn = SaveShippingAddress(httpsURLConn);
        writeToFile(httpsURLConn.getResponse(), "f-passport-address.html");

        httpsURLConn = SavePassport(httpsURLConn);
        writeToFile(httpsURLConn.getResponse(), "g-contact-details.html");

        httpsURLConn = SaveLocalContact(httpsURLConn);
        writeToFile(httpsURLConn.getResponse(), (appontMentCount++)+"h-calendar.html");

        String timeSlot = getValidTimeSlot(httpsURLConn, startTime, endTime);

        httpsURLConn = ChooseDate(httpsURLConn, timeSlot);
        writeToFile(httpsURLConn.getResponse(), "i-confirm.html");

        httpsURLConn = SaveConfirm(httpsURLConn);
        writeToFile(httpsURLConn.getResponse(), "j-receipt.html");

    }   //end of createNewAppointment()


    public static String getValidTimeSlot(
        CHTTPSURLConnection httpsURLConn,
        Calendar startTime, Calendar endTime)  throws Exception
    {
        String timeSlot = CTTSService.getValidAvailableSlot(
            httpsURLConn.getResponse(), startTime, endTime);

        long i=0;
        while( timeSlot == null )
        {
            System.out.println("Trying again ["+ (i++) + "]");
            httpsURLConn = SaveLocalContact(httpsURLConn);
            writeToFile(httpsURLConn.getResponse(), "h-calendar.html");
            timeSlot = CTTSService.getValidAvailableSlot(
                httpsURLConn.getResponse(), startTime, endTime);
        }   //end of while

        System.out.println("Available Time Slot= " + timeSlot);
        return timeSlot ;

    }   //end of getValidTimeSlot()


    /**************************************************************************
                      webApplication()
    **************************************************************************/
    public static CHTTPSURLConnection webApplication(
        CHTTPSURLConnection httpsURLConn) throws Exception
    {
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/ChooseZone.aspx?type=A",
            httpsURLConn.getHeader() );
        httpsURLConn.doGET();

        return httpsURLConn ;
    }   //end of webApplication()


    /**************************************************************************
                      saveZone()
    **************************************************************************/
    public static CHTTPSURLConnection saveZone(
        CHTTPSURLConnection httpsURLConn, String zone) throws Exception
    {
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/SaveZone.aspx",
            httpsURLConn.getHeader() );
        httpsURLConn.doPOST(zone);
        return httpsURLConn ;
    }   //end of saveZone()


    /**************************************************************************
                      saveVisaType()
    **************************************************************************/
    public static CHTTPSURLConnection saveVisaType(
        CHTTPSURLConnection httpsURLConn, String visaType) throws Exception
    {
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/SaveVisa.aspx",
            httpsURLConn.getHeader() );
        httpsURLConn.doPOST(visaType);
        return httpsURLConn ;
    }   //end of saveVisaType()


    /**************************************************************************
                      SavePersonalInterview()
    **************************************************************************/
    public static CHTTPSURLConnection SavePersonalInterview(
        CHTTPSURLConnection httpsURLConn, String optVisit) throws Exception
    {
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/SavePersonalInterview.aspx",
            httpsURLConn.getHeader() );
        httpsURLConn.doPOST(optVisit);
        return httpsURLConn ;
    }   //end of SavePersonalInterview()


    /**************************************************************************
                      SaveShippingAddress()
    **************************************************************************/
    public static CHTTPSURLConnection SaveShippingAddress(
        CHTTPSURLConnection httpsURLConn) throws Exception
    {
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/SaveConsignee.aspx?PptPos= ",
            httpsURLConn.getHeader() );
        String file = RESPONSE_FILES_DIR + "shippinginfo/" + appontMentCount +".txt" ;
        httpsURLConn.doPOST(readFromFile(file));
        return httpsURLConn ;

    }   //end of SaveShippingAddress()


    /**************************************************************************
                      SavePassport()
    **************************************************************************/
    public static CHTTPSURLConnection SavePassport(
        CHTTPSURLConnection httpsURLConn) throws Exception
    {
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/SavePassport.aspx?oper=finish&PptPos=1",
            httpsURLConn.getHeader() );
        String file = RESPONSE_FILES_DIR + "passport/" + appontMentCount +".txt" ;
        httpsURLConn.doPOST(readFromFile(file));
        return httpsURLConn ;

    }   //end of SavePassport()



    /**************************************************************************
                      SaveLocalContact()
    **************************************************************************/
    public static CHTTPSURLConnection SaveLocalContact(
        CHTTPSURLConnection httpsURLConn) throws Exception
    {
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/SaveContact.aspx?strvar=finish&PptPos=1",
            httpsURLConn.getHeader() );
        //String file = RESPONSE_FILES_DIR + "localcontact/" + appontMentCount +".txt" ;
        //readFromFile(file);
        httpsURLConn.doPOST("__VIEWSTATE=dDwxNDA3MzUzODE2Ozs%2B&txtOccupation=none&txtEmpName=none&txtcont_phone3=none&txtcont_phone2=none&txtcont_usname=none&txtcont_phone1=none&txtcont_usphone3=none&txtcont_usphone2=none&txtcont_usphone1=none&txtcont_email=none&");
        return httpsURLConn ;

    }   //end of SaveLocalContact()


    /**************************************************************************
                      ChooseDate()
    **************************************************************************/
    public static CHTTPSURLConnection ChooseDate(
        CHTTPSURLConnection httpsURLConn, String timeSlot) throws Exception
    {
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/Timeslot.aspx",
            httpsURLConn.getHeader() );
        httpsURLConn.doPOST("optdate=" + timeSlot);
        return httpsURLConn ;

    }   //end of ChooseDate()



    /**************************************************************************
                      ChooseTime()  only for DELHI
    **************************************************************************/
    public static CHTTPSURLConnection ChooseTime(
        CHTTPSURLConnection httpsURLConn, String time) throws Exception
    {
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/Confirm.aspx",
            httpsURLConn.getHeader() );
        httpsURLConn.doPOST("optTime=" + time);
        return httpsURLConn ;

    }   //end of ChooseTime()


    /**************************************************************************
                      SaveConfirm()
    **************************************************************************/
    public static CHTTPSURLConnection SaveConfirm(
        CHTTPSURLConnection httpsURLConn) throws Exception
    {
        httpsURLConn = new CHTTPSURLConnection(
            "https://www.ttsvisas.com/saveconfirm.aspx",
            httpsURLConn.getHeader() );
        httpsURLConn.doPOST("");
        return httpsURLConn ;

    }   //end of SaveConfirm()







    public static String readFromFile(String file)  throws Exception
    {
        Properties prop = new Properties();
        prop.load(new FileInputStream(file));
        String data = "" ;

        for(Enumeration e = prop.keys();    e.hasMoreElements(); )
        {
            String key = e.nextElement().toString();
            String value = prop.getProperty(key) ;
            data += key + "=" + value + "&" ;
        }

        prop.clear();
        return data ;
    }   //end of readFromFile(String)


    public static void writeToFile(String response, String fileName) throws Exception
    {
        //Save Receipt
        String file = RESPONSE_FILES_DIR + fileName ;
        File _file = new File(file);
        java.io.PrintWriter pw = new java.io.PrintWriter(
            new java.io.FileOutputStream(_file));
        _file.setLastModified(System.currentTimeMillis());
        pw.print(response);
        pw.flush();
        pw.close();

    }   //end of writeToFile(String, String)





}   //end of CTTSServiceCreate

