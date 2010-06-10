/*
 * Created on 2010-6-10 下午02:39:18
 * $Id$
 */
package aurora.bm;

import aurora.service.validation.IParameter;
import aurora.service.validation.IParameterIterator;

public class EmptyParameterIterator implements IParameterIterator {
    
    public static final EmptyParameterIterator DEFAULT_INSTANCE = new EmptyParameterIterator();

    public boolean hasNext() {
        return false;
    }

    public IParameter next() {
        return null;
    }

}
