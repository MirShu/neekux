/**
 * 
 */
package meekux.grandar.com.meekuxpjxroject.utils;

import java.lang.ref.WeakReference;

/**
 *
 * com.fph.appnavigationlib.utils.WeakReferenceUtil.java
 * 
 * Created by wang on 2016上午10:24:43 
 * 
 * Tips:弱引用工具类  new WeakReferenceUtil(T).getWeakT();
 */
public class WeakReferenceUtil<T> {

	private WeakReference<T> weakReference;
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private WeakReferenceUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public WeakReferenceUtil(T t){
		weakReference =new WeakReference<T>(t);
	}
	
	
	public T getWeakT(){
		return weakReference.get();
	}
	
}
