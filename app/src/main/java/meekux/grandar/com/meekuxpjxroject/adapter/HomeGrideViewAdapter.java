package meekux.grandar.com.meekuxpjxroject.adapter;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.entity.Home;

/**
 * Created by gpc on 2017/4/24.
 */

public class HomeGrideViewAdapter extends BeanAdapter<Home> {
    public HomeGrideViewAdapter(GridView homeGridView, Context context, int mResource) {
        super(context, mResource);
        Init();
    }

    private void Init() {

    }

    @Override
    public void bindView(View itemV, int position, Home value, BindChildValueImp bind) {
        TextView hometitle = (TextView) itemV.findViewById(R.id.hometitle);
        hometitle.setText(value.getTitle());
        ImageView homeimg = (ImageView) itemV.findViewById(R.id.homeimg);
        homeimg.setImageResource(value.getIconid());

    }
}
