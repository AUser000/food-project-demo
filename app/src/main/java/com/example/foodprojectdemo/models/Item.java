package com.example.foodprojectdemo.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Item {

    public String name;
    public String categoryId;

    public Item() {
    }

    public Item(String name, String categoryId) {
        this.name = name;
        this.categoryId = categoryId;
    }
}
