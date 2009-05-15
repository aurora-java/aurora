/*
 * Created on 2009-5-15
 */
package aurora.presentation.component;

import aurora.presentation.ViewContext;
import uncertain.composite.CompositeMap;
import uncertain.util.template.ITagContent;

public class ViewContextTag implements ITagContent {
    
    /**
     * @param viewContext
     * @param tag
     */
    public ViewContextTag(ViewContext viewContext, String tag) {
        super();
        mViewContext = viewContext;
        mTag = tag;
    }

    ViewContext     mViewContext;
    String          mTag;

    public String getContent(CompositeMap context) {
        Object obj = mViewContext.getContextMap().get(mTag);
        return obj==null?"":obj.toString();
    }

}
