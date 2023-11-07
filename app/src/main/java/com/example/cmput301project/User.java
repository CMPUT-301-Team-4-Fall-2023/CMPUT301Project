package com.example.cmput301project;

import com.example.cmput301project.itemClasses.ItemList;

public class User {
    private String username;
    private String password;
    private ItemList itemList;

    public User(String username, String password, ItemList itemList) {
        this.username = username;
        this.password = password;
        this.itemList = itemList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ItemList getItemList() {
        return itemList;
    }

    public void setItemList(ItemList itemList) {
        this.itemList = itemList;
    }
}
