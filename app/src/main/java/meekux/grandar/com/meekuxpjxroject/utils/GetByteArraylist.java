package meekux.grandar.com.meekuxpjxroject.utils;

import java.util.ArrayList;

/**
 * Created by liuqk on 2017/5/28.
 */

public class GetByteArraylist {

    private static int length;
    private static byte[] bytes;
    private static byte[] bytes1;
    private static ArrayList<byte[]> bytes2;

    public static ArrayList<byte[]> getbytearraylist(String string){
        if (string.length() > 70 &&string.lastIndexOf("]")==string.length()-1) {
            String[] split = string.split("-86");
            length = split.length;
            bytes2 = new ArrayList<>();
            for (int i = 1; i < length; i++) {
                String s = split[i];

                byte[] bytes = getbytearray(s);
                    bytes2.add(bytes);

            }
            return bytes2;

        }

        return bytes2;
    }
    public static byte[] getbytearray(String string) {
        int length = string.length();
        int i2 = string.lastIndexOf("[");
        int i3 = string.lastIndexOf("]");
        if ((length-1) == i2){
            String substring = string.substring(1, length - 2);
            String replace = substring.replace(" ", "");
            String[] split1 = replace.split(",");
            int length1 = split1.length;
            bytes = new byte[length1];
            for (int i = 0; i <= length1 - 1; i++) {
                String s = split1[i];
                int intstr = Integer.parseInt(s);
                bytes[i] = (byte) intstr;
            }
            return bytes;

        } else if ((length-1) == i3) {
            String substring = string.substring(1, length - 1);
            String replace = substring.replace(" ", "");
            String[] splitstring = replace.split(",");
            int lengt = splitstring.length;
            bytes = new byte[lengt];
            for (int i=0;i<lengt;i++) {
                String s = splitstring[i];
                int i1 = Integer.parseInt(s);
                bytes[i] = (byte) i1;
            }
            return bytes;

        }
        return bytes;
    }

}
//    int length = split.length;
//    String str1 = str.substring(1, str.length() - 1);
//    String str2 = str1.replace(" ","");
//
//    String[] substr = str2.split(",");
//    int len  = substr.length;
//    byte[] b = new byte[len];
//        for(int i = 0;i <=len-1;i++){
//                String s = substr[i];
//                int intstr = Integer.parseInt(s);
//                b[i] = (byte) intstr;
//                }
//                Log.d("nihao", "nhao" + b);
//
//                }