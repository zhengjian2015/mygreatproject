package com.liaoxuefeng.annos;

public class Person {
    @Range(min=1, max=20)
    public String name;

    @Range(max=10)
    public String city;
}

