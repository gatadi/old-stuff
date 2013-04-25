import java.net.URL ;
import java.net.URI ;
import java.io.* ;
//import java.util.* ;


import com.snapfish.rendering.handlers.CGlobalXMLParser ;
import com.snapfish.rendering.handlers.CSlimlineHandler;
import com.snapfish.rendering.handlers.CRepositoryReader ;
import com.snapfish.util.CPropertiesCache;
import com.snapfish.image.file.CFileSystemProperties;
import com.snapfish.image.file.CFilePathObject ;
import com.snapfish.util.price.CPriceManager ;
import com.snapfish.util.CResourceHandler ;
import com.snapfish.util.CXMLCache ;
import com.snapfish.file.CFile ;
//import com.snapfish.core.servlet.* ;
import com.snapfish.render.templates.CTemplateValidator ;

import com.snapfish.auxapps.csr.core.servlet.*  ;

import com.snapfish.core.ISubscriberConstants;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
//import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream ;
import java.io.FileInputStream ;

import javax.swing.text.html.HTMLEditorKit ;
import javax.swing.text.html.HTMLDocument ;
import javax.swing.text.html.HTMLDocument.HTMLReader ;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag ;
import javax.swing.text.html.HTMLDocument.Iterator ;
import javax.swing.text.html.HTMLDocument ;
import javax.swing.text.AttributeSet ;
import javax.swing.text.Element ;
import javax.swing.text.MutableAttributeSet ;

import java.util.Dictionary ;
import java.util.Enumeration ;
import java.util.Vector ;

public class CNewTTSVisas
{


    public static void main(String[] args) throws Exception
    {
        //System.out.println("\nCTest.main()");

        System.getProperties().put( "http.proxySet", "true" );
        System.getProperties().put( "http.proxyHost", "web-proxy" );
        System.getProperties().put( "http.proxyPort", "8088" );

        System.getProperties().put( "socksProxyHost", "socks-server" );
        System.getProperties().put( "socksProxyPort", "1080" );

        //startSession();

        ttsvisasCheckAptmt("12", "2005");
    }

    public static void startSession() throws Exception
    {
        String cookie = getCookie() ;
        String[] resp = getCodeAndStatus(cookie) ;
        System.out.println("Cookie=" + cookie);
        System.out.println("viewStatus=" + resp[0]);
        System.out.println("codeURL=" + resp[1]);
    }

    public static void ttsvisasCheckAptmt(String month, String year) throws Exception
    {
        String code = "x74d2h" ;
        String cookie = "ASP.NET_SessionId=cw2wh555fcecng45v102tiu1";
        String viewStatus = "dDwtMTAyNjIwMjc0Mzt0PDtsPGk8MT47PjtsPHQ8O2w8aTwxPjtpPDU+Oz47bDx0PDtsPGk8MD47PjtsPHQ8O2w8aTwwPjs+O2w8dDw7bDxpPDA+O2k8MT47PjtsPHQ8O2w8aTwxPjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxDaG9vc2UgWm9uZSxWaXNhLExhbmd1YWdlLE1vbnRoICYgWWVhcjs+Pjs+Ozs+Oz4+O3Q8O2w8aTwxPjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxUVCBTZXJ2aWNlcyAtIFdlYiBBcHBvaW50bWVudDs+Pjs+Ozs+Oz4+Oz4+Oz4+Oz4+O3Q8O2w8aTwwPjtpPDE+O2k8Mj47aTwzPjtpPDQ+Oz47bDx0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPERlc2NyaXB0aW9uO0NvbnNab25lOz4+Oz47dDxpPDM+O0A8Q2hlbm5haTtEZWxoaTtDYWxjdXR0YTs+O0A8MXxTb3V0aDsyfE5vcnRoOzN8RWFzdDs+Pjs+Ozs+Oz4+Oz4+O3Q8O2w8aTwxPjs+O2w8dDw7bDxpPDA+Oz47bDx0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs+O2w8VmlzYVR5cGU7VmlzYVR5cGVJZDs+Pjs+O3Q8aTw0Nj47QDxBMTtBMjtBMztCMS9CMi1WSVNJVE9SIEZPUiBCVVNJTkVTUztCMS9CMi1WSVNJVE9SIEZPUiBQTEVBU1VSRTtDMTtDMS9EO0MyO0MzO0Q7RjE7RjI7RzE7RzI7RzM7RzQ7RzU7SDFCO0gxQztIMkE7SDJCO0gzO0g0O0k7SjE7SjI7TDEtSU5UUkFDT01QQU5ZIFRSQU5TRkVSRUUgKEJMQU5LRVQgUEVUSVRJT04pO0wxLUlOVFJBQ09NUEFOWSBUUkFOU0ZFUkVFIChJTkRJVklEVUFMIFBFVElUSU9OKTtMMjtNMTtNMjtPMTtPMjtPMztQMTtQMjtQMztQNDtRMTtSMTtSMjtUMjtUMztUNDtURDtUTjs+O0A8MTsyOzM7NTs2NDs4Ozk7MTA7MTE7MTI7MTY7MTc7MTg7MTk7MjA7MjE7MjI7MjM7MjQ7MjU7MjY7Mjc7Mjg7Mjk7MzA7MzE7NjU7MzQ7MzU7MzY7Mzc7NDc7NDg7NDk7NTA7NTE7NTI7NTM7NTQ7NTc7NTg7NjY7Njc7Njg7NjI7NjM7Pj47Pjs7Pjs+Pjs+Pjt0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPExhbmd1YWdlRGVzYztMYW5ndWFnZV9JZDs+Pjs+O3Q8aTwxOD47QDwtLS1TZWxlY3QtLS07RW5nbGlzaDtGcmVuY2g7R2VybWFuO0hpbmRpO0phcGFuZXNlO0thbm5hZGE7S29ua2FuaTtNYWxheWFsYW07TWFuZGFyaW4gQ2hpbmVzZTtNZWl0ZWlsb24gKE1hbmlwdXJpKTtTcGFuaXNoO1RhbWlsO1RlbHVndTtUaWJldGFuIC0gUGxlYXNlIGJyaW5nIGEgdHJhbnNsYXRvciB3aXRoIHlvdTtUdWx1O1VyZHU7T1RIRVIgLSBQbGVhc2UgYnJpbmcgYSB0cmFuc2xhdG9yIHdpdGggeW91Oz47QDwwOzE7MTI7MTM7MzsxNDs0Ozk7NTsxNTsxMTsxNjsyOzY7MTc7MTA7Nzs4Oz4+Oz47Oz47Pj47Pj47dDw7bDxpPDE+Oz47bDx0PDtsPGk8MD47aTwyPjs+O2w8dDx0PDt0PGk8MTI+O0A8SmFudWFyeTtGZWJydWFyeTtNYXJjaDtBcHJpbDtNYXk7SnVuZTtKdWx5O0F1Z3VzdDtTZXB0ZW1iZXI7T2N0b2JlcjtOb3ZlbWJlcjtEZWNlbWJlcjs+O0A8MTsyOzM7NDs1OzY7Nzs4Ozk7MTA7MTE7MTI7Pj47Pjs7Pjt0PHQ8O3A8bDxpPDA+O2k8MT47PjtsPHA8MjAwNTsyMDA1PjtwPDIwMDY7MjAwNj47Pj47Pjs7Pjs+Pjs+Pjt0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxYNzREMkg7Pj47Pjs7Pjs+Pjs+Pjs+Pjs+Pjs+Pjs+ojQ7Qz48bxc/kPFguLyDe/pkEzg=";

        checkAptmnt(viewStatus, code, cookie, month, year);
        checkAptmnt(viewStatus, code, cookie, month, year);


    }   //end of main()


    public static String getCookie() throws Exception
    {
        //System.out.println("***** initialize() ****");
        String url = "http://www.ttsvisas.com/ChkApptmntAvb1.aspx" ;
        String file = "e:/workarea/JavaProjects/JavaTest/ttsvisas/chkapptmntavb.htm";

        CHTTPURLConnection httpConn = new CHTTPURLConnection(url, file);
        httpConn.doGET() ;
        Vector cookies = httpConn.getCookies() ;

        java.io.BufferedReader reader = new java.io.BufferedReader(
            new java.io.FileReader(file));

        HTMLEditorKit.Parser parser = new HTMLParse().getParser() ;
        HTMLParserCallback parserCallBack = new HTMLParserCallback();
        parser.parse(reader, parserCallBack, false) ;

        return cookies.elementAt(0).toString() ;
    }

    public static String[] getCodeAndStatus(String cookie)  throws Exception
    {
        //System.out.println("\ngetCodeAndStatus()\n");
        String url = "http://www.ttsvisas.com/ChkApptmntAvb1.aspx" ;
        String file = "e:/workarea/JavaProjects/JavaTest/ttsvisas/chkapptmntavb.htm";

        Vector cookies = new Vector() ;
        cookies.add(cookie);

        java.io.BufferedReader reader = new java.io.BufferedReader(
            new java.io.FileReader(file));

        HTMLEditorKit.Parser parser = new HTMLParse().getParser() ;
        HTMLParserCallback parserCallBack = new HTMLParserCallback();
        parser.parse(reader, parserCallBack, false) ;

        return new String[]{parserCallBack.viewState,
            "http://www.ttsvisas.com" + parserCallBack.codeURL} ;
    }


    public static void checkAptmnt(
        String status, String code, String cookie,
        String month, String year) throws Exception
    {

        //System.out.println("\ncheckAptmnt()\n") ;
        String txtAcceptInput = code ;
        String viewState = status ; //"dDwtMTAyNjIwMjc0Mzt0PDtsPGk8MT47PjtsPHQ8O2w8aTwxPjtpPDU+Oz47bDx0PDtsPGk8MD47PjtsPHQ8O2w8aTwwPjs+O2w8dDw7bDxpPDA+O2k8MT47PjtsPHQ8O2w8aTwxPjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxDaG9vc2UgWm9uZSxWaXNhLExhbmd1YWdlLE1vbnRoICYgWWVhcjs+Pjs+Ozs+Oz4+O3Q8O2w8aTwxPjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxUVCBTZXJ2aWNlcyAtIFdlYiBBcHBvaW50bWVudDs+Pjs+Ozs+Oz4+Oz4+Oz4+Oz4+O3Q8O2w8aTwwPjtpPDE+O2k8Mj47aTwzPjtpPDQ+Oz47bDx0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPERlc2NyaXB0aW9uO0NvbnNab25lOz4+Oz47dDxpPDM+O0A8Q2hlbm5haTtEZWxoaTtDYWxjdXR0YTs+O0A8MXxTb3V0aDsyfE5vcnRoOzN8RWFzdDs+Pjs+Ozs+Oz4+Oz4+O3Q8O2w8aTwxPjs+O2w8dDw7bDxpPDA+Oz47bDx0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs+O2w8VmlzYVR5cGU7VmlzYVR5cGVJZDs+Pjs+O3Q8aTw0Nj47QDxBMTtBMjtBMztCMS9CMi1WSVNJVE9SIEZPUiBCVVNJTkVTUztCMS9CMi1WSVNJVE9SIEZPUiBQTEVBU1VSRTtDMTtDMS9EO0MyO0MzO0Q7RjE7RjI7RzE7RzI7RzM7RzQ7RzU7SDFCO0gxQztIMkE7SDJCO0gzO0g0O0k7SjE7SjI7TDEtSU5UUkFDT01QQU5ZIFRSQU5TRkVSRUUgKEJMQU5LRVQgUEVUSVRJT04pO0wxLUlOVFJBQ09NUEFOWSBUUkFOU0ZFUkVFIChJTkRJVklEVUFMIFBFVElUSU9OKTtMMjtNMTtNMjtPMTtPMjtPMztQMTtQMjtQMztQNDtRMTtSMTtSMjtUMjtUMztUNDtURDtUTjs+O0A8MTsyOzM7NTs2NDs4Ozk7MTA7MTE7MTI7MTY7MTc7MTg7MTk7MjA7MjE7MjI7MjM7MjQ7MjU7MjY7Mjc7Mjg7Mjk7MzA7MzE7NjU7MzQ7MzU7MzY7Mzc7NDc7NDg7NDk7NTA7NTE7NTI7NTM7NTQ7NTc7NTg7NjY7Njc7Njg7NjI7NjM7Pj47Pjs7Pjs+Pjs+Pjt0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPExhbmd1YWdlRGVzYztMYW5ndWFnZV9JZDs+Pjs+O3Q8aTwxOD47QDwtLS1TZWxlY3QtLS07RW5nbGlzaDtGcmVuY2g7R2VybWFuO0hpbmRpO0phcGFuZXNlO0thbm5hZGE7S29ua2FuaTtNYWxheWFsYW07TWFuZGFyaW4gQ2hpbmVzZTtNZWl0ZWlsb24gKE1hbmlwdXJpKTtTcGFuaXNoO1RhbWlsO1RlbHVndTtUaWJldGFuIC0gUGxlYXNlIGJyaW5nIGEgdHJhbnNsYXRvciB3aXRoIHlvdTtUdWx1O1VyZHU7T1RIRVIgLSBQbGVhc2UgYnJpbmcgYSB0cmFuc2xhdG9yIHdpdGggeW91Oz47QDwwOzE7MTI7MTM7MzsxNDs0Ozk7NTsxNTsxMTsxNjsyOzY7MTc7MTA7Nzs4Oz4+Oz47Oz47Pj47Pj47dDw7bDxpPDE+Oz47bDx0PDtsPGk8MD47aTwyPjs+O2w8dDx0PDt0PGk8MTI+O0A8SmFudWFyeTtGZWJydWFyeTtNYXJjaDtBcHJpbDtNYXk7SnVuZTtKdWx5O0F1Z3VzdDtTZXB0ZW1iZXI7T2N0b2JlcjtOb3ZlbWJlcjtEZWNlbWJlcjs+O0A8MTsyOzM7NDs1OzY7Nzs4Ozk7MTA7MTE7MTI7Pj47Pjs7Pjt0PHQ8O3A8bDxpPDA+O2k8MT47PjtsPHA8MjAwNTsyMDA1PjtwPDIwMDY7MjAwNj47Pj47Pjs7Pjs+Pjs+Pjt0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxLR0oxQUo7Pj47Pjs7Pjs+Pjs+Pjs+Pjs+Pjs+Pjs+q8vTFPQL75zNtoeZ2WlPeWwm7nw=" ;

        String url = "http://www.ttsvisas.com/ChkApptmntAvb1.aspx" ;
        String file = "e:/workarea/JavaProjects/JavaTest/ttsvisas/chkapptmntavbresp.htm";

        Vector cookies = new Vector() ;
        cookies.add(cookie);

        CHTTPURLConnection httpConn = new CHTTPURLConnection(
                url, cookies, file);

        viewState = java.net.URLEncoder.encode(viewState) ;

        String content = "__VIEWSTATE=" + viewState
            + "&cboZone=1%7CSouth&cboVisa=23&cboLanguage=1"
            + "&cboMonth=" + month
            + "&cboYear=" + year
            + "&TxtAcceptInput=" + txtAcceptInput
            + "&cmdOK=OK&HidDisplayVal=Chennai%7CH1B%7CEnglish" ;

        getAvailableDates(httpConn.doPOST(content));
    }



    public static void getAvailableDates(String data) throws Exception
    {
        //System.out.println("\ngetAvailableDates()\n");
        //String file = "e:/workarea/JavaProjects/JavaTest/chkapptmntavbresp.htm";

        //java.io.BufferedReader reader = new java.io.BufferedReader(
        //    new java.io.FileReader(file));
        java.io.StringReader reader = new java.io.StringReader(data);

        HTMLEditorKit.Parser parser = new HTMLParse().getParser() ;
        HTMLParserCallback parserCallBack = new HTMLParserCallback() ;
        parser.parse(reader, parserCallBack, false) ;

        String availableDates = "" ;
        for(int i=0; i<parserCallBack.dates.size(); i++)
        {
            String date = (String)parserCallBack.dates.elementAt(i) ;
            String status = (String)parserCallBack.status.elementAt(i) ;
            //Don't print Holiday list
            if( !status.equalsIgnoreCase("H")
                && !status.equalsIgnoreCase("C") ){
                System.out.println(date + "=" +  status);
            }

            if( status.equalsIgnoreCase("A") ){
                availableDates += "\n" + date + "=" + status;
            }
        }

        System.out.println("\nAvailableDates : " + availableDates);

        reader.close() ;

        //new File(file).delete();
    }

}



// cboZone=1|South      Chennai=1|South
// cboVisa=23           H1B=23, H4=28
// cboLanguage=1        1=English
// cboMonth=9           1-12   Jan-Dec
// cboYear=2005         2005 or 2006
//TxtAcceptInput=49d1t5       code
//HidDisplayVal=cboZone | cboVisa | cboLanguage ;
//HidDisplayVal=Chennai|H1B|English
//__VIEWSTATE=dDwtMTAyNjIwMjc0Mzt0PDtsPGk8MT47PjtsPHQ8O2w8aTwxPjtpPDU+Oz47bDx0PDtsPGk8MD47PjtsPHQ8O2w8aTwwPjs+O2w8dDw7bDxpPDA+O2k8MT47PjtsPHQ8O2w8aTwxPjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxDaG9vc2UgWm9uZSxWaXNhLExhbmd1YWdlLE1vbnRoICYgWWVhcjs+Pjs+Ozs+Oz4+O3Q8O2w8aTwxPjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxUVCBTZXJ2aWNlcyAtIFdlYiBBcHBvaW50bWVudDs+Pjs+Ozs+Oz4+Oz4+Oz4+Oz4+O3Q8O2w8aTwwPjtpPDE+O2k8Mj47aTwzPjtpPDQ+Oz47bDx0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPERlc2NyaXB0aW9uO0NvbnNab25lOz4+Oz47dDxpPDM+O0A8Q2hlbm5haTtEZWxoaTtDYWxjdXR0YTs+O0A8MXxTb3V0aDsyfE5vcnRoOzN8RWFzdDs+Pjs+Ozs+Oz4+Oz4+O3Q8O2w8aTwxPjs+O2w8dDw7bDxpPDA+Oz47bDx0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs+O2w8VmlzYVR5cGU7VmlzYVR5cGVJZDs+Pjs+O3Q8aTw0Nj47QDxBMTtBMjtBMztCMS9CMi1WSVNJVE9SIEZPUiBCVVNJTkVTUztCMS9CMi1WSVNJVE9SIEZPUiBQTEVBU1VSRTtDMTtDMS9EO0MyO0MzO0Q7RjE7RjI7RzE7RzI7RzM7RzQ7RzU7SDFCO0gxQztIMkE7SDJCO0gzO0g0O0k7SjE7SjI7TDEtSU5UUkFDT01QQU5ZIFRSQU5TRkVSRUUgKEJMQU5LRVQgUEVUSVRJT04pO0wxLUlOVFJBQ09NUEFOWSBUUkFOU0ZFUkVFIChJTkRJVklEVUFMIFBFVElUSU9OKTtMMjtNMTtNMjtPMTtPMjtPMztQMTtQMjtQMztQNDtRMTtSMTtSMjtUMjtUMztUNDtURDtUTjs+O0A8MTsyOzM7NTs2NDs4Ozk7MTA7MTE7MTI7MTY7MTc7MTg7MTk7MjA7MjE7MjI7MjM7MjQ7MjU7MjY7Mjc7Mjg7Mjk7MzA7MzE7NjU7MzQ7MzU7MzY7Mzc7NDc7NDg7NDk7NTA7NTE7NTI7NTM7NTQ7NTc7NTg7NjY7Njc7Njg7NjI7NjM7Pj47Pjs7Pjs+Pjs+Pjt0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPExhbmd1YWdlRGVzYztMYW5ndWFnZV9JZDs+Pjs+O3Q8aTwxOD47QDwtLS1TZWxlY3QtLS07RW5nbGlzaDtGcmVuY2g7R2VybWFuO0hpbmRpO0phcGFuZXNlO0thbm5hZGE7S29ua2FuaTtNYWxheWFsYW07TWFuZGFyaW4gQ2hpbmVzZTtNZWl0ZWlsb24gKE1hbmlwdXJpKTtTcGFuaXNoO1RhbWlsO1RlbHVndTtUaWJldGFuIC0gUGxlYXNlIGJyaW5nIGEgdHJhbnNsYXRvciB3aXRoIHlvdTtUdWx1O1VyZHU7T1RIRVIgLSBQbGVhc2UgYnJpbmcgYSB0cmFuc2xhdG9yIHdpdGggeW91Oz47QDwwOzE7MTI7MTM7MzsxNDs0Ozk7NTsxNTsxMTsxNjsyOzY7MTc7MTA7Nzs4Oz4+Oz47Oz47Pj47Pj47dDw7bDxpPDE+Oz47bDx0PDtsPGk8MD47aTwyPjs+O2w8dDx0PDt0PGk8MTI+O0A8SmFudWFyeTtGZWJydWFyeTtNYXJjaDtBcHJpbDtNYXk7SnVuZTtKdWx5O0F1Z3VzdDtTZXB0ZW1iZXI7T2N0b2JlcjtOb3ZlbWJlcjtEZWNlbWJlcjs+O0A8MTsyOzM7NDs1OzY7Nzs4Ozk7MTA7MTE7MTI7Pj47Pjs7Pjt0PHQ8O3A8bDxpPDA+O2k8MT47PjtsPHA8MjAwNTsyMDA1PjtwPDIwMDY7MjAwNj47Pj47Pjs7Pjs+Pjs+Pjt0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDxwPHA8bDxUZXh0Oz47bDw0OUQxVDU7Pj47Pjs7Pjs+Pjs+Pjs+Pjs+Pjs+Pjs+d1z8bj0WUIQI7e+85Vg48j2Y5WQ=




class HTMLParserCallback extends HTMLEditorKit.ParserCallback
{
    Vector dates = new Vector(31);
    Vector status = new Vector(31);
    boolean isDatesRowStarted = false ;
    boolean isStatusRowStarted = false ;

    Vector cookies = new Vector() ;
    String viewState = null ;
    String codeURL = null ;

    public void handleSimpleTag(HTML.Tag tag,
        MutableAttributeSet attribSet, int offSet)
    {
        if( tag.equals(HTML.Tag.INPUT) )
        {
            if("__VIEWSTATE".equals(attribSet.getAttribute(HTML.Attribute.NAME)))
            {
                Object value = attribSet.getAttribute(HTML.Attribute.VALUE) ;
                viewState = value.toString() ;
            }
        }

        if( tag.equals(HTML.Tag.IMG) )
        {
            if("HipCtrl".equals(attribSet.getAttribute(HTML.Attribute.ID)))
            {
                Object codeImgURL = attribSet.getAttribute(HTML.Attribute.SRC) ;
                codeURL = codeImgURL.toString();

            }
        }

    }

    public void handleText(char[] data, int pos)
    {
        String text = new String(data);
        if( text.trim().length() == 0 ){
            return;
        }

        if(text.equals("Month Year")){
            isDatesRowStarted = true ;
            return;
        }

        if(text.endsWith("2005")
           || text.endsWith("2006") )
        {
            isStatusRowStarted = true ;
            isDatesRowStarted = false ;
            return ;
        }

        if(isDatesRowStarted){
            dates.addElement(text);
        }

        if(isStatusRowStarted)
        {
            if( status.size() == dates.size() ){
                isStatusRowStarted = false ;
                return;
            }
            status.addElement(text);
        }

    }


}


class HTMLParser extends HTMLEditorKit
{

    public HTMLEditorKit.Parser getParser()
    {
        return super.getParser();
    }
}
