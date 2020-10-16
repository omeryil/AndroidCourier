package com.mubo.postgetapp;

public class TestClass {
    private String a;
    private int b;
    public TestClass()
    {
        super();
    }
    public TestClass(String a, int b) {
        this.a = a;
        this.b = b;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
