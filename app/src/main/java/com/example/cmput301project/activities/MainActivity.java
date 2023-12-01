/**
 * The main activity of the application responsible for displaying all items, their details, and descriptions.
 * Users can perform actions such as adding and deleting items directly from this activity.
 * The UI design is expected to be updated to match the Figma design. The class integrates with the Database
 * to handle item data and updates dynamically. It includes functionality for adding, editing, viewing, and
 * deleting items, as well as applying filters to the displayed item list. The total valuation of items is
 * calculated and displayed. The main UI components include a ListView, buttons for adding, deleting, and
 * filtering items, and a FloatingActionButton for adding new items. Changes in item data trigger updates
 * in the displayed list, and the class supports fragment interactions for adding, editing, and viewing items.
 */



package com.example.cmput301project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cmput301project.Database;
import com.example.cmput301project.R;
import com.example.cmput301project.TotalListener;
import com.example.cmput301project.UserManager;
import com.example.cmput301project.fragments.AddItemFragment;
import com.example.cmput301project.fragments.AddTagsSelectedItemsFragment;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements AddItemFragment.OnFragmentInteractionListener, EditItemFragment.OnFragmentInteractionListener, ViewItemFragment.OnFragmentInteractionListener, ItemFiltersFragment.OnFragmentInteractionListener, AddTagsSelectedItemsFragment.OnFragmentInteractionListener {
    private Database db;
    private ArrayList<Item> items;
    private ItemList itemList;
    private ItemFilter itemFilter;
    private ListView itemsView;
    private TextView totalCostView;
    private Button filtersButton;
    private TotalListener totalListener;
    private ArrayAdapter<Item> itemAdapter;
    private Button deleteButton;
    private Button addTagsSelectedButton;
    private CircleImageView profilePicture;
    private UserManager userManager;

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(getApplicationContext())
                .load(userManager.getUserProfilePicture())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.defaultuser)
                        .error(R.drawable.defaultuser))
                .into(profilePicture);
    }

    /**
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     *
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userManager = UserManager.getInstance();

        items = new ArrayList<Item>();
        itemList = new ItemList(items);
        itemFilter = new ItemFilter();

        itemsView = findViewById(R.id.item_list);
        totalCostView = findViewById(R.id.total_cost);
        filtersButton = findViewById(R.id.filter_items_button);

        profilePicture = findViewById(R.id.profile_image);
        profilePicture.setOnClickListener(v -> {
            navigateToUserProfile();
        });

        Glide.with(getApplicationContext())
                .load(userManager.getUserProfilePicture())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.defaultuser)
                        .error(R.drawable.defaultuser))
                .into(profilePicture);

        deleteButton = findViewById(R.id.delete_items_button);
        deleteButton.setOnClickListener(v -> {
            ((ItemAdapter) itemsView.getAdapter()).deleteSelectedItems();
        });

        addTagsSelectedButton = findViewById(R.id.add_tags_selected_button);
        addTagsSelectedButton.setOnClickListener(v -> {
            if (!((ItemAdapter) itemsView.getAdapter()).getSelectedItems().isEmpty()) {
                AddTagsSelectedItemsFragment tagsSelectedFragment = new AddTagsSelectedItemsFragment((ItemAdapter) itemAdapter);
                Bundle args = new Bundle();
                tagsSelectedFragment.setArguments(args);
                tagsSelectedFragment.show(getSupportFragmentManager(), "ADD_TAGS_SELECTED");
            } else {
                Toast.makeText(this, "You must select at least one item", Toast.LENGTH_SHORT).show();
            }
        });

        totalListener = new TotalListener(0.0, totalCostView);
        itemAdapter = new ItemAdapter(this, items);
        itemsView.setAdapter(itemAdapter);
        final FloatingActionButton addButton = findViewById(R.id.add_item_button);
        addButton.setOnClickListener(v -> {
            new AddItemFragment().show(getSupportFragmentManager(), "ADD_ITEM");
        });

        filtersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemFiltersFragment itemFiltersFragment = new ItemFiltersFragment();
                Bundle args = new Bundle();
                if (!itemFilter.isFilterActive()) {
                    itemList.setUnfilteredItems(new ArrayList<Item>(items));
                    itemList.setFilteredItems(new ArrayList<Item>(items));
                }
                if (itemFilter.isFilterDate()) {
                    DateFormat df = new SimpleDateFormat("mm/dd/yyyy");
                    args.putString("from", df.format(itemFilter.getFrom()));
                    args.putString("to", df.format(itemFilter.getTo()));
                }
                if (itemFilter.isFilterKeywords()) {
                    String[] keywords = itemFilter.getKeywords().toArray(new String[0]);
                    args.putStringArray("keywords", keywords);
                }
                if (itemFilter.isFilterMakes()) {
                    args.putString("make", itemFilter.getMake());
                }
                if (itemFilter.isFilterTag()) {
                    args.putString("tag", itemFilter.getTag());
                }
                itemFiltersFragment.setArguments(args);
                itemFiltersFragment.show(getSupportFragmentManager(), "ITEM_FILTERS");
            }

        });

        db = Database.getInstance();
        db.addArrayAsListener(items, itemAdapter);
        db.addTotalListener(totalListener);
    }

    private void navigateToUserProfile() {
        Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
        startActivity(i);
    }

    /**
     * @param item
     */
    @Override
    public void onOKPressed(Item item) {
        db.addItem(item);
    }

    /**
     *
     */
    @Override
    public void updateTotalCost() { //add up all costs of expenses within list, update display
        totalCostView.setText("Total Valuation $" + String.format("%.2f", totalListener.getTotal()));
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * @param item
     */
    @Override
    public void editItem(Item item) {
        new EditItemFragment(item).show(getSupportFragmentManager(), "EDIT_ITEM");
    }

    /**
     * @param item
     */
    public void viewItem(Item item) {
        new ViewItemFragment(item).show(getSupportFragmentManager(), "VIEW_ITEM");
    }

    /**
     * @param item
     */
    @Override
    public void onDeletePressed(Item item) { //delete selected expense, update display
        db.deleteItem(item);
    }

    /**
     * @param i
     */
    @Override
    public void onFiltersSaved(ItemFilter i) {
        if (i.isFilterActive()) {
            itemFilter = i;
            itemList.filterItems(itemFilter);
            items.clear();
            items.addAll(itemList.getFilteredItems());
            itemAdapter.notifyDataSetChanged();
            updateTotalCost();
        }

    }

    /**
     *
     */
    @Override
    public void onFiltersCleared() {
        itemFilter = new ItemFilter();
        items.clear();
        items.addAll(itemList.getUnfilteredItems());
        itemAdapter.notifyDataSetChanged();
        updateTotalCost();
    }

    /**
     * @param item
     */
    @Override
    public void onItemEdited(Item item) {
        //interfaces can't have same named methods
        //TODO: change way of passing state to the main activity
        db.editItem(item);
    }

    /**
     * Updates total cost after any changes are made
     */
    @Override
    public void updateTotalCostAfterEdit() {
        //interfaces can't have same named methods
        //TODO: change way of passing state to the main activity
        updateTotalCost();
    }
}