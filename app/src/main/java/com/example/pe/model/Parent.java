package com.example.pe.model;

import java.io.Serializable;

public class Parent implements Serializable {
    private int id;
    private String field1;

    public Parent() {
    }

    public Parent(int id, String field1) {
        this.id = id;
        this.field1 = field1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }
}
