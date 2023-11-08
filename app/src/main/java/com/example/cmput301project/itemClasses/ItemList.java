package com.example.cmput301project.itemClasses;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ItemList {
    private ArrayList<Item> items;
    private ArrayList<Item> filteredItems;

    public ItemList(ArrayList<Item> items) {
        this.items = items;
        this.filteredItems = items;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public ArrayList<Item> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(ArrayList<Item> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public void filterByDate(Date from, Date to) {
        List<Item> f = items.stream().filter(item -> item.getPurchaseDate().after(from) && item.getPurchaseDate().before(to)) // Replace this condition with your filtering criteria
                .collect(Collectors.toList());
        filteredItems = new ArrayList<>(f);
    }

    public void filterByKeywords() {
    }

    public void filterByMake() {
    }
}
