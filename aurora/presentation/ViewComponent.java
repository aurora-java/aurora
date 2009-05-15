/*
 * Created on 2007-9-5
 */
package aurora.presentation;

import uncertain.composite.ElementIdentifier;
import uncertain.util.StringSplitter;

public class ViewComponent {
    
    String                  name;
    Class                   builder;
    String                  feature_classes;
    ViewComponentPackage    owner;
    
    String                  nameSpace;
    String                  elementName;
    String                  default_template;   
/*
    // to be 
    String                  features;
    Class[]                 feature_class_array;
    
    public void setFeatureClasses( String classes )
        throws ClassNotFoundException
    {
        this.feature_classes = classes;
        String[] c = StringSplitter.splitToArray(classes, ',', false);
        feature_class_array = new Class[c.length];
        for(int i=0; i<c.length; i++){
            feature_class_array[i] = Class.forName(c[i].trim());
        }
    }
    
    public String getFeatureClasses(){
        return this.feature_classes;
    }
    
    public Class[] getFeatureClassArray(){
        return feature_class_array;
    }
*/
    
    /**
     * @return the builder
     */
    public Class getBuilder() {
        return builder;
    }

    /**
     * @param builder the builder to set
     */
    public void setBuilder(Class builder) {
        this.builder = builder;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the default_template
     */
    public String getDefaultTemplate() {
        return default_template;
    }

    /**
     * @param default_template the default_template to set
     */
    public void setDefaultTemplate(String default_template) {
        this.default_template = default_template;
    }

    /**
     * @return the _package
     */
    public ViewComponentPackage getOwner() {
        return owner;
    }

    /**
     * @param _package the _package to set
     */
    public void setOwner(ViewComponentPackage owner) {
        this.owner = owner;
    }

    /**
     * @return the elementName
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * @param elementName the elementName to set
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * @return the nameSpace
     */
    public String getNameSpace() {
        return nameSpace;
    }

    /**
     * @param nameSpace the nameSpace to set
     */
    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }
    
    public ElementIdentifier getElementIdentifier(){
        return new ElementIdentifier(nameSpace, elementName);
    }

}
