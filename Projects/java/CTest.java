//import java.util.Locale ;
import java.io.* ;
import java.util.* ;

/*
import com.snapfish.core.servlet.* ;
import com.snapfish.rendering.handlers.CRepositoryReader ;
import com.snapfish.rendering.common.CMontageBean ;
import com.snapfish.rendering.handlers.CGlobalXMLParser ;

import com.snapfish.util.CResourceHandler ;
import com.snapfish.file.CFile ;
import java.nio.charset.Charset ;
import com.snapfish.core.CProxyServer;

import java.nio.charset.Charset ;

import java.util.LinkedList ;
import java.util.Arrays ;

import com.snapfish.util.CDecrypter;
import com.snapfish.i18n.CResourceManager ;
import com.snapfish.i18n.CResourceBundle ;

import javax.xml.parsers.DocumentBuilder ;
import javax.xml.parsers.DocumentBuilderFactory ;
import org.w3c.dom.Document ;
import org.w3c.dom.NodeList ;
import org.w3c.dom.Node ;
import org.w3c.dom.Element ;

import com.snapfish.core.servlet.CLinkManager ;
import com.snapfish.core.servlet.CXmlWorkflowManager ;
import com.snapfish.core.CWebsiteInfo ;


import com.perforce.api.* ;

*/
import java.util.* ;

import java.lang.reflect.* ;

import java.util.Date;
import java.util.Vector;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CTest
{


    public static void main(String[] args) throws Exception

    {

        System.out.println("\nCTest.main() : "  ) ;

        double x = 8.0 ;
        int y = 8 ;
        System.out.print( x==y);




        /*
        System.out.println("admanager=" + new CDecrypter().decrypt("DES+(JVY9I\\WU^/H`\n`\nend"));
        System.out.println("adnatalieau=" + new CDecrypter().decrypt("DES+0J+5.?%+_M=&R@$5RD5UW:```\n`\nend"));
        System.out.println("eydie=" + new CDecrypter().decrypt("DES+(B[HR9Y#A(,X`\n`\nend"));
        System.out.println("campsnap=" + new CDecrypter().decrypt("DES+(D\"(NM&W:Y[L`\n`\nend"));
        System.out.println("campsnapfishau=" + new CDecrypter().decrypt("DES+0RKVH$A6&I`6M%.DU24W-4@``\n`\nend"));
        System.out.println("campsnapfishde=" + new CDecrypter().decrypt("DES+0RKVH$A6&I`6M%.DU24W-4@``DES+(Y`#01^&^^28`\n`\nend"));
        System.out.println("contestmanager=" + new CDecrypter().decrypt("DES+()'NG63C3PLH`\n`\nend"));
        */

        /*
        int photoboxW = 94 ; //514 ;
        int photoboxH = 137; //746 ;
        int cropType = photoboxW > photoboxH ? 0 : 1 ;
        double cropToAspectRatio = 1.0*photoboxW/photoboxH ;

        int pictWidth = 64 ;
        int pictHeight = 96;
        String journal = "" ;//"ca=0.24270072992700728,0,0.7572992700729927,1";
        String strJournalca = getCrop(pictWidth,pictHeight, cropToAspectRatio, cropType) ;
        journal = getFinalJournal(journal, strJournalca) ;
        System.out.println("CropType=" + cropType + "\nJournal=" + strJournalca);
        System.out.println("FinalJournal=" + journal);

        //ca=0.04257907542579077,0,0.9574209245742092,1

        */



    }

    public static String getCrop(int pictWidth, int pictHeight,
            double aspectRatio) {
        boolean isLandscape = true;
        String strJournalca = "";
        double crop1, crop2;
        double dblaspectRatio = (aspectRatio >= 1.0) ? aspectRatio
                : 1.0 / aspectRatio;

        if (pictHeight > pictWidth) {
            isLandscape = false;
        }
        if (isLandscape) {
            crop1 = (pictWidth - (pictHeight * dblaspectRatio))
            / (2.0 * pictWidth);
            if (crop1 < 0) {
                crop1 = (pictHeight - (pictWidth / dblaspectRatio))
                / (2.0 * pictHeight);
                isLandscape = false;
            }
            crop2 = 1 - crop1;
        } else {
            crop1 = (pictHeight - (pictWidth * dblaspectRatio))
            / (2.0 * pictHeight);
            if (crop1 < 0) {
                crop1 = (pictWidth - (pictHeight / dblaspectRatio))
                / (2.0 * pictWidth);
                isLandscape = true;
            }
            crop2 = 1 - crop1;
        }

        if (isLandscape) {
            strJournalca = "/ca=" + "0," + crop1 + ",1," + crop2;
        } else {
            strJournalca = "/ca=" + crop1 + ",0," + crop2 + ",1";
        }
        return strJournalca;

    } //end of function getCrop()


    public static String getCrop(int pictWidth, int pictHeight,
            double aspectRatio, int cropType)
    {
        String strJournalca = "";
        double crop1, crop2;
        double width , height ;
        boolean isLandscape = pictWidth > pictHeight;

        double dblaspectRatio = (aspectRatio >= 1.0) ? aspectRatio
                : 1.0 / aspectRatio;

        //for lanscape tiled
        if (cropType == 0 && !isLandscape)
        {
            width = pictWidth ;
            height = pictWidth / dblaspectRatio ;
            crop1 = (pictHeight - height )/(2.0*pictHeight) ;
            crop2 = 1 - crop1;
            strJournalca = "/ca=" + crop1 + ",0,"  + crop2 + ",1";
            return strJournalca ;
        } //for portrait tiled
        else if(cropType == 1 && isLandscape){
            height = pictHeight ;
            width = pictHeight / dblaspectRatio ;
            crop1 = (pictWidth - width )/(2.0*pictWidth) ;
            crop2 = 1 - crop1;
            strJournalca = "/ca=" + "0," + crop1 + ",1," + crop2 ;
            return strJournalca ;
        }  //for naormal cropping
        else
        {
            return getCrop(pictWidth, pictHeight, aspectRatio) ;
        }

    } //end of function getCrop()

    public static String getFinalCrop(String strca1, String strca2) {
        if (strca1 == null && strca2 == null)
            return "";
        else if (strca1 == null || strca1.equals(""))
            return strca2;
        else if (strca2 == null || strca2.equals(""))
            return strca1;
        else {
            int indexOfCa1 = strca1.indexOf("ca");

            int indexOfCa2 = strca2.indexOf("ca");

            String finalCrop = "";

            if (indexOfCa1 != -1 && indexOfCa2 != -1) {
                String strArrayofcas1[] = strca1.substring(indexOfCa1 + 3)
                .split(",");
                String strArrayofcas2[] = strca2.substring(indexOfCa2 + 3)
                .split(",");
                double dblfirstCrop[] = new double[strArrayofcas1.length];
                double dblSecondCrop[] = new double[strArrayofcas2.length];
                double finalcrop[] = new double[strArrayofcas2.length];

                for (int i = 0; i < strArrayofcas1.length
                && i < strArrayofcas2.length; i++) {
                    dblfirstCrop[i] = Double.parseDouble(strArrayofcas1[i]);
                    dblSecondCrop[i] = Double.parseDouble(strArrayofcas2[i]);
                }

                /*
                 *
                 * finalcrop[0] = 1 - (1-dblfirstCrop[0]) * (1-dblSecondCrop[0]) ;
                 * finalcrop[1] = 1 - (1-dblfirstCrop[1]) * (1-dblSecondCrop[1]) ;
                 * finalcrop[2] = dblfirstCrop[2] * dblSecondCrop[2] ;
                 * finalcrop[3] = dblfirstCrop[3] * dblSecondCrop[3] ;
                 */

                finalcrop[0] = dblfirstCrop[0] + dblSecondCrop[0]
                                                               * (dblfirstCrop[2] - dblfirstCrop[0]);
                finalcrop[1] = dblfirstCrop[1] + dblSecondCrop[1]
                                                               * (dblfirstCrop[3] - dblfirstCrop[1]);
                finalcrop[2] = dblfirstCrop[2] - (1 - dblSecondCrop[2])
                * (dblfirstCrop[2] - dblfirstCrop[0]);
                finalcrop[3] = dblfirstCrop[3] - (1 - dblSecondCrop[3])
                * (dblfirstCrop[3] - dblfirstCrop[1]);

                finalCrop = "/ca=" + finalcrop[0] + "," + finalcrop[1] + ","
                + finalcrop[2] + "," + finalcrop[3];

            }

            return finalCrop;
        }
    } //end of get final crop


public static String getFinalJournal(String oldjournal, String strJournalca) {
        String finalJournal = "";
        String ca = null;
        String rf = null;
        String of = null;
        String strfinalca = null;
        int angle = 0;

        Hashtable ht_rotation_journal = new Hashtable();

        ht_rotation_journal.put("cw", "270");
        ht_rotation_journal.put("ccw", "90");
        ht_rotation_journal.put("ud", "180");

        Hashtable ht_other_journal = new Hashtable();

        if (oldjournal == null) {
            oldjournal = "";
        }
        if (strJournalca == null) {
            strJournalca = "";
        }
        String journal = oldjournal;
        String jounalSplits[] = journal.split("/");
        String flipHorizontal = null;

        for (int i = 0; i < jounalSplits.length; i++) {
            String pair[] = jounalSplits[i].split("=");
            if (pair.length == 2) {
                if (pair[0].trim().equals("rf")) {
                    String rotation = (String) ht_rotation_journal.get(pair[1]);
                    if (rotation != null) {
                        angle += Integer.parseInt(rotation);
                        if (flipHorizontal != null) {
                            angle += 180;
                        }
                    } else {    /* Added by Vinod Gatadi to handle rendering of flipped & rotated images
                                   for which rf=90.0,fp and is to be made as rf=fh/rf=cw    */
                        if(pair[1].indexOf("90") >= 0 ){
                            angle += 90 ; flipHorizontal = "fh" ;
                        }else if (pair[1].indexOf("270") >= 0){
                            angle += 270 ; flipHorizontal = "fh" ;
                        }else{
                            flipHorizontal = "fh" ;
                    }
                    }
                } else if (pair[0].trim().equals("ca")) {
                    ca = pair[1];
                } else if (pair[0].trim().equals("of")) {
                    of = pair[1];
                } else {
                    ht_other_journal.put(pair[0], pair[1]);

                }
            }
        }

        angle = angle % 360;

        if (angle == 90)
            rf = "ccw";
        if (angle == 180)
            rf = "ud";
        if (angle == 270)
            rf = "cw";

        for (Enumeration e = ht_other_journal.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            finalJournal += "/" + key.toString() + "="
            + ht_other_journal.get(key).toString();
        }

        if (ca != null) {
            strfinalca = "/ca=" + ca;
        }

        if (strJournalca != null && strJournalca != "") {
            strJournalca = "/ca="
                + shift(strJournalca.substring("/ca=".length()), angle);
            strfinalca = getFinalCrop(strfinalca, strJournalca);
        }

        if (strfinalca != null) {

            //strfinalca = shift(strfinalca.substring ("/ca=".length()),
            // angle);
            //finalJournal = finalJournal + "/ca=no/ca=" + strfinalca ;
            finalJournal = finalJournal + "/ca=no/" + strfinalca;
        }

        if (rf != null) {
            finalJournal = finalJournal + "/rf=" + rf;
        }

        if (flipHorizontal != null) {
            finalJournal = finalJournal + "/rf=" + flipHorizontal;
        }

        if (of != null) {
            finalJournal = finalJournal + "/of=" + of;
        }

        return finalJournal;

    }

public static String shift(String ca, int rotationAngle) {
        if (ca == null) {
            return "";
        }
        String caAsAnArrayOfStrings[] = ca.split(",");
        double caAsAnArray[] = new double[] {
                Double.parseDouble(caAsAnArrayOfStrings[0]),
                Double.parseDouble(caAsAnArrayOfStrings[1]),
                Double.parseDouble(caAsAnArrayOfStrings[2]),
                Double.parseDouble(caAsAnArrayOfStrings[3]) };

        //shift ca parameters (top,left,bottom, right)
        if (caAsAnArray.length == 4) {
            switch (rotationAngle) {
            case 90:
                caAsAnArray = new double[] { caAsAnArray[1],
                    1 - caAsAnArray[2], caAsAnArray[3], 1 - caAsAnArray[0] };
                break;
            case 180:
                caAsAnArray = new double[] { 1 - caAsAnArray[2],
                    1 - caAsAnArray[3], 1 - caAsAnArray[0],
                    1 - caAsAnArray[1] };
                break;
            case 270:
                caAsAnArray = new double[] { 1 - caAsAnArray[3],
                    caAsAnArray[0], 1 - caAsAnArray[1], caAsAnArray[2] };
                break;
            }
            ca = caAsAnArray[0] + "," + caAsAnArray[1] + "," + caAsAnArray[2]
                                                                           + "," + caAsAnArray[3];
        }

        return ca;
    }



/*

    public static void createXMLDocument() throws Exception
    {
        DocumentBuilder docBuilder = DocumentBuilderFactory.
            newInstance().newDocumentBuilder() ;
        Document doc = docBuilder.newDocument() ;

        Element e1 = doc.createElement("Element1") ;
        Node n1 = doc.createTextNode("Vijay Gatadi") ;
        e1.appendChild(n1);
        doc.appendChild(e1); ;

        System.out.println(doc.toString() );

    }

    public static void parseXML() throws Exception
    {
        String xml = "<RootElement><Element1>Vijay Gatadi</Element1></RootElement>";
        DocumentBuilder docBuilder = DocumentBuilderFactory.
            newInstance().newDocumentBuilder() ;
        Document doc = docBuilder.parse(new StringBufferInputStream(xml)) ;

        NodeList list = doc.getChildNodes() ;
        for(int i=0; i<list.getLength(); i++)
        {
            Node node = list.item(i) ;
            System.out.println(node.getNodeType() + " : " + node.getNodeName());
        }




    }



    public static void i18nTest()
    {
        Locale locales[] = Locale.getAvailableLocales() ;
        for(int i=0; i<locales.length; i++)
        {
            System.out.print(locales[i].getLanguage() + "_" + locales[i].getCountry());
            System.out.println(" =>  " + locales[i].getDisplayLanguage()
                + "_" + locales[i].getDisplayCountry());
        }
    }
    */

    /*

    public static void hpInternetConfig()
    {
        System.getProperties().put( "http.proxySet", "true" );
        System.getProperties().put( "http.proxyHost", "web-proxy" );  //203.199.183.231
        System.getProperties().put( "http.proxyPort", "8088" );

        System.getProperties().put( "socksProxyHost", "socks-server" ); //203.199.183.231
        System.getProperties().put( "socksProxyPort", "1080" );
    }

    public static void icici()
    {
    String file = "e:/gatadi/ICICI/ICICI-2004.htm" ;
        String filemodified = "e:/gatadi/ICICI/ICICI-2004-new.htm" ;
        String matchString = "<td width=\"4%\" valign=\"top\" align=\"center\">" ;
        String matchString1 = "<td width=\"6%\" valign=\"top\" align=\"center\">" ;
        int y = 1 ;

        java.io.BufferedReader bin = new java.io.BufferedReader(
            new java.io.InputStreamReader(new java.io.FileInputStream(file))) ;
        java.io.PrintWriter pout = new java.io.PrintWriter(
            new java.io.FileOutputStream(filemodified)) ;

        String line = null ;
        while(  (line = bin.readLine() ) != null )
        {
            if ( line.indexOf( matchString ) != -1
            ||  line.indexOf( matchString1 ) != -1)
            {
                {
                    line = "                     " + matchString + y + "</td>"  ;
                    y++;
                }

                System.out.println(line) ;
            }
            pout.println(line) ;
        }

        bin.close() ;
        pout.close() ;

     */

}





/*

select * from acct a where a.COBRAND_ACCOUNT_ID = 'vijay@snapfish.com'

select * from acct_promotion ap where ap.ACCOUNT_OID = 500741

select * from acct_promotion where account_oid = 500741

select * from promotion where oid in (select promotion_oid from acct_promotion where account_oid = 500741)

select * from promotion where name like '%ship%'

//8x12 Memory book
update acct_promotion set promotion_oid = 906500, granted_quantity=5, remaining_quantity=5, record_seq=record_seq+1 where oid = xxxxx and account_oid = (select oid from acct a where a.COBRAND_ACCOUNT_ID = 'vijay@snapfish.com')

//8x12 Memory book addl 2page
update acct_promotion set promotion_oid = 906501, granted_quantity=50, remaining_quantity=50, record_seq=record_seq+1 where oid = xxxxx and account_oid = (select oid from acct a where a.COBRAND_ACCOUNT_ID = 'vijay@snapfish.com')

//12x12 Velvet Book
update acct_promotion set promotion_oid = 906587, granted_quantity=3, remaining_quantity=3, record_seq=record_seq+1 where oid = 54387051 and account_oid = (select oid from acct a where a.COBRAND_ACCOUNT_ID = 'vijay@snapfish.com')

//12x12 Velvet Book Addl 2page
update acct_promotion set promotion_oid = 906588, granted_quantity=50, remaining_quantity=50, record_seq=record_seq+1 where oid = 1928698 and account_oid = (select oid from acct a where a.COBRAND_ACCOUNT_ID = 'vijay@snapfish.com')

//8.5x11.5 custom book (custom dust jacket)  27.99
update acct_promotion set promotion_oid = 906560, granted_quantity=30, remaining_quantity=30, record_seq=record_seq+1 where oid = xxx and account_oid = (select oid from acct a where a.COBRAND_ACCOUNT_ID = 'vijay@snapfish.com')
update acct_promotion set promotion_oid = 906561, granted_quantity=30, remaining_quantity=30, record_seq=record_seq+1 where oid = 2177108 and account_oid = (select oid from acct a where a.COBRAND_ACCOUNT_ID = 'vijay@snapfish.com')


//8.5x11.5 custom cover book 29.99
update acct_promotion set promotion_oid = 906720, granted_quantity=30, remaining_quantity=30, record_seq=record_seq+1 where oid = xxx and account_oid = (select oid from acct a where a.COBRAND_ACCOUNT_ID = 'vijay@snapfish.com')
update acct_promotion set promotion_oid = 906721, granted_quantity=30, remaining_quantity=30, record_seq=record_seq+1 where oid = 2177108 and account_oid = (select oid from acct a where a.COBRAND_ACCOUNT_ID = 'vijay@snapfish.com')

//8.5x11 calendar
update acct_promotion set promotion_oid = 912002, granted_quantity=5, remaining_quantity=5, record_seq=record_seq+1 where oid = xxx and account_oid = (select oid from acct a where a.COBRAND_ACCOUNT_ID = 'vijay@snapfish.com')


Sridhar  : update acct_promotion set granted_quantity=300, remaining_quantity=300, record_seq=record_seq+1 where oid = 257113626 and accout_oid = (select oid from acct a where a.COBRAND_ACCOUNT_ID = 'sthota@visa.com')



6560 - Dust Jacket 8.5x11.5  DBM   >>              906560  custom book (cust dust jacket cover) 29.99
6561 - Dust Jacket 8.5x11.5 Addl pages  DMB        906561  custom book  addl

6502 Memorybook - Leather Cover           HMB      906502
6579 Memorybook - Leather Black Flanders  HMB      906502

6720  Photocover Memorybook 8.5X11.5               PMB     906720
6721  Photocover Memorybook 8.5X11.5 - addl. page  PMB     906721

*/

