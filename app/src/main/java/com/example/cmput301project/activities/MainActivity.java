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
import com.example.cmput301project.fragments.EditItemFragment;
import com.example.cmput301project.fragments.ItemFiltersFragment;
import com.example.cmput301project.fragments.ViewItemFragment;
import com.example.cmput301project.itemClasses.Item;
import com.example.cmput301project.itemClasses.ItemAdapter;
import com.example.cmput301project.itemClasses.ItemFilter;
import com.example.cmput301project.itemClasses.ItemList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements AddItemFragment.OnFragmentInteractionListener, EditItemFragment.OnFragmentInteractionListener, ViewItemFragment.OnFragmentInteractionListener, ItemFiltersFragment.OnFragmentInteractionListener {

    private ArrayList<Item> items;
    private ItemList itemList;
    private ItemFilter itemFilter;
    private ListView itemsView;
    private TextView totalCostView;
    private Button filtersButton;
    private Double totalCost;
    private ArrayAdapter<Item> itemAdapter;
    private ArrayAdapter<Item> filteredItemAdapter;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items = new ArrayList<Item>();
        itemList = new ItemList(items);
        itemFilter = new ItemFilter();
        itemsView = findViewById(R.id.item_list);
        totalCostView = findViewById(R.id.total_cost);
        filtersButton = findViewById(R.id.filter_items_button);

        deleteButton = findViewById(R.id.delete_items_button);
        deleteButton.setOnClickListener(v -> {
            ((ItemAdapter) itemsView.getAdapter()).deleteSelectedItems();
        });

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
                ItemFiltersFragment itemFiltersFragment = new ItemFiltersFragment();
                Bundle args = new Bundle();
                if (itemFilter.isFilterDate()) {
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                    args.putString("from", df.format(itemFilter.getFrom()));
                    args.putString("to", df.format(itemFilter.getTo()));
                }
                if (itemFilter.isFilterKeywords()) {

                }
                if (itemFilter.isFilterMakes()) {

                }
                itemFiltersFragment.setArguments(args);
                itemFiltersFragment.show(getSupportFragmentManager(), "ITEM_FILTERS");
            }

        });
    }

    @Override
    public void onOKPressed(Item item) {
        items.add(item); //on a new expense addition, add the expense to the datalist and update display
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateTotalCost() { //add up all costs of expenses within list, update display
        totalCost = 0.00;
        for (int i = 0; i < items.size(); i++) {
            totalCost = totalCost + items.get(i).getValue();
        }
        totalCostView.setText("Total Valuation $" + String.format("%.2f", totalCost));
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void editItem(Item item) {
        new EditItemFragment(item).show(getSupportFragmentManager(), "EDIT_ITEM");
    }

    public void viewItem(Item item) {
        new ViewItemFragment(item).show(getSupportFragmentManager(), "VIEW_ITEM");
    }

    @Override
    public void onDeletePressed(Item item) { //delete selected expense, update display
        items.remove(item);
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFiltersSaved(ItemFilter i) {
        this.itemFilter = i;
        if (itemFilter.isFilterDate()) {
            itemList.filterByDate(itemFilter.getFrom(), itemFilter.getTo());
        }
        if (itemFilter.isFilterKeywords()) {
            itemList.filterByKeywords();
        }
        if (itemFilter.isFilterMakes()) {
            itemList.filterByMake();
        }
        items.clear();
        items.addAll(itemList.getFilteredItems());
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFiltersCleared() {
        items.clear();
        items.addAll(itemList.getItems());
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemEdited(Item item) {
        //interfaces can't have same named methods
        //TODO: change way of passing state to the main activity
        items.add(item);
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateTotalCostAfterEdit() {
        //interfaces can't have same named methods
        //TODO: change way of passing state to the main activity
        updateTotalCost();
    }
    
    private void deleteSelectedItems() {
        ArrayList<Item> selected = new ArrayList<>();
        for (Item item : items) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }
        items.removeAll(selected);
        itemAdapter.notifyDataSetChanged();
    }

    public void setItems(ArrayList<Item> sampleItems) {
    }
}