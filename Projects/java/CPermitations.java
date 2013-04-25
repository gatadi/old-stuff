
public class CPermitations
{

    private String[] values = null ;
    private int N = 9 ;


    public CPermitations(String list)
    {
        this.values = getPermitations(list) ;
        this.N = list.length() ;
    }

    public String[] values()
    {
        return this.values ;
    }


    private  String[] getPermitations(String list)
    {
        if( list.length() == 2 )
        {
            String[] arrayOfLists = new String[list.length()] ;
            //left shift
            for(int i=0; i<list.length(); i++)
            {
                arrayOfLists[i] = list;
                list = list.substring(1)  + list.substring(0, 1) ;
            }
            return arrayOfLists ;
        }
        else
        {
            int count = 0 ;
            String[] arrayOfLists = new String[fact(list.length())];
            String[] arrayOfTempLists = getPermitations(list.substring(1));
            for(int i=0; i<arrayOfTempLists.length; i++)
            {
                String _list = list.substring(0,1) + arrayOfTempLists[i] ;
                //left shift
                for(int j=0; j<_list.length(); j++)
                {
                    arrayOfLists[count++] = _list;
                    _list = _list.substring(1)  + _list.substring(0, 1) ;
                }
            }
            return arrayOfLists ;
        }
    }

    private int fact(int n)
    {
        if( n==1 ){
            return n ;
        }
        return n*fact(n-1);
    }



    public static void main(String[] args)
    {
        long t1 = System.currentTimeMillis() ;
        CPermitations perm = new CPermitations("123456789");
        String[] values = perm.values() ;
        long t2 = System.currentTimeMillis() ;
        for(int i=0; i<values.length; i++)
        {
            //System.out.println(values[i]);
        }
        System.out.println("\nPermitations = " + values.length);
        System.out.println("\nTotal time to generate permitations is : " + (t2-t1)/1000 + "." + (t2-t1)%1000 + " seconds");
        
    }

}   //end of CPermitations class
