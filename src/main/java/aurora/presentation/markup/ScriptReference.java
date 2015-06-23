/*
 * Created on 2009-7-21
 */
package aurora.presentation.markup;

public class ScriptReference {

    /**
     * @param src
     */
    public ScriptReference(String src) {
        super();
        this.src = src;
    }

    String  src;
    
    public String toString(){
        StringBuffer buf = new StringBuffer();
        buf.append("<script src='").append(src).append("'></script>");
        return buf.toString();
    }

}
