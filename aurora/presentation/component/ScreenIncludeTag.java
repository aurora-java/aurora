/*
 * Created on 2011-4-19 上午10:32:22
 * $Id$
 */
package aurora.presentation.component;

import uncertain.composite.CompositeMap;
import uncertain.exception.GeneralException;
import uncertain.ocm.IObjectRegistry;
import uncertain.util.template.ITagContent;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.service.ServiceContext;
import aurora.service.ServiceInstance;

public class ScreenIncludeTag implements ITagContent {
    
    String              mScreenName;
    IObjectRegistry     mReg;

    /**
     * @param mScreenName
     * @param mReg
     */
    public ScreenIncludeTag(String mScreenName, IObjectRegistry mReg) {
        super();
        this.mScreenName = mScreenName;
        this.mReg = mReg;
    }
    
    private ScreenInclude createScreenInclude(){
        ScreenInclude inc = new ScreenInclude(mReg);
        return inc;
    }

    public String getContent(CompositeMap context) {
        BuildSession session = null;
        CompositeMap root = null;
        
        ViewContext view_context = ViewContext.getViewContext(context);
        if(view_context!=null){
            root = view_context.getModel().getRoot();
        }else{
            root = context.getRoot();
        }
    
        if(root==null)
            throw new IllegalStateException("Can't get correct context  map containing BuildSession");
        ServiceInstance svc = ServiceInstance.getInstance(root);
        ServiceContext sctx = svc.getServiceContext();
        session = (BuildSession)sctx.getInstanceOfType(BuildSession.class);
    
        ScreenInclude sc = createScreenInclude();
        CompositeMap view = ScreenInclude.createScreenIncludeConfig(mScreenName);
        try{
            sc.doScreenInclude(session, context, view, root);
        }catch(Exception ex){
            throw  new GeneralException("aurora.presentation.component.screen_include_invoke_error", 
                    new Object[]{view.toXML()}, 
                    ex);
        }
        
        return null;
    }

}
