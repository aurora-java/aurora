/*
 * Created on 2009-5-22
 */
package aurora.presentation.markup;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uncertain.composite.DynamicObject;
import aurora.presentation.ViewContext;

public class HtmlPageContext  {
    
    public static final String HTML_HEAD_CSS = "html.head.css";
    public static final String HTML_HEAD_SCRIPT = "html.head.script";
    public static final String HTML_INIT_SCRIPT = "html.init_script";    
    
    Set     mIncludedResources;
    Map     mContextMap;
    
    public static HtmlPageContext getInstance( ViewContext context ){
        HtmlPageContext page = (HtmlPageContext)context.getInstance(HtmlPageContext.class);
        if( page == null ){
            page = new HtmlPageContext( context.getMap() ); 
            context.setInstance(HtmlPageContext.class, page );
        }
        return page;
    }
    
    public HtmlPageContext( Map context_map ){
        mContextMap = context_map;
        mIncludedResources = new HashSet();
    }
 
    public void addResource( String url ){
        mIncludedResources.add(url);
    }
    
    public boolean containsResource( String url ){
        return mIncludedResources.contains(url);
    }
    
    public TagList getNamedTagList( String name ){
        TagList list = (TagList)mContextMap.get(name);
        if(list==null){
            list = new TagList();
            mContextMap.put(name, list);
        }
        return list;
    }
    
    public StringBuffer getNamedPart( String name ){
        StringBuffer buf = (StringBuffer)mContextMap.get(name);
        if(buf==null){
            buf = new StringBuffer();
            mContextMap.put(name, buf);
        }
        return buf;
    }
    
    public TagList getScriptReference(){
        return getNamedTagList(HTML_HEAD_SCRIPT);
    }
    
    public void addScript( String url ){
        boolean is_added = containsResource(url);
        if(!is_added){
            addResource( url );
            TagList list = getScriptReference();
            list.add( new ScriptReference(url));
        }
    }

    
    public TagList getCssReference(){
        return getNamedTagList(HTML_HEAD_CSS);
    }
    
    public void addStyleSheet( String url ){
        boolean is_added = containsResource(url);
        if(!is_added){
            addResource( url );
            TagList list = getCssReference();
            list.add( new StyleSheetReference(url));
        }
    }
    
    public StringBuffer getInitScript(){
        return getNamedPart(HTML_INIT_SCRIPT);
    }

}
