/*
 * Created on 2011-7-16 下午11:26:05
 * $Id$
 */
package aurora.application;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;

public class DefaultSessionInfoProvider implements ISessionInfoProvider, IGlobalInstance {
    
    String  userIdPath = "/session/@user_id";
    String  userLanguagePath = "/session/@lang";

    public boolean isLoggedin(CompositeMap session_context) {
        return getUserId(session_context)!=null;
    }

    public Object getUserId(CompositeMap session_context) {
        return session_context.getObject(userIdPath);
    }

    public String getUserLanguage(CompositeMap session_context) {
        Object lang = session_context.getObject(userLanguagePath);
        return lang==null?null:lang.toString();
    }

    public String getUserIdPath() {
        return userIdPath;
    }

    public String getUserLanguagePath() {
        return userLanguagePath;
    }

    public void setUserLanguagePath(String userLanguagePath) {
        this.userLanguagePath = userLanguagePath;
    }

    public void setUserIdPath(String userIdPath) {
        this.userIdPath = userIdPath;
    }

}
