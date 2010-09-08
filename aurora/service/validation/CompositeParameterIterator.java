/*
 * Created on 2010-9-8 下午01:12:42
 * $Id$
 */
package aurora.service.validation;

import java.util.Collection;
import java.util.Iterator;

/**
 * Accept a collection of IParameterIterator and iterates each iterator
 */
public class CompositeParameterIterator implements IParameterIterator {
    
    /**
     * @param parameterList
     */
    public CompositeParameterIterator(Collection parameterList) {
        if(parameterList==null)
            throw new NullPointerException("Collection is null");        
        mParameterList = parameterList;
        mListIterator = parameterList.iterator();
        moveNext();
    }

    Collection              mParameterList;
    Iterator                mListIterator;
    IParameterIterator      mCurrentIterator;
    
    
    private void moveNext(){
        if(mListIterator.hasNext())
            mCurrentIterator = (IParameterIterator)mListIterator.next();
        else
            mCurrentIterator = null;
    }
    
    public void goFirst(){
        mListIterator = mParameterList.iterator();
        moveNext();
    }

    public boolean hasNext() {
        if(mCurrentIterator==null)
            return false;
        if(!mCurrentIterator.hasNext()){
            moveNext();
            return hasNext();
        }
        return  mCurrentIterator.hasNext();     
    }

    public IParameter next() {
        if(mCurrentIterator!=null){
            IParameter param = mCurrentIterator.next();
            if(!mCurrentIterator.hasNext())
                moveNext();
            return param;
        }
        else
            return null;
    }

}
