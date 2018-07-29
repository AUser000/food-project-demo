package com.example.foodprojectdemo.models;

public class SpinnerItem {

    private String key;
    private String name;

    public SpinnerItem(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SpinnerItem) {
            SpinnerItem spinnerItem = (SpinnerItem) obj;
            if (spinnerItem.getName().equals(name) && spinnerItem.getKey() == key) {
                return true;
            }
        }
        return false;
    }
}
