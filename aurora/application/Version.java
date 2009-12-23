/*
 * Created on 2006-5-25
 */
package aurora.application;

import java.lang.reflect.Method;

public class Version {
    
    public static final String AURORA_FRAMEWORK_NAMESPACE = "http://www.aurora-framework.org/application";

    static StringBuffer VERSION_STRING = new StringBuffer();
    
    static {
        VERSION_STRING.append(getMajorVersion()).append('.').append(getMinorVersion()).append('.').append(getBuild());
    }
    
    public static String getVersion(){
        return VERSION_STRING.toString();
    }
    
    public static int getMajorVersion(){
        return 1;
    }
    
    public static int getMinorVersion(){
        return 1;
    }
    
    public static int getBuild(){
        return 0;
    }
/*    
    public static void main(String[] args) throws Exception {
        Class cls = Class.forName("org.lwap.application.Version");
        Object version = cls.newInstance();
        Method m = cls.getMethod("getVersion", null);
        String s = (String)m.invoke(version, null);
        System.out.println(s);
    }
*/
}
