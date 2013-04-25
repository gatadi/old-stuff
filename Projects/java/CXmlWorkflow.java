import com.snapfish.core.servlet.CHandlerContext ;
import com.snapfish.core.servlet.IXmlWorkflow ;
import javax.servlet.ServletException;
import java.io.IOException ;


public class CXmlWorkflow extends com.snapfish.core.servlet.CXmlWorkflow
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("URL_HOME=" + IURLConstants.URL_HOME);
        CHandlerContext hCtx = null ;
        getInstance().runUrlId(hCtx, IURLConstants.URL_HOME);
    }

    public static synchronized IXmlWorkflow getInstance()
    {
        return new CXmlWorkflow();
    }
    
    public int runUrlId(CHandlerContext ctx, int id)
        throws IOException, ServletException
    {
        switch(id) {
          default:
            super.runUrlId(ctx, id) ;
          case 636 : //ATT_ADD_PHONE_CONFIRM
            System.out.println("id=" + id);
            return do_URL_HOME(ctx);
        }
    }


    private final static int do_URL_HOME(CHandlerContext ctx)
        throws ServletException, IOException
    {
        System.out.println("Test CXmlWorkflow...");
        return IURLConstants.URL_DONE ;
    }


}
