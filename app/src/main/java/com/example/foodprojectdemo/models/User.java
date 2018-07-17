package com.example.foodprojectdemo.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String firstName;
    public String lastName;
    public String email;
    public String type;

    public User() {
    }

    public User(String firstName, String lastName, String email, String type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.type = type;
    }
}
