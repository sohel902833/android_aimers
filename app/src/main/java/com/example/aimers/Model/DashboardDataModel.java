package com.example.aimers.Model;

public class DashboardDataModel {
    String title,description,type;
    int image;

    public DashboardDataModel(String title, String description, int image,String type) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.type=type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
