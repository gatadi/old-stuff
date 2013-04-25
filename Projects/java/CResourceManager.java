import java.util.HashMap ;
import java.util.Locale ;
import java.io.UnsupportedEncodingException ;


public class CResourceManager
{
    private static HashMap hashMap = new HashMap(1) ;

    private Locale locale ;
    private String asp ;
    private HashMap lookup ;

    private CResourceManager(String asp, Locale locale)
    {
        this.asp = asp ;
        this.locale = locale ;
        this.lookup = new HashMap(100) ;
    }


    public CResourceBundle getResourceBundle(String baseName)
    {
        if( lookup.get(baseName) == null )
        {
            try
            {
                synchronized(lookup)
                {
                    if( lookup.get(baseName) != null ){
                        return (CResourceBundle)lookup.get(baseName);
                    }

                    lookup.put(baseName, new CResourceBundle(
                            this.asp, baseName, this.locale)) ;
                }
            }catch(UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
        }

        return (CResourceBundle)lookup.get(baseName);
    }

    public static CResourceManager getInstance(String asp, Locale locale)
    {
        if( hashMap.get(asp + "_" + locale.toString()) == null )
        {
            synchronized(hashMap)
            {
                hashMap.put(asp + "_" + locale.toString(),
                    new CResourceManager(asp, locale) );
            }
            
        }

        return (CResourceManager)hashMap.get(asp + "_" + locale.toString()) ;
    }


    public static CResourceBundle getResourceBundle(
        String asp, String baseName, Locale locale)
    {
        return getInstance(asp, locale).getResourceBundle(baseName) ;
    }


    public static void main(String[] args)
    {
        CResourceBundle bundle = CResourceManager.getInstance(
            "SNAPFISH", new Locale("hi", "IN")).getResourceBundle("ai18");
        
        System.out.println(bundle.getMessage("Key1"));
        System.out.println(bundle.getImgSrc("Key1", false));
    }


}   //end of CResourceManager

