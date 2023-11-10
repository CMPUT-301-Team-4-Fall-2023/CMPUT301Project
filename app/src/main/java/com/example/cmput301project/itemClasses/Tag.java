/**
 * Represents a tag applied to an item.
 * Tags are used to categorize and organize items
 * based on specific attributes. Each tag has a name,
 * and instances of this class provide methods to
 * retrieve and modify the tag name.
 */

package com.example.cmput301project.itemClasses;

public class Tag {
    private String name;

    public Tag() {}
    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
