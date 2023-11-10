/**
 * Filter object designed to specify criteria for item filtering.
 * The filter stores attributes such as date range, keywords, and makes,
 * allowing users to narrow down the items based on specific conditions.
 * It includes methods to check the presence of filtering criteria for
 * dates, keywords, and makes. This class facilitates the customization
 * of item queries by encapsulating various filter parameters within an
 * easily manageable structure. Users can employ instances of this class
 * to define and apply filters when searching for items in the application.
 */


package com.example.cmput301project.itemClasses;

import java.util.ArrayList;
import java.util.Date;

public class ItemFilter {
    private Date from;
    private Date to;
    private ArrayList<String> keywords;
    private ArrayList<String> makes;

    public ItemFilter() {
        from = null;
        to = null;
        keywords = new ArrayList<String>();
        makes = new ArrayList<String>();
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public ArrayList<String> getMakes() {
        return makes;
    }

    public void setMakes(ArrayList<String> makes) {
        this.makes = makes;
    }

    public boolean isFilterDate() {
        return from != null && to != null;
    }

    public boolean isFilterKeywords() {
        return !keywords.isEmpty();
    }
    public boolean isFilterMakes() {
        return !makes.isEmpty();
    }
}


