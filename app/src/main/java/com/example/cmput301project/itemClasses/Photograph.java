/**
 * Represents a photograph associated with an item.
 * This class stores the file path of the photograph
 * and provides methods to retrieve and modify the path.
 * Instances of this class are used to link images
 * with corresponding items, enhancing the visual aspect
 * of item representation within the application.
 */


package com.example.cmput301project.itemClasses;

public class Photograph {
    private String path;

    public Photograph(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
