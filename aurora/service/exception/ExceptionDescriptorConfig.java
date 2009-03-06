/*
 * Created on 2008-7-1
 */
package aurora.service.exception;

import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;
import uncertain.core.UncertainEngine;
import uncertain.ocm.ObjectSpace;
import aurora.service.ServiceContext;

public class ExceptionDescriptorConfig implements IExceptionDescriptor {
    
    // Map<String,IExceptionDescriptor>
    Map                         mClassMap;
    
    IExceptionDescriptor        mDefaultDescriptor;
    
    UncertainEngine             mUncertainEngine;
    
    public ExceptionDescriptorConfig(UncertainEngine engine){
        mClassMap = new HashMap();
        mUncertainEngine = engine;
        mDefaultDescriptor = new DefaultExceptionDescriptor();
        registerInstance();
    }

    public CompositeMap process(ServiceContext context, Throwable exception) {
        IExceptionDescriptor desc = (IExceptionDescriptor)mClassMap.get(exception.getClass().getName());
        if(desc!=null)
            return desc.process(context, exception);
        else if(mDefaultDescriptor!=null)
            return mDefaultDescriptor.process(context, exception);
        return null;
    }
    
    public void addExceptionDescriptor( CompositeMap item )
        throws Exception
    {
        ExceptionDescriptor desc = (ExceptionDescriptor)DynamicObject.cast(item, ExceptionDescriptor.class);
        String exp = desc.getException();
        
        String cls = desc.getHandleClass();
        if(cls==null) throw new ConfigurationError("Must set 'handleClass' property");
        Class handle_cls = Class.forName(cls);
        Object instance = mUncertainEngine.getObjectSpace().getParameterOfType(handle_cls);
        if(instance==null)
            instance = mUncertainEngine.getObjectSpace().createInstance(handle_cls);
        if(instance==null)
            throw new IllegalArgumentException("Can't create exception handle class " + handle_cls.getName() );
        mUncertainEngine.getOcManager().populateObject(item, instance);
        
        if(exp!=null){
            mClassMap.put(exp, instance);
        }
        
    }

    /**
     * @return the mDefaultDescriptor
     */
    public IExceptionDescriptor getDefaultDescriptor() {
        return mDefaultDescriptor;
    }

    /**
     * @param defaultDescriptor the mDefaultDescriptor to set
     */
    public void setDefaultDescriptor(IExceptionDescriptor defaultDescriptor) {
        mDefaultDescriptor = defaultDescriptor;
    }
    
    public void registerInstance(){
        ObjectSpace os = mUncertainEngine.getObjectSpace();
        Object o = os.getParameterOfType(IExceptionDescriptor.class);
        if(o==null)
            os.registerParameter(IExceptionDescriptor.class, this);
    }    

}
