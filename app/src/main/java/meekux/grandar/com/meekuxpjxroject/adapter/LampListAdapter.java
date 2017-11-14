package meekux.grandar.com.meekuxpjxroject.adapter;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.socks.library.KLog;

import java.util.List;

import meekux.grandar.com.meekuxpjxroject.R;
import meekux.grandar.com.meekuxpjxroject.entity.LampListHeaderBean;
import meekux.grandar.com.meekuxpjxroject.utils.IConstant;
import meekux.grandar.com.meekuxpjxroject.utils.SharedPreferencesUtils;


/**
 * Created by baixiaoming on 2017/3/17 9:06
 * Function 灯列表适配器
 */

public class LampListAdapter extends BaseSectionQuickAdapter<LampListHeaderBean, BaseViewHolder> {
    private String sn;
private boolean isConnected;
    public LampListAdapter(List<LampListHeaderBean> data, String sn) {
        super(R.layout.layout_item_adapter_lamp, R.layout.layout_item_adapter_lamp_header, data);
        this.sn = sn;
        isConnected= SharedPreferencesUtils.getinstance().getBooleanValue(IConstant.ISCONNECT, false);
        KLog.e("是否连接状态----"+isConnected);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, LampListHeaderBean item) {
        helper.setText(R.id.lamplist_text_header, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, LampListHeaderBean item) {


        helper.setText(R.id.lamplist_text_lamptype, "IGOO_Love[" +item.t.getSn().substring(0, 3) + "]"
                +"..."+item.t.getDate())
                .setText(R.id.lamplist_text_content, item.t.getName()+"..."+item.t.getIp())
                .addOnClickListener(R.id.item_content)
                .addOnClickListener(R.id.lamplist_text_contact);

        if (sn.equals(item.t.getSn())&&isConnected){
            helper.setText(R.id.lamplist_text_contact, R.string.connected);
            helper.setTextColor(R.id.lamplist_text_contact, mContext.getResources().getColor(R.color.textColor));
            helper.setBackgroundRes(R.id.lamplist_text_contact, R.drawable.shape_corner_bg_red);
        }else {
            helper.setText(R.id.lamplist_text_contact, R.string.contact);
            helper.setTextColor(R.id.lamplist_text_contact, mContext.getResources().getColor(R.color.greencolor));
            helper.setBackgroundRes(R.id.lamplist_text_contact, R.drawable.shape_corner_bg_gray);
        }

    }

}
