package meekux.grandar.com.meekuxpjxroject.entity;

/**
 * Created by gpc on 2017/3/1.
 */

public class Menu {
    public Menu(String[] meuntitle, int[] meunid){
        this.meunimageid=meunid;
        this.title=meuntitle;
    }
    public String[] getTitle() {
        return title;
    }

    public void setTitle(String[] title) {
        this.title = title;
    }

    private String[] title;//标题


    public int[] getMeunimageid() {
        return meunimageid;
    }

    public void setMeunimageid(int[] meunimageid) {
        this.meunimageid = meunimageid;
    }

    private int[] meunimageid;//菜单图

}
