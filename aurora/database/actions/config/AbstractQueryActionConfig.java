/*
 * Created on 2009-9-10 下午02:37:13
 * Author: Zhou Fan
 */
package aurora.database.actions.config;

import java.util.List;

import uncertain.composite.DynamicObject;

public class AbstractQueryActionConfig extends DynamicObject {

    public static final String KEY_RECORDNAME = "recordname";
    public static final String KEY_ROOTPATH = "rootpath";
    public static final String KEY_PAGESIZE = "pagesize";
    public static final String KEY_AUTOCOUNT = "autocount";
    private static final String KEY_FETCHALL = "fetchall";
    public static final String KEY_PARAMETER = "parameter";

    public String getParameter() {
        return getString(KEY_PARAMETER);
    }

    public void setParameter(String parameter) {
        putString(KEY_PARAMETER, parameter);
    }

    public boolean getFetchAll() {
        return getBoolean(KEY_FETCHALL, false);
    }

    public void setFetchAll(boolean fetchAll) {
        putBoolean(KEY_FETCHALL, fetchAll);
    }

    public boolean getAutoCount() {
        return getBoolean(KEY_AUTOCOUNT, false);
    }

    public void setAutoCount(boolean autoCount) {
        putBoolean(KEY_AUTOCOUNT, autoCount);
    }

    public Integer getPageSize() {
        return getInteger(KEY_PAGESIZE);
    }

    public void setPageSize(int pageSize) {
        putInt(KEY_PAGESIZE, pageSize);
    }

    public String getRootPath() {
        return getString(KEY_ROOTPATH);
    }

    public void setRootPath(String rootPath) {
        putString(KEY_ROOTPATH, rootPath);
    }

    public String getRecordName() {
        return getString(KEY_RECORDNAME);
    }

    public void setRecordName(String recordName) {
        putString(KEY_RECORDNAME, recordName);
    }

}
