package meekux.grandar.com.meekuxpjxroject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.FolderBean;

import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.service.PlayLightSongListService;

/**
 * Created by zhaoyang on 2017/5/15.
 */

public class MusicsingleAdapter extends BaseAdapter {
    Context context;
    List<FolderBean> list;
    private boolean isPasue = false; //是否暂停

    public MusicsingleAdapter(Context context, List<FolderBean> data) {
        this.context = context;
        this.list = data;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_activity_item_music, null);
            holder = new ViewHolder();
            holder.text_name = view.findViewById(R.id.main_music_item_title);
            holder.text_des = view.findViewById(R.id.main_music_item_des);
            holder.text_time = view.findViewById(R.id.main_music_item_time);
            holder.img_avatar = view.findViewById(R.id.main_music_item_img);
            holder.img_state = view.findViewById(R.id.main_music_item_state);
            holder.img_icon = view.findViewById(R.id.main_music_item_icon);
            holder.view_rl = view.findViewById(R.id.main_music_item_view);
            holder.music_item_icon = view.findViewById(R.id.music_item_icon);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.img_avatar.setImageResource(list.get(position).getImg());
        holder.music_item_icon.setImageResource(list.get(position).getImg_icon());
        holder.text_name.setText(list.get(position).getPath_name());
        holder.text_des.setText(list.get(position).getTitle());
        final ViewHolder finalHolder = holder;
        holder.view_rl.setOnClickListener(view1 -> {
            /**
             * 首次进来播放MP3
             */
            if (PlayLightSongListService.mediaPlayer == null) {
                onMusicClick.play(position, 0);
                finalHolder.img_state.setImageResource(R.mipmap.music_pause_item);
                finalHolder.img_state.setVisibility(View.VISIBLE);
            }
            /**
             * 暂停MP3播放
             */
            else if (PlayLightSongListService.mediaPlayer.isPlaying()) {
                onMusicClick.play(position, 1);
                finalHolder.img_state.setImageResource(R.mipmap.music_play_item);
                isPasue = true;
            }

            /**
             * 继续MP3播放
             */
            else if (isPasue) {
                finalHolder.img_state.setImageResource(R.mipmap.music_pause_item);
                onMusicClick.play(position, 2);
                isPasue = false;
            }
        });

        if (PlayLightSongListService.mediaPlayer == null) {
            finalHolder.img_state.setVisibility(View.GONE);
            notifyDataSetChanged();
            onMusicClick.play(position, 1);
        } else if (PlayLightSongListService.mediaPlayer.isPlaying()) {
            finalHolder.img_state.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }
        notifyDataSetChanged();
        return view;
    }

    class ViewHolder {
        TextView text_name;
        TextView text_des;
        TextView text_time;
        ImageView img_avatar;
        ImageView img_state;
        ImageView img_icon, music_item_icon;
        RelativeLayout view_rl;
    }

    public interface OnMusicClick {
        void play(int position, int state);
    }

    private OnMusicClick onMusicClick;

    public void setOnMusicClick(OnMusicClick onMusicClick) {
        this.onMusicClick = onMusicClick;
    }

}
