/*
 * Created on 2009-5-15
 */
package aurora.presentation.component;

import uncertain.util.template.ITagContent;
import uncertain.util.template.ITagCreator;
import aurora.presentation.ViewContext;

public class ViewContextTagCreator implements ITagCreator {
    
    ViewContext     mViewContext;
    
    public ViewContextTagCreator( ViewContext context){
        mViewContext = context;
    }

    public ITagContent createInstance(String name_space, String tag) {
        return new ViewContextTag( mViewContext, tag );
    }
    
    public ViewContext getViewContext(){
        return mViewContext;
    }

}
