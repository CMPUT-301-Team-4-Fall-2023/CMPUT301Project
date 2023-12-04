/**
 * The TotalListener class is designed to handle and update a total value, providing a convenient
 * way to observe changes and update a corresponding TextView in an Android application.
 * <p>
 * This class is typically used in conjunction with Firestore listeners to monitor and calculate
 * the total valuation of items dynamically.
 * <p>
 * Usage:
 * - Create an instance of TotalListener, providing an initial total value and a TextView for updates.
 * - Use the provided methods to get, set, add to, and update the total value.
 * - The update method automatically formats and updates the associated TextView with the new total value.
 * <p>
 * Note: Ensure that the provided TextView is initialized before using the TotalListener.
 */

package com.example.cmput301project;

import android.widget.TextView;

public class TotalListener {
    private Double total;
    private TextView listenerField;

    /**
     * Constructs a TotalListener with an initial total value and a TextView for updates.
     *
     * @param total         The initial total value.
     * @param listenerField The TextView to be updated with the total value.
     */
    public TotalListener(Double total, TextView listenerField) {
        this.total = total;
        this.listenerField = listenerField;
    }

    /**
     * Retrieves the current total value.
     *
     * @return The current total value.
     */
    public Double getTotal() {
        return total;
    }

    /**
     * Sets the total value to a specified value.
     *
     * @param total The new total value.
     */
    public void setTotal(Double total) {
        this.total = total;
    }

    /**
     * Adds a specified value to the current total.
     *
     * @param add The value to be added to the total.
     */
    public void addTotal(Double add) {
        this.total += add;
    }

    /**
     * Updates the associated TextView with the formatted total value.
     * The total value is displayed as "Total Valuation $X.XX".
     */
    public void update() {
        this.listenerField.setText("Total Valuation $" + String.format("%.2f", total));
    }
}
