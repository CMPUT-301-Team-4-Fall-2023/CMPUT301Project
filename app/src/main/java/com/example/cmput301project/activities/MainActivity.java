package com.example.cmput301project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cmput301project.R;
import com.example.cmput301project.fragments.AddItemFragment;
import com.example.cmput301project.fragments.ItemFiltersFragment;
import com.example.cmput301project.fragments.ViewItemFragment;
import com.example.cmput301project.itemClasses.Item;
import com.example.cmput301project.itemClasses.ItemAdapter;
import com.example.cmput301project.itemClasses.ItemFilter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements AddItemFragment.OnFragmentInteractionListener, ViewItemFragment.OnFragmentInteractionListener, ItemFiltersFragment.OnFragmentInteractionListener {
    private ArrayList<Item> items;
    private ListView itemsView;
    private TextView totalCostView;
    private Button filtersButton;
    private Double totalCost;
    private ArrayAdapter<Item> itemAdapter;
    private ArrayAdapter<Item> filteredItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items = new ArrayList<Item>();
        itemsView = findViewById(R.id.item_list);
        totalCostView = findViewById(R.id.total_cost);
        filtersButton = findViewById(R.id.filter_items_button);

        totalCost = 0.00; //initialize costs to 0 on startup
        itemAdapter = new ItemAdapter(this, items);
        itemsView.setAdapter(itemAdapter);
        final FloatingActionButton addButton = findViewById(R.id.add_item_button);
        addButton.setOnClickListener(v -> {
            new AddItemFragment().show(getSupportFragmentManager(), "ADD_ITEM"); //add floating + button to add new expense
        });

        filtersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ItemFiltersFragment().show(getSupportFragmentManager(), "ITEM_FILTERS");
            }

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
    @Override
    public void onFiltersSaved(ItemFilter itemFilter) {
        ArrayList<Item> filteredItems = new ArrayList<>();
    }

    public ArrayList<Item> filterByDate(Date from, Date to) {
        List<Item> filteredItems = items.stream()
                .filter(item -> item.getPurchaseDate().after(from) && item.getPurchaseDate().before(to)) // Replace this condition with your filtering criteria
                .collect(Collectors.toList());
        ArrayList<Item> filteredItemsArrayList = new ArrayList<>(filteredItems);
        return filteredItemsArrayList;
    }
    public ArrayList<Item> filterByKeywords() {
        return items;
    }

    public ArrayList<Item> filterByMake() {
        return items;
    }

}