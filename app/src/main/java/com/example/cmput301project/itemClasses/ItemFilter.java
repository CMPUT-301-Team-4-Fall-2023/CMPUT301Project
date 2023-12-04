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
/**
 * Filter object designed to specify criteria for item filtering.
 * The filter stores attributes such as date range, keywords, and makes,
 * allowing users to narrow down the items based on specific conditions.
 * It includes methods to check the presence of filtering criteria for
 * dates, keywords, makes, and tags. This class facilitates the customization
 * of item queries by encapsulating various filter parameters within an
 * easily manageable structure.
 */
public class ItemFilter {
    private Date from;
    private Date to;
    private ArrayList<String> keywords;
    private String make;
    private String tag;

    /**
     * Constructs an empty ItemFilter with default values.
     * The initial values for date range, keywords, make, and tag are set to null or an empty list.
     */
    public ItemFilter() {
        from = null;
        to = null;
        keywords = new ArrayList<String>();
        make = null;
        tag = null;
    }
    /**
     * Gets the start date of the filter.
     *
     * @return The start date of the filter.
     */

    public Date getFrom() {
        return from;
    }

    /**
     * Sets the start date of the filter.
     *
     * @param from The start date of the filter.
     */
    public void setFrom(Date from) {
        this.from = from;
    }

    /**
     * Gets the end date of the filter.
     *
     * @return The end date of the filter.
     */
    public Date getTo() {
        return to;
    }

    /**
     * Sets the end date of the filter.
     *
     * @param to The end date of the filter.
     */
    public void setTo(Date to) {
        this.to = to;
    }

    /**
     * Gets the list of keywords in the filter.
     *
     * @return The list of keywords in the filter.
     */
    public ArrayList<String> getKeywords() {
        return keywords;
    }

    /**
     * Adds a keyword to the list of keywords in the filter.
     *
     * @param keyword The keyword to be added.
     */
    public void addKeyword(String keyword) {
        keywords.add(keyword);
    }

    /**
     * Gets the make in the filter.
     *
     * @return The make in the filter.
     */
    public String getMake() {
        return make;
    }

    /**
     * Sets the make in the filter.
     *
     * @param make The make to be set in the filter.
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Gets the tag in the filter.
     *
     * @return The tag in the filter.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the tag in the filter.
     *
     * @param tag The tag to be set in the filter.
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Checks if the filter includes date range criteria.
     *
     * @return True if date range criteria are set, false otherwise.
     */
    public boolean isFilterDate() {
        return from != null && to != null;
    }

    /**
     * Checks if the filter includes keyword criteria.
     *
     * @return True if keyword criteria are set, false otherwise.
     */
    public boolean isFilterKeywords() {
        return !keywords.isEmpty();
    }

    /**
     * Checks if the filter includes make criteria.
     *
     * @return True if make criteria are set, false otherwise.
     */
    public boolean isFilterMakes() {
        return make != null;
    }

    /**
     * Checks if the filter includes tag criteria.
     *
     * @return True if tag criteria are set, false otherwise.
     */
    public boolean isFilterTag() {
        return tag != null;
    }

    /**
     * Checks if any filtering criteria are active in the filter.
     *
     * @return True if any criteria are set, false if the filter is empty.
     */
    public boolean isFilterActive() {return isFilterDate() || isFilterKeywords() || isFilterMakes() || isFilterTag();}
}