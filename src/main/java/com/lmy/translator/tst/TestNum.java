package com.lmy.translator.tst;

public class TestNum {
    public static int num0;

    public static int num1;

    public static void setNum(int num00, int num11){
        num0 = num00;
        num1 = num11;
    }

    public static int getNum() {

        return num1-num0;
    }
}
