/**
 * Container class for managing a list of Item objects and facilitating filtering operations.
 * The ItemList class holds an ArrayList of items and provides methods for retrieving,
 * setting, and filtering items based on criteria such as date range, keywords, and make.
 * Filtering methods modify a separate list of filtered items, allowing for dynamic updates
 * to the displayed items. This class is intended to be used to organize and manipulate
 * Item data within the context of the application's functionality.
 */


package com.example.cmput301project.itemClasses;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ItemList {
    private ArrayList<Item> unfilteredItems;
    private ArrayList<Item> filteredItems;

    public ItemList(ArrayList<Item> items) {
        this.unfilteredItems = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>(items);
    }

    public ArrayList<Item> getUnfilteredItems() {
        return unfilteredItems;
    }

    public void setUnfilteredItems(ArrayList<Item> items) {
        unfilteredItems = items;
    }

    public ArrayList<Item> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(ArrayList<Item> filteredItems) {
        this.filteredItems = filteredItems;
    }

    private void filterByDate(Date from, Date to) {
        List<Item> f = filteredItems.stream().filter(item -> item.getPurchaseDate().after(from) && item.getPurchaseDate().before(to)) // Replace this condition with your filtering criteria
                .collect(Collectors.toList());
        filteredItems = new ArrayList<>(f);
    }

    private void filterByKeywords(ArrayList<String> keywords) {
        List<Item> f = filteredItems.stream().filter(item -> {
                    for (int i = 0; i < keywords.size(); i++) {
                        if (!item.getDescription().toLowerCase().contains(keywords.get(i).toLowerCase())) {
                            return false;
                        }
                    }
                    return true;
                }) // Replace this condition with your filtering criteria
                .collect(Collectors.toList());
        filteredItems = new ArrayList<>(f);

    }

    private void filterByMake(String make) {
        List<Item> f = filteredItems.stream().filter(item -> item.getMake().toLowerCase().equals(make.toLowerCase())) // Replace this condition with your filtering criteria
                .collect(Collectors.toList());
        filteredItems = new ArrayList<>(f);
    }

    private void filterByTag(String tag) {
        List<Item> f = filteredItems.stream().filter(item -> {
                    for (int i = 0; i < item.getTags().size(); i++) {
                        if (item.getTags().size() == 0) {
                            return false;
                        }
                        if (item.getTags()
                                .get(i)
                                .getName()
                                .toLowerCase().
                                equals(tag.toLowerCase())
                        ) {
                            return true;
                        }
                    }
                    return false;
                }) // Replace this condition with your filtering criteria
                .collect(Collectors.toList());
        filteredItems = new ArrayList<>(f);
    }

    public void filterItems(ItemFilter itemFilter) {
        filteredItems = new ArrayList<>(unfilteredItems);
        if (itemFilter.isFilterDate()) {
            filterByDate(itemFilter.getFrom(), itemFilter.getTo());
        }
        if (itemFilter.isFilterKeywords()) {
            filterByKeywords(itemFilter.getKeywords());
        }
        if (itemFilter.isFilterMakes()) {
            filterByMake(itemFilter.getMake());
        }
        if (itemFilter.isFilterTag()) {
            filterByTag(itemFilter.getTag());
        }
    }
}
