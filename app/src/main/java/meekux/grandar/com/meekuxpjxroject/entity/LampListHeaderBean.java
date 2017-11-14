package meekux.grandar.com.meekuxpjxroject.entity;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.cookie.MyDeviceBean;
import com.chad.library.adapter.base.entity.SectionEntity;
public class LampListHeaderBean extends SectionEntity<MyDeviceBean> {


    public LampListHeaderBean(MyDeviceBean myDeviceBean) {
        super(myDeviceBean);
    }

    public LampListHeaderBean(boolean isHeader, String header) {
        super(isHeader, header);
    }
}
