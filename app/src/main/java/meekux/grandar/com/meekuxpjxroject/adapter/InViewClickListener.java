/**
 * 
 */
package meekux.grandar.com.meekuxpjxroject.adapter;


import android.view.View;

/**
 *
 * com.fph.appnavigationlib.adapter.InViewClickListenerImp.java
 * 
 * Created by wang on 2016年6月3日下午4:06:24 
 * 
 * Tips: Adapter item id Onclick
 */
public interface InViewClickListener<T> {
	public void OnClickListener(View parentV, View v, Integer position,
								T values);
	public void OnClickLongListener(View parentV, View v, Integer position,
								T values);
}
