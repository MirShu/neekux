package meekux.grandar.com.meekuxpjxroject.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author:SeanLim
 * Created by Time on: 2017/9/12
 * 类名：DataManager
 */
public class DataManager {
    private static List<String> sStringList = Arrays.asList("灯泡1", "灯泡1", "灯泡1", "灯泡1",
            "灯泡1", "灯泡1", "灯泡1", "灯泡1", "灯泡1", "灯泡1");

    public static final List<String> getData(int number) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            stringList.add(sStringList.get(i % sStringList.size())+" ?");
        }
        return stringList;
    }
}
