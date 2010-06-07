/*
 * Created on 2010-5-25 下午09:37:32
 * $Id$
 */
package aurora.bm;

import java.util.Collection;
import java.util.Iterator;

import uncertain.composite.CompositeMap;
import aurora.service.validation.IParameter;
import aurora.service.validation.IParameterIterator;

/**
 * Iterate parameters from predefined parameter CompositeMap 
 */
public class PredefinedParameterIterator implements IParameterIterator {
    
    /**
     * @param parameters
     */
    public PredefinedParameterIterator(Collection parameters) {
        super();
        if(parameters!=null)
            this.mIterator = parameters.iterator();
    }

    Iterator        mIterator;
    
    public boolean hasNext(){
        if(mIterator==null)
            return false;
        return mIterator.hasNext();
    }
    
    public IParameter next(){
        if(!hasNext())
            return null;
        CompositeMap m = (CompositeMap)mIterator.next();
        Field f = Field.getInstance(m);
        return f;
    }

}
