package com.mobilecourse.backend;

public class Test2 {
    public String name;
    public int id;

    public Test2() {
        this.name = "anonymous";
        this.id = 0;
    }

    public Test2(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}