package com.designpattern.sigleton;

/**
 * 跟Mgr01 本质上是一样的
 */
public class Mgr02 {

    private static final Mgr02 INSTANCE;

    static {
        INSTANCE = new Mgr02();
    }

    private Mgr02(){}

    public static Mgr02 getINSTANCE() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        Mgr02 m1 = Mgr02.getINSTANCE();
        Mgr02 m2 = Mgr02.getINSTANCE();
        System.out.println(m1 == m2);
    }


}
