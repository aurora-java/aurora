/*
 * Created on 2011-8-19 下午04:29:02
 * $Id$
 */
package aurora.bm;

public class BusinessModelReference {
    
    BusinessModel       businessModel;
    

    public BusinessModelReference(BusinessModel businessModel) {
        this.businessModel = businessModel;
    }

    public BusinessModel getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(BusinessModel businessModel) {
        this.businessModel = businessModel;
    }

}
