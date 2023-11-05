package com.example.cmput301project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddItemFragment.OnFragmentInteractionListener, ViewItemFragment.OnFragmentInteractionListener {
    private ArrayList<Item> items;
    private ListView itemsView;
    private TextView totalCostView;
    private Double totalCost;
    private ArrayAdapter<Item> itemAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items = new ArrayList<Item>();
        itemsView = findViewById(R.id.item_list);
        totalCostView = findViewById(R.id.total_cost);
        totalCost = 0.00; //initialize costs to 0 on startup
        itemAdapter = new ItemList(this, items);
        itemsView.setAdapter(itemAdapter);
        final FloatingActionButton addButton = findViewById(R.id.add_item_button);
        addButton.setOnClickListener(v -> {
            new AddItemFragment().show(getSupportFragmentManager(), "ADD_ITEM"); //add floating + button to add new expense
        });
    }
    @Override
    public void onOKPressed(Item item) {
        items.add(item); //on a new expense addition, add the expense to the datalist and update display
        itemAdapter.notifyDataSetChanged();
    }
    @Override
    public void updateTotalCost(){ //add up all costs of expenses within list, update display
        totalCost = 0.00;
        for (int i = 0; i < items.size(); i++) {
            totalCost = totalCost + items.get(i).getValue();
        }
        totalCostView.setText("Total Valuation $" + String.format("%.2f", totalCost));
        itemAdapter.notifyDataSetChanged();
    }
    @Override
    public void editItem(Item item){
        new AddItemFragment(item).show(getSupportFragmentManager(), "EDIT_ITEM");
    }
    public void viewItem(Item item){
        new ViewItemFragment(item).show(getSupportFragmentManager(), "VIEW_ITEM");
    }
    @Override
    public void onDeletePressed(Item item) { //delete selected expense, update display
        items.remove(item);
        itemAdapter.notifyDataSetChanged();
    }
}