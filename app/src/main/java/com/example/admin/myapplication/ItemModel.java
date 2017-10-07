package com.example.admin.myapplication;

/**
 * Created by Admin on 10/6/2017.
 */

public class ItemModel {
    String name,gender;
    public ItemModel(String name,String gender){
        this.name=name;
        this.gender=gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
