

public class CExceptionTest
{
    public static void main(String[] args) throws Exception
    {
        try{
            new CExceptionTest().f3() ;
        }catch(CException1 e)
        {
            //e.printStackTrace() ;
            System.out.println("\n******* Caught") ;
            throw e ;
        }

    }   //end of main(String[])


    public void f1() throws  Exception
    {
        System.out.println("f1()") ;

        try{
            String s = null ;
            s.toString() ;
        }catch(Exception e){
            throw new CException1(getStackTrace(e) + "\nException occured in f1() \n");
        }


    }   //end of f1()

    public void f2() throws Exception
    {
        System.out.println("f2()") ;
        try{
            f1() ;
        }catch(CException1 e)
        {
            throw new CException2(getStackTrace(e) + "\nException occured in f2()");
            //throw e ;

        }

    }   //end of f2()


    public void f3() throws Exception
    {
        System.out.println("f3()") ;
        try{
            f2() ;
        }catch(Exception e)
        {
            //throw new CException3("Exception occured in f3()");
            throw e;
        }

    }   //end of f2()

                    
    public static String getStackTrace(Throwable t)
    {
        java.io.StringWriter sw = new java.io.StringWriter() ;
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        t.printStackTrace(pw) ;
        return sw.toString() ;
        
    }   //end of getStackTRace() ;
    
}   //end of CExceptionTest





class CException1 extends Exception
{
    CException1(String str)
    {
        super(str) ;
    }

}

class CException2 extends Exception
{
    CException2(String str)
    {
        super(str) ;
    }
}

class CException3 extends Exception
{
    CException3(String str)
    {
        super(str) ;
    }
}
