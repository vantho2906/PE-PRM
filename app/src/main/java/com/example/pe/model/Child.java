package com.example.pe.model;

import java.io.Serializable;

public class Child implements Serializable {
    private int id;
    private String field1;
    private String field2;
    private String field3;
    private int idParent;

    public Child() {
    }

    public Child(int id, String field1, String field2, String field3, int idParent) {
        this.id = id;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.idParent = idParent;
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

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public int getIdParent() {
        return idParent;
    }

    public void setIdParent(int idParent) {
        this.idParent = idParent;
    }
}
