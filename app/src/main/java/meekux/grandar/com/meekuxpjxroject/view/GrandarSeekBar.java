package meekux.grandar.com.meekuxpjxroject.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class GrandarSeekBar extends SeekBar {
    private static final String TAG = "GrandarSeekBar";

    public GrandarSeekBar(Context context) {
        super(context);
    }

    public GrandarSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GrandarSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        float curPos = (getProgress() * getWidth()) / getMax();

        if (action == MotionEvent.ACTION_MOVE) {
            if (!isPressed()) {
                final float x = event.getX();

                if (x >= curPos - (getThumbOffset() / 2) && x <= curPos + (getThumbOffset() / 2)) {
                    event.setAction(MotionEvent.ACTION_DOWN);
                    super.onTouchEvent(event);
                }
            } else {
                return super.onTouchEvent(event);
            }
        } else {
            return super.onTouchEvent(event);
        }
        return true;
    }

}
