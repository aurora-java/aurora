/*
 * Created on 2008-6-19
 */
package aurora.service.validation;

import java.util.Iterator;

public class ParameterListIterator implements IParameterIterator {
    
    Iterator    mIterator;
    
    public ParameterListIterator( Iterator it ){
        mIterator = it;
    }

    public boolean hasNext() {
        return mIterator.hasNext();
    }

    public IParameter next() {
        return (IParameter)mIterator.next();
    }

}
