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
/**
 * Container class for managing a list of Item objects and facilitating filtering operations.
 * The ItemList class holds an ArrayList of items and provides methods for retrieving,
 * setting, and filtering items based on criteria such as date range, keywords, and make.
 * Filtering methods modify a separate list of filtered items, allowing for dynamic updates
 * to the displayed items. This class is intended to be used to organize and manipulate
 * Item data within the context of the application's functionality.
 */
public class ItemList {
    private ArrayList<Item> unfilteredItems;
    private ArrayList<Item> filteredItems;

    /**
     * Constructs an ItemList with the provided list of items.
     * The unfilteredItems and filteredItems lists are initialized with a copy of the provided items.
     *
     * @param items The list of items to be managed by this ItemList.
     */
    public ItemList(ArrayList<Item> items) {
        this.unfilteredItems = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>(items);
    }

    /**
     * Gets the list of unfiltered items.
     *
     * @return The list of unfiltered items.
     */
    public ArrayList<Item> getUnfilteredItems() {
        return unfilteredItems;
    }

    /**
     * Sets the list of unfiltered items.
     *
     * @param items The list of items to set as unfiltered.
     */
    public void setUnfilteredItems(ArrayList<Item> items) {
        unfilteredItems = items;
    }

    /**
     * Gets the list of filtered items.
     *
     * @return The list of filtered items.
     */
    public ArrayList<Item> getFilteredItems() {
        return filteredItems;
    }

    /**
     * Sets the list of filtered items.
     *
     * @param filteredItems The list of items to set as filtered.
     */
    public void setFilteredItems(ArrayList<Item> filteredItems) {
        this.filteredItems = filteredItems;
    }

    /**
     * Filters items based on the provided date range.
     *
     * @param from The start date of the filter.
     * @param to   The end date of the filter.
     */
    private void filterByDate(Date from, Date to) {

        List<Item> f = filteredItems.stream().filter(item ->
                (item.getPurchaseDate().equals(from) || item.getPurchaseDate().after(from))
                        && (item.getPurchaseDate().equals(to) || item.getPurchaseDate().before(to))) // Replace this condition with your filtering criteria
                .collect(Collectors.toList());
        filteredItems = new ArrayList<>(f);
    }

    /**
     * Filters items based on the provided list of keywords.
     *
     * @param keywords The list of keywords to filter by.
     */
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

    /**
     * Filters items based on the provided make.
     *
     * @param make The make to filter by.
     */
    private void filterByMake(String make) {
        List<Item> f = filteredItems.stream().filter(item -> item.getMake().toLowerCase().equals(make.toLowerCase())) // Replace this condition with your filtering criteria
                .collect(Collectors.toList());
        filteredItems = new ArrayList<>(f);
    }

    /**
     * Filters items based on the provided tag.
     *
     * @param tag The tag to filter by.
     */
    private void filterByTag(String tag) {
        List<Item> f = filteredItems.stream().filter(item -> {
                    if(item.getTags() == null) {return false;}
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

    /**
     * Filters the items based on the criteria specified in the provided ItemFilter.
     *
     * @param itemFilter The ItemFilter containing criteria for filtering items.
     */
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
