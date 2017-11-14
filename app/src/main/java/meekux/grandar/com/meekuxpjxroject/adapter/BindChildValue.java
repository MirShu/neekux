/**
 * 
 */
package meekux.grandar.com.meekuxpjxroject.adapter;

import java.util.HashMap;
import java.util.Map;
import com.socks.library.KLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.TextView;

import meekux.grandar.com.meekuxpjxroject.utils.ViewUtil;
import meekux.grandar.com.meekuxpjxroject.utils.WeakReferenceUtil;

/**
 * 
 *
 * com.fph.appnavigationlib.adapter.BindChildValue.java
 * 
 * Created by wang on 2016年6月1日上午10:14:13 
 * 
 * Tips:
 */
@SuppressLint("UseSparseArrays")
public class BindChildValue implements BindChildValueImp {

	private View childView;
	
	
	private Map<Integer, View> viewMap=new HashMap<Integer, View>();
	

	public BindChildValue(View chidView) {
		setChildView(chidView);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#getChildView()
	 */
	@Override
	public View getChildView() {
		return childView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#getViewById(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <V extends View> V getViewById(int resId) {
		if (getChildView() == null) {
			return null;
		}
		if (viewMap.containsKey(resId)) {
			return (V) viewMap.get(resId);
		}
		View view1 = getChildView().findViewById(resId);
		if (view1 == null) {
			return null;
		}
		viewMap.put(resId, view1);

		return (V) view1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fph.appnavigationlib.adapter.BindChildValueImp#setChildView(android.
	 * view.View)
	 */
	@Override
	public void setChildView(View childView) {
		this.childView = childView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildText(int,
	 * java.lang.CharSequence)
	 */
	@Override
	public BindChildValueImp bindChildText(int resId, Object charSequence) {
		ViewUtil.bindView(charSequence, getViewById(resId));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildUrl(int,
	 * java.lang.String)
	 */
	@Override
	public BindChildValueImp bindChildUrl(int resId, String charSequence) {
		bindChildUrl(resId, charSequence, -1, -1);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildVisible(int,
	 * int)
	 */
	@Override
	public BindChildValueImp bindChildVisible(int resId, int isVisible) {
		if (getViewById(resId) != null) {
			getViewById(resId).setVisibility(isVisible);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildDrawable(int,
	 * int)
	 */
	@Override
	public BindChildValueImp bindChildDrawable(int resId, int charSequence) {
		bindChildDrawable(resId, charSequence, -1, -1);
		return this;
	}

	@Override
	public BindChildValueImp bindChildDrawable(int resId, int charSequence, int waitDrawableId, int errorDrawableId) {
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildLinkify(int)
	 */
	@Override
	public BindChildValueImp bindChildLinkify(int resId) {
		if (getViewById(resId) != null) {
			TextView textView = getViewById(resId);
			Linkify.addLinks(textView, Linkify.ALL);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#
	 * bindChildOnClickListener(int, android.view.View.OnClickListener)
	 */
	@Override
	public BindChildValueImp bindChildOnClickListener(int resId, OnClickListener onClickListener) {
		if (getViewById(resId) != null) {
			getViewById(resId).setOnClickListener(onClickListener);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildEnable(int,
	 * boolean)
	 */
	@Override
	public BindChildValueImp bindChildEnable(int resId, boolean enable) {
		if (getViewById(resId) != null) {
			getViewById(resId).setEnabled(enable);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildCheckable(
	 * int, boolean)
	 */
	@Override
	public BindChildValueImp bindChildCheckable(int resId, boolean checkable) {
		if (getViewById(resId) != null) {
			if (getViewById(resId) instanceof CompoundButton) {
				((CompoundButton) getViewById(resId)).setChecked(checkable);
			}
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildClickable(
	 * int, boolean)
	 */
	@Override
	public BindChildValueImp bindChildClickable(int resId, boolean clickable) {
		if (getViewById(resId) != null) {
			getViewById(resId).setClickable(clickable);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#
	 * bindChildBackgroundColor(int, int)
	 */
	@Override
	public BindChildValueImp bindChildBackgroundColor(int resId, int color) {
		if (getViewById(resId)!=null) {
			getViewById(resId).setBackgroundColor(color);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#
	 * bindChildBackgroundColor(int, java.lang.String)
	 */
	@Override
	public BindChildValueImp bindChildBackgroundColor(int resId, String color) {
		bindChildBackgroundColor(resId, Color.parseColor(color));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#
	 * bindChildBackgroundDrawable(int, int)
	 */
	@Override
	public BindChildValueImp bindChildBackgroundDrawable(int resId, int drawable) {
		if (getViewById(resId)!=null) {
			try {
				getViewById(resId).setBackgroundResource(drawable);
			} catch (Exception e) {
				KLog.e(e.getMessage());
			}
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildTextColor(int, int)
	 */
	@Override
	public BindChildValueImp bindChildTextColor(int resId, int color) {
		if (getViewById(resId)!=null && getViewById(resId) instanceof TextView) {
			
			try {
				((TextView)getViewById(resId)).setTextColor(color);
			} catch (Exception e) {
				KLog.e(e.getMessage());
			}
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildTextColor(int, java.lang.String)
	 */
	@Override
	public BindChildValueImp bindChildTextColor(int resId, String color) {
		try {
			bindChildTextColor(resId,Color.parseColor(color));
		} catch (Exception e) {
			KLog.e(e.getMessage());
		}
		return this ;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildTextSize(int, float)
	 */
	@Override
	public BindChildValueImp bindChildTextSize(int resId, float size) {
	if (getViewById(resId)!=null && getViewById(resId) instanceof TextView) {
			try {
				((TextView)getViewById(resId)).setTextSize(size);
			} catch (Exception e) {
				KLog.e(e.getMessage());
			}
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildOnCheckedChangeListener(int, android.widget.CompoundButton.OnCheckedChangeListener)
	 */
	@Override
	public BindChildValueImp bindChildOnCheckedChangeListener(int resId,
			OnCheckedChangeListener onCheckedChangeListener) {
		if (getViewById(resId) !=null && getViewById(resId) instanceof CompoundButton) {
			((CompoundButton)getViewById(resId)).setOnCheckedChangeListener(onCheckedChangeListener);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildOnCheckedChangeListener(int, android.widget.RadioGroup.OnCheckedChangeListener)
	 */
	@Override
	public BindChildValueImp bindChildOnCheckedChangeListener(int resId,
			RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
		if (getViewById(resId) !=null && getViewById(resId) instanceof RadioGroup) {
			((RadioGroup)getViewById(resId)).setOnCheckedChangeListener(onCheckedChangeListener);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#getWeakContext()
	 */
	@Override
	public Context getWeakContext() {
		// TODO Auto-generated method stub
		try {
			return new WeakReferenceUtil<Context>(getChildView().getContext()).getWeakT();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChilddTextChangedListener(int, android.text.TextWatcher)
	 */
	@Override
	public BindChildValueImp bindChilddTextChangedListener(int resId, TextWatcher listener) {
		// TODO Auto-generated method stub
		if (getViewById(resId)!=null && getViewById(resId) instanceof TextView) {
			((TextView)getViewById(resId)).addTextChangedListener(listener);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildOnTouchListener(int, android.view.View.OnTouchListener)
	 */
	@Override
	public BindChildValueImp bindChildOnTouchListener(int resId, OnTouchListener onTouchListener) {
		// TODO Auto-generated method stub
		if (getViewById(resId)!=null ) {
			getViewById(resId).setOnTouchListener(onTouchListener);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildTag(int, java.lang.Object)
	 */
	@Override
	public BindChildValueImp bindChildTag(int resId, Object tag) {
		// TODO Auto-generated method stub
		if (getViewById(resId)!=null ) {
			getViewById(resId).setTag(tag);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildUrl(int, java.lang.String, java.lang.String)
	 */
	@Override
	public BindChildValueImp bindChildUrl(int resId, String charSequence, String valueFix) {
		// TODO Auto-generated method stub
	//	ViewUtil.bindView( getViewById(resId),charSequence,valueFix);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildUrl(int, java.lang.String, int, int, java.lang.String)
	 */
	@Override
	public BindChildValueImp bindChildUrl(int resId, String charSequence, int waitDrawableId, int errorDrawableId,
			String valueFix) {
		// TODO Auto-generated method stub
		//ViewUtil.bindView( getViewById(resId),charSequence,valueFix,waitDrawableId,errorDrawableId);
		return this;
	}

	@Override
	public BindChildValueImp bindChildUrl(int resId, String charSequence, int waitDrawableId, int errorDrawableId) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.fph.appnavigationlib.adapter.BindChildValueImp#bindChildChecked(int, boolean)
	 */
	@Override
	public BindChildValueImp bindChildChecked(int resId, boolean checked) {
		// TODO Auto-generated method stub
		return null;
	}


}
