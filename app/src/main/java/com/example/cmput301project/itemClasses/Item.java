package com.example.cmput301project.itemClasses;

import java.util.ArrayList;
import java.util.Date;

public class Item {
    private String name;
    private Date purchaseDate;
    private String description;
    private String make;
    private String model;
    private Integer serialNumber;
    private Double value;
    private String comment;
    private ArrayList<Tag> tags;
    private ArrayList<Photograph> photographs;
    private boolean selected;

    public Item() {}

    public Item(String name, Date purchaseDate, String description, String make, String model, Integer serialNumber, Double value, String comment) {
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.value = value;
        this.comment = comment;
        this.tags = tags;
        this.photographs = photographs;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tag);
    }
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }
    public void clearTags() {
        // Clear current tags
        this.tags.clear();
    }
    public ArrayList<Photograph> getPhotographs() {
        return photographs;
    }

    public void setPhotographs(ArrayList<Photograph> photographs) {
        this.photographs = photographs;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
