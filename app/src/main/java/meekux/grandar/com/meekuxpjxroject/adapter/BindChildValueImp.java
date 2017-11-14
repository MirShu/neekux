/**
 * 
 */
package meekux.grandar.com.meekuxpjxroject.adapter;

import android.content.Context;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;

/**
 *
 * com.fph.appnavigationlib.adapter.BindChildValueImp.java
 * 
 * Created by wang on 20162016年5月31日下午2:40:53
 * 
 * Tips:
 */
public interface BindChildValueImp {

	public View getChildView();

	public void setChildView(View childView);

	public BindChildValueImp bindChildText(int resId, Object charSequence);

	public BindChildValueImp bindChildTextColor(int resId, int color);

	public BindChildValueImp bindChildTextColor(int resId, String color);

	public BindChildValueImp bindChildTextSize(int resId, float size);

	public BindChildValueImp bindChildUrl(int resId, String charSequence);

	public BindChildValueImp bindChildUrl(int resId, String charSequence, String valueFix);

	public BindChildValueImp bindChildUrl(int resId, String charSequence, int waitDrawableId, int errorDrawableId,
										  String valueFix);

	public BindChildValueImp bindChildUrl(int resId, String charSequence, int waitDrawableId, int errorDrawableId);

	public BindChildValueImp bindChildVisible(int resId, int isVisible);

	public BindChildValueImp bindChildDrawable(int resId, int charSequence);

	public BindChildValueImp bindChildDrawable(int resId, int charSequence, int waitDrawableId, int errorDrawableId);

	public BindChildValueImp bindChildLinkify(int resId);

	public BindChildValueImp bindChildOnClickListener(int resId, OnClickListener onClickListener);

	public BindChildValueImp bindChildOnCheckedChangeListener(int resId,
															  OnCheckedChangeListener onCheckedChangeListener);

	public BindChildValueImp bindChildOnCheckedChangeListener(int resId,
															  RadioGroup.OnCheckedChangeListener onCheckedChangeListener);

	// public BindChildValueImp bindChildOnClickListener(int resId,
	// OnClickListener onClickListener);
	//
	// public BindChildValueImp bindChildOnClickListener(int resId,
	// OnClickListener onClickListener);
	//
	public BindChildValueImp bindChildTag(int resId, Object tag);

	//
	public BindChildValueImp bindChildOnTouchListener(int resId, OnTouchListener onTouchListener);

	public BindChildValueImp bindChildEnable(int resId, boolean enable);

	public BindChildValueImp bindChildCheckable(int resId, boolean checkable);

	public BindChildValueImp bindChildChecked(int resId, boolean checked);

	public BindChildValueImp bindChildClickable(int resId, boolean clickable);

	public BindChildValueImp bindChildBackgroundColor(int resId, int color);

	public BindChildValueImp bindChildBackgroundColor(int resId, String color);

	public BindChildValueImp bindChildBackgroundDrawable(int resId, int drawable);

	public BindChildValueImp bindChilddTextChangedListener(int resId, TextWatcher listener);

	// public BindChildValueImp bindChildClickable(int resId, boolean
	// clickable);
	public <V extends View> V getViewById(int resId);

	public Context getWeakContext();
}
