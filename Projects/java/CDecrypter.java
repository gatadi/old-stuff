import javax.crypto.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Hashtable;
import java.io.FileNotFoundException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
/**
 * This class decrypts Strings encrypted using DES in ECB mode and
 * PKCS#5 padding
 * @author Raman Kumar
 * @version 1.0
 */

public class CDecrypter {   

    public static void main(String[] args) throws Exception
    {
        System.out.println("CUtils.main()");
        
        //String pw = "DES+0[-N5)'<N;?6R@$5RD5UW:```\n`\nend" ;
        //System.out.println("Password=" + decrypt(pw));


    }   //end of main(String[])

  /**
   * the Cipher object for decryption.
   */
  private Cipher m_cipher;

  /**
   * hash of Cipher objects for decryption.
   */
  private static Hashtable m_cipherHash = new Hashtable();

  /**
   * the key used for decryption
   */

  private SecretKey m_secretKey;

   /**
   * properties object  to hold cryptographic properties
   **/

  private Properties m_prop;

  /**
   * encryption identifier that the data was prepended with
  **/

  private String m_encryptionIdentifier;

  /**
   * boolean to indicate whether data is uuencoded or not
   **/

   private boolean m_uuencoded;

  /**
   * no args Constructor
   */

  public CDecrypter() throws CCryptoException
  {
    try
    {
       /**
       * Import the secret Key that was used to encrypt the data.
       */

       m_secretKey = CImportKey.getKey();

       /**
       * Read in properties from crypto properties file
       **/
       m_prop = new Properties();
       InputStream in = CResourceHandler.findResourceAsStream(CryptConstants.PROPERTIES_FILE);
       m_prop.load(in);
       in.close();
    }
    catch (IOException e)
    {
       throw new CCryptoException("CEncrypter(): could not load properties file " + CryptConstants.PROPERTIES_FILE);
    }
  }


   /**
    * Decrypt strings.
    *@param s String to be decrypted
    *@return decrypted String.
    */

  public String decrypt(String str) throws CCryptoException
  {
    // cant accept null input
    byte [] cipherBytes = null;

    if (str == null)
    {
        return null;
    }


    try
    {
       /** intialize the cipher and strip off header(encryption marker) field **/

       String s = init(str);
       byte[] b = CryptUtilities.getBytes(s);
       cipherBytes = b;

      /** uuencode the cipher text if data is marked as uuencoded. **/

      if (m_uuencoded)
      {
         ByteArrayInputStream i = new ByteArrayInputStream(b);
         ByteArrayOutputStream o = new ByteArrayOutputStream();
         CUUDecoder dec = new CUUDecoder(i,o);
         dec.uudecode();
         cipherBytes = o.toByteArray();
         i.close();
         o.close();
      }

      byte [] cleartext = m_cipher.doFinal(cipherBytes);
      String st = CryptUtilities.getString(cleartext);
      return st;
    }
      catch (FileNotFoundException e)
    {
      throw new CCryptoException("decrypt() :Cryptographic properties file not found" + e);
    }
    catch (IOException e)
    {
      throw new CCryptoException(" decrypt() : IOException " + e);
    }
    catch (IllegalBlockSizeException e )
    {
      throw new CCryptoException("decrypt() : Illegal block size " + e);
    }
    catch (BadPaddingException e)
    {
      throw new CCryptoException("decrypt() : Bad Padding Exception " + e);
    }

  }




  /** strips off the encryption identifier from the encrypted data **
   ** If the identifier does not match with one of the well known identifiers
   ** in the hashtable, set identifier to DES-(i.e. DES with no uuencode)
   ** and do not strip away the header. This is for backward compatibility
   ** @param s string being decrypted.
   ** @return s string with the encryption identifier stripped off
  **/

  private String getEncryptionIdentifier(String s)
  {
     StringBuffer buf = new StringBuffer(s);
     m_encryptionIdentifier = buf.substring(0,CryptConstants.MARKER_LENGTH);
     if (CryptConstants.isValidIdentifier(m_encryptionIdentifier))
     {
        return buf.substring(CryptConstants.MARKER_LENGTH); // strip off the encryption identifier
     }
     else
     {
        m_encryptionIdentifier = "DES-";
        return buf.toString();
     }
  }




  /** Do 2 things here :
   ** 1) Initialize the cipher based on info obtained by stripping of the
   **    encryption identifier.
   ** 2) Return the encrypted data stripped of the encryption marker field
   **
   ** @param String s - the encrypted string.
   ** @param String
   **/

  private String init(String s) throws CCryptoException
  {
    try
    {
      /* Determine the course of action based on the encryption marker field in
         the encrypted data.
      */

      String stripped = getEncryptionIdentifier(s);
      String algoName = CryptConstants.getAlgorithm(m_encryptionIdentifier);
      m_uuencoded = CryptConstants.isUuencoded(m_encryptionIdentifier);

      /**
      **Obtain a cipher object and set cipher to decryption mode.
      **/

      m_cipher = (Cipher)m_cipherHash.get(algoName);
      if (m_cipher == null)
      {
          synchronized(algoName.intern())
          {
              m_cipher = (Cipher)m_cipherHash.get(algoName);
              if (m_cipher == null)
              {
                  m_cipher = Cipher.getInstance(algoName);
                  m_cipher.init(Cipher.DECRYPT_MODE, m_secretKey);
                  m_cipherHash.put(algoName, m_cipher);
              }
          }
      }

      return stripped;
    }
    catch (InvalidKeyException e )
    {
      throw new CCryptoException("CDecrypter() : Invalid key being used for encryption." + e.getMessage());
    }
    catch (NoSuchPaddingException e)
    {
      throw new CCryptoException("CDecrypter() : Padding Algorithm not found. Check " +
                                 " classpath and $JAVA_HOME/jre/lib/java.security file " + e.getMessage());
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new CCryptoException("CEncrypter(): Encryption Algorithm not found. Check " +
                                   " classpath and $JAVA_HOME/jre/lib/java.security file");
    }
  }
}




