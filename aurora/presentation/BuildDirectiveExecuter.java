/*
 * Created on 2007-8-23
 */
package aurora.presentation;

import aurora.util.template.IContentProvider;
import aurora.util.template.IDynamicContent;
import aurora.util.template.StringTag;

public class BuildDirectiveExecuter implements IContentProvider{
    
    ViewContext     view_context;
    BuildSession    build_session;
    char            cmd;
    String          argument;
    
    /**
     * @param view_context
     * @param build_session
     */
    public BuildDirectiveExecuter(ViewContext view_context, BuildSession build_session) {
        this.view_context = view_context;
        this.build_session = build_session;
    }

    public boolean accepts( IDynamicContent content ){
        String tag = content.toString();
        if(tag==null||tag.length()<2)
            return false;
        cmd = tag.charAt(0);
        if(cmd!='!'||cmd!='#') 
            return false;
        argument = tag.substring(1);
        return true;
    }
    
    public Object createContent(IDynamicContent content){       
        switch(cmd){
            case '!':
                try{
                    build_session.fireBuildEvent(argument, view_context);
                }catch(Exception ex){
                    throw new RuntimeException("Error when firing build event " + argument,ex);
                }
                break;
            case '#':
                break;
        }
        return null;
    }


}
