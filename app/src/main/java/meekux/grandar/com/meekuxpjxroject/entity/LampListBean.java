package meekux.grandar.com.meekuxpjxroject.entity;

/**
 * 落地灯实体类
 * Created by xuhaifeng on 2017/3/23.
 */

public class LampListBean {
    private int img;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String title;
    private String content;


    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LampListBean(int img, String content, String title, int type) {
        this.img = img;
        this.content = content;
        this.title = title;
        this.type=type;
    }

}
