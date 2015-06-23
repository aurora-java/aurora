/*
 * Created on 2008-6-19
 */
package aurora.service.json;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.Writer;

public class JSONPrintWriter extends PrintWriter {
    
    public JSONPrintWriter( Writer out ){
        super(out);
    }    
   
    
    public void printInternal( char c)
        throws IOException
    {
        String       t;
        switch (c) {
        case '\\':
        case '"':
            out.write('\\');
            out.write(c);
            break;
        case '/':
            out.write('\\');
            out.write(c);
            break;
        case '\b':
            out.write("\\b");
            break;
        case '\t':
            out.write("\\t");
            break;
        case '\n':
            out.write("\\n");
            break;
        case '\f':
            out.write("\\f");
            break;
        case '\r':
            out.write("\\r");
            break;
        default:
            if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || 
                           (c >= '\u2000' && c < '\u2100')) {
                t = "000" + Integer.toHexString(c);
                out.write("\\u" + t.substring(t.length() - 4));
            } else {
                out.write(c);
            }
        }        
    }
    
    public void  printInternal(String string) 
        throws IOException
    {
        if(string==null) return;
        
        char         b;
        char         c = 0;
        int          i;
        int          len = string.length();
        //StringBuffer sb = new StringBuffer(len + 4);
        String       t;
        out.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                out.write('\\');
                out.write(c);
                break;
            case '/':
                if (b == '<') {
                    out.write('\\');
                }
                out.write(c);
                break;
            case '\b':
                out.write("\\b");
                break;
            case '\t':
                out.write("\\t");
                break;
            case '\n':
                out.write("\\n");
                break;
            case '\f':
                out.write("\\f");
                break;
            case '\r':
                out.write("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || 
                               (c >= '\u2000' && c < '\u2100')) {
                    t = "000" + Integer.toHexString(c);
                    out.write("\\u" + t.substring(t.length() - 4));
                } else {
                    out.write(c);
                }
            }
        }
        out.write('"');
        return;
    }
    
    private void myEnsureOpen() throws IOException {
        if (out == null)
            throw new IOException("Stream closed");
    }    

    
    /** 
     * Write a single character.
     * @param c int specifying a character to be written.
     */
    public void write(int c) {
    try {
        synchronized (lock) {
        myEnsureOpen();
        out.write(c);
        }
    }
    catch (InterruptedIOException x) {
        Thread.currentThread().interrupt();
    }
    catch (IOException x) {
        setError();
    }
    }

    /** 
     * Write a portion of an array of characters. 
     * @param buf Array of characters
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     */
    public void write(char buf[], int off, int len) {
    try {
        synchronized (lock) {
        myEnsureOpen();
        String s = new String(buf, off, len);
        printInternal(s);
        }
    }
    catch (InterruptedIOException x) {
        Thread.currentThread().interrupt();
    }
    catch (IOException x) {
        setError();
    }
    }

    /**
     * Write an array of characters.  This method cannot be inherited from the
     * Writer class because it must suppress I/O exceptions.
     * @param buf Array of characters to be written
     */
    public void write(char buf[]) {
        try{
            synchronized (lock) {
            myEnsureOpen();            
            printInternal( new String(buf));
            }
        }catch (IOException x) {
            setError();
        }
    }

    /** 
     * Write a portion of a string. 
     * @param s A String
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     */
    public void write(String s, int off, int len) {
    try {
        synchronized (lock) {
        myEnsureOpen();
        String str = new String(s.substring(off, off+len));
        printInternal(str);
        }
    }
    catch (InterruptedIOException x) {
        Thread.currentThread().interrupt();
    }
    catch (IOException x) {
        setError();
    }
    }


    
}
