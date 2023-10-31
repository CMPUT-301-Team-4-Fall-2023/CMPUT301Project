package com.example.cmput301project;

import java.util.ArrayList;

public class ItemList {
    private ArrayList<Item> items;

    public ItemList(ArrayList<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Integer index) {
        items.remove(index);
    }
}
