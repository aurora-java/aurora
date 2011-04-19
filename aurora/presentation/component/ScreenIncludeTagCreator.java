/*
 * Created on 2011-4-19 下午12:05:27
 * $Id$
 */
package aurora.presentation.component;

import uncertain.ocm.IObjectRegistry;
import uncertain.util.template.ITagContent;
import uncertain.util.template.ITagCreator;

public class ScreenIncludeTagCreator implements ITagCreator {
    
    IObjectRegistry     mReg;

    /**
     * @param mReg
     */
    public ScreenIncludeTagCreator(IObjectRegistry reg) {
        this.mReg = reg;
    }

    public ITagContent createInstance(String namespace, String tag) {
        return new ScreenIncludeTag(tag, mReg);
    }

}
