package TestMath;

import java.util.ArrayList;

public class TestCollection {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        list.add("1");
        list.add("3");
        list.add("2");
        list.add(2,"4");
        //输出结果  1 3 4 2
        System.out.println(list.toString());
        list.set(0,"5");
        //输出结果  5 3 4 2
        System.out.println(list.toString());
    }
}

