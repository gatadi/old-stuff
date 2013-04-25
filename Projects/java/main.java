/*
 * @(#)main.java	1.7 99/02/18
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.io.File;
import com.sun.xml.parser.Resolver;
import com.sun.xml.tree.XmlDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class main
{
    //
    // Reading and writing an XML document stored in a file.
    //
    public static void main (String argv [])
    {
	InputSource	input;
	XmlDocument	doc;

	if (argv.length != 1) {
	    System.err.println ("Usage: cmd filename");
	    System.exit (1);
	}

	try {
	    // turn the filename into an input source
	    input = Resolver.createInputSource (new File (argv [0]));

	    // turn it into an in-memory object
	    // ... the "false" flag says not to validate
	    doc = XmlDocument.createXmlDocument (input, false);

	    // normalize text representation
	    doc.getDocumentElement ().normalize ();

	    // prettyprint
	    doc.write (System.out);

	} catch (SAXParseException err) {
	    System.out.println ("** Parsing error" 
		+ ", line " + err.getLineNumber ()
		+ ", uri " + err.getSystemId ());
	    System.out.println("   " + err.getMessage ());
	    // print stack trace as below

	} catch (SAXException e) {
	    Exception	x = e.getException ();

	    ((x == null) ? e : x).printStackTrace ();

	} catch (Throwable t) {
	    t.printStackTrace ();
	}

	System.exit (0);
    }
}
