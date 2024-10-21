package com.example.myapplication.model;

public class MyLedgerData {

    private String id;
    private String name;
    private String image;
    private boolean isDefault;

    public MyLedgerData(String id, String name, String image, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isDefault = isDefault;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

}
