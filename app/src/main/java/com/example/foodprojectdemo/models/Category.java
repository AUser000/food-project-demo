package com.example.foodprojectdemo.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Dhanushka Dharmasena on 23/07/2018.
 */
@IgnoreExtraProperties
public class Category {

    public String name;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

}
