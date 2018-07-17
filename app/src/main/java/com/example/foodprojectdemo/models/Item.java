package com.example.foodprojectdemo.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Item {

    public String name;
    public String category;

    public Item() {
    }

    public Item(String name, String category) {
        this.name = name;
        this.category = category;
    }
}
