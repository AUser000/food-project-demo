package com.example.foodprojectdemo.models;


/**
 * Created by Dhanushka Dharmasena on 03/08/2018.
 */
public class InventoryMetaData {
    public double lat;
    public double lng;
    public String itemId;
    public String inventoryId;

    public InventoryMetaData(double lat, double lng, String itemId, String inventoryId) {
        this.lat = lat;
        this.lng = lng;
        this.itemId = itemId;
        this.inventoryId = inventoryId;
    }

    public InventoryMetaData() {
    }

}
