import java.util.ResourceBundle ;
import java.util.MissingResourceException ;
import java.util.HashMap ;
import java.util.Enumeration ;
import java.util.Locale ;
import java.nio.charset.Charset ;
import java.io.UnsupportedEncodingException ;
import com.snapfish.core.CProxyServer ;

public class CResourceBundle
{
    private HashMap lookup  ;
    private String asp  ;

    CResourceBundle(String asp, String baseName, Locale locale)
        throws MissingResourceException, UnsupportedEncodingException
    {
        this.lookup = new HashMap(5) ;
        this.asp = asp ;
        
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale) ;
        System.out.println("Loading " + baseName
            + "_" + locale.getLanguage() + "_" + locale.getCountry()
            + ".properties ...");
        for(Enumeration e = bundle.getKeys() ; e.hasMoreElements(); )
        {
            String key = e.nextElement().toString() ;

            Charset charset = (Charset)Charset.forName("ISO-8859-1") ;
            String value = new String(charset.encode(bundle.getString(key)).array(), "UTF-8");
            lookup.put(key, value);
        }

    }   //end of getBundle(String, Locale)


    public String getMessage(String key)
    {
        Object o = lookup.get(key) ;
        if( o == null ){
            return null ;
        }
        
        return o.toString() ;
    }


    public String getImgSrc(String key, boolean isSecure)
    {
        Object o = lookup.get(key) ;
        if( o == null ){
            return null ;
        }
        return CProxyServer.getImageHostName(this.asp, isSecure) + o.toString() ;
    }

    public static void main(String[] args) throws Exception
    {
        CResourceBundle bundle = new CResourceBundle(
                "SNAPFISH", "ai18", new Locale("hi", "IN"));
        System.out.println(bundle.getMessage("Key1"));
        System.out.println(bundle.getImgSrc("Key1", true));

    }

}   //end of CResourceBundle
