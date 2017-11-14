package meekux.grandar.com.meekuxpjxroject.entity;

import java.io.Serializable;

/**
 * Created by zhaoyang on 2017/5/27.
 */

public class SearchBean implements Serializable {
    private String ip;
    private String sn;
    private String name;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    private int version;
    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {

        return ip;
    }

    public String getSn() {
        return sn;
    }

    public String getName() {
        return name;
    }
}
