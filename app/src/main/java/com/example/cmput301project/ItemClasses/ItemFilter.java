package com.example.cmput301project.ItemClasses;

import java.util.ArrayList;
import java.util.Date;

public class ItemFilter {
    private Date from;
    private Date to;
    private ArrayList<String> keywords;
    private ArrayList<String> makes;
    private boolean filterDate = false;
    private boolean filterKeywords = false;
    private boolean filterMake = false;

    public ItemFilter() {
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
        return filterDate;
    }

    public void setFilterDate(boolean filterDate) {
        this.filterDate = filterDate;
    }

    public boolean isFilterKeywords() {
        return filterKeywords;
    }

    public void setFilterKeywords(boolean filterKeywords) {
        this.filterKeywords = filterKeywords;
    }

    public boolean isFilterMake() {
        return filterMake;
    }

    public void setFilterMake(boolean filterMake) {
        this.filterMake = filterMake;
    }
}


