package meekux.grandar.com.meekuxpjxroject.utils;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/8
 * 类名：ItemBean
 */
public class ItemBean {
    public ItemBean(String text, boolean select) {
        this.text = text;
        this.select = select;
    }

    private  String  text;
    private  boolean  select;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
