package meekux.grandar.com.meekuxpjxroject.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.entity.ChangeColor;

/**
 * Created by gpc on 2017/4/24.
 */

public class ChangeColorGrideViewAdapter extends BeanAdapter<ChangeColor> {
    public ChangeColorGrideViewAdapter(Context context, int mResource) {
        super(context, mResource);
    }

    @Override
    public void bindView(View itemV, int position, ChangeColor value, BindChildValueImp bind) {
        TextView hometitle = (TextView) itemV.findViewById(R.id.changtitle);
        hometitle.setText(value.getTitle());
        ImageView homeimg = (ImageView) itemV.findViewById(R.id.changicon);
        homeimg.setImageResource(value.getIconid());
    }
}
