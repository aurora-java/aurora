/*
 * Created on 2007-8-13 ÏÂÎç09:41:55
 */
package aurora.util.template;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import uncertain.util.AdaptiveTagParser;
import uncertain.util.TagParseHandle;

public class TagTemplateParser {
    
    private class ParseHandle implements TagParseHandle {
        
        TextTemplate    template = new TextTemplate();        
        StringBuffer buf = null;
        
        public String  ProcessTag(int index, String tag){
            if( buf != null){
                //System.out.println("tag:"+tag+" buf:"+buf.toString());
                template.addContent(buf.toString());
                buf.setLength(0);
            }
            template.addContent( new StringTag(tag));
            return null;
        }
        
        public int ProcessCharacter( int index, char ch){
            if( buf == null) buf = new StringBuffer();
            buf.append(ch);
            return -1; 
        }
        
        public StringBuffer getBuffer(){
            return buf;
        }
        
        public void finish(){
            if(buf!=null)
                if(buf.length()>0)
                    template.addContent(buf.toString());
        }
    };
    
    public TextTemplate buildTemplate( InputStream is)
        throws IOException    
    {
        return buildTemplate( new InputStreamReader(is));
    }
    
    public TextTemplate buildTemplate( Reader reader) 
        throws IOException 
    {
        AdaptiveTagParser parser   = AdaptiveTagParser.newUnixShellParser();
        ParseHandle handle = new ParseHandle();        
        parser.parse( reader, handle);
        parser.clear();
        handle.finish();
        return handle.template;
    }
    
    public TextTemplate buildTemplate( String content)
        throws IOException    
    {
        return buildTemplate( new StringReader(content));
    }

}
