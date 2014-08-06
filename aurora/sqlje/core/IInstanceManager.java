/*
 * Created on 2014-7-30 下午4:06:13
 * $Id$
 */
package aurora.sqlje.core;

public interface IInstanceManager {

	public <T extends ISqlCallEnabled> T createInstance(
			Class<? extends ISqlCallEnabled> clazz);

	public <T extends ISqlCallEnabled> T createInstance(
			Class<? extends ISqlCallEnabled> clazz, ISqlCallEnabled caller);

}
