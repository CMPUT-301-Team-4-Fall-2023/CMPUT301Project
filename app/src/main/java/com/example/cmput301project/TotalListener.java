package com.example.cmput301project;

import android.widget.TextView;

public class TotalListener {
    private Double total;
    private TextView listenerField;


    public TotalListener(Double total, TextView listenerField) {
        this.total = total;
        this.listenerField = listenerField;
    }


    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void addTotal(Double add){
        this.total += add;
    }

    public void update(){
        this.listenerField.setText("Total Valuation $" + String.format("%.2f", total));
    }
}
