/*
 * Created on 2011-1-21 下午03:06:43
 * $Id$
 */
package aurora.presentation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import uncertain.composite.CompositeMap;
import uncertain.util.template.CompositeMapTagCreator;
import uncertain.util.template.ITagCreatorRegistry;
import uncertain.util.template.TagCreatorRegistry;
import uncertain.util.template.TagTemplateParser;
import uncertain.util.template.TextTemplate;

/**
 * Provides static utility method to parse string containing tags 
 */
public class TagParseUtil {

    static final CompositeMapTagCreator DEFAULT_CREATOR = new CompositeMapTagCreator();
    
    public static String getParsedContent(TagTemplateParser parser, String text,
            CompositeMap model, ITagCreatorRegistry reg) throws IOException {
        if(text==null)
            return null;
        if(text.indexOf('$')<0)
            return text;
//      StringReader reader = new StringReader(text);
        TextTemplate tplt = parser.buildTemplate(text, reg);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);
        tplt.createOutput(writer, model);
        writer.flush();
        baos.flush();
        String result = baos.toString();
        baos.close();
        return result;
    }

    public static String parseStringFromSession(BuildSession session, String text,
            CompositeMap model) throws IOException {
        ITagCreatorRegistry parent = session.getTagCreatorRegistry();
        TagCreatorRegistry reg = new TagCreatorRegistry();
        reg.setDefaultCreator(DEFAULT_CREATOR);
        reg.setParent(parent);
        return getParsedContent(session.getPresentationManager()
                .getTemplateParser(), text, model, reg);
    }

}
