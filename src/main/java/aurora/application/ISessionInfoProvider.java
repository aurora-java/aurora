/*
 * Created on 2011-7-16 下午11:12:16
 * $Id$
 */
package aurora.application;

import uncertain.composite.CompositeMap;

/**
 * Application must provide instance implements this interface
 * for other class to check user session.
 * All methods are based on a CompositeMap containing user session data
 */
public interface ISessionInfoProvider {
    
    /**
     * @return Whether user is logged in
     */
    public boolean isLoggedin( CompositeMap session_context );
    
    public Object getUserId( CompositeMap session_context );
    
    public String getUserLanguage( CompositeMap session_context );
    
    public String getUserIdPath();
    
    public String getUserLanguagePath();

}
