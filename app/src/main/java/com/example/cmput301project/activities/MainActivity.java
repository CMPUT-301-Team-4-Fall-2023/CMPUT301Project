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

// Import statements

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
import com.example.cmput301project.fragments.SortItemsFragment;
import com.example.cmput301project.fragments.ViewItemFragment;
import com.example.cmput301project.itemClasses.Item;
import com.example.cmput301project.itemClasses.ItemAdapter;
import com.example.cmput301project.itemClasses.ItemFilter;
import com.example.cmput301project.itemClasses.ItemList;
import com.example.cmput301project.itemClasses.Tag;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements AddItemFragment.OnFragmentInteractionListener, EditItemFragment.OnFragmentInteractionListener, ViewItemFragment.OnFragmentInteractionListener, ItemFiltersFragment.OnFragmentInteractionListener, AddTagsSelectedItemsFragment.OnFragmentInteractionListener, SortItemsFragment.OnFragmentInteractionListener {

    // Member variable declaration
    private Database db;
    private ArrayList<Item> items;
    private ItemList itemList;
    private ItemFilter itemFilter;
    private ListView itemsView;
    private TextView totalCostView;
    private Button filtersButton;
    private Button sortButton;
    private Object sortRadioTag;
    private String sortTagString;
    private TotalListener totalListener;
    private ArrayAdapter<Item> itemAdapter;
    private Button deleteButton;
    private Button addTagsSelectedButton;
    private CircleImageView profilePicture;
    private UserManager userManager;

    /**
     * onResume method is overridden to refresh the user profile picture
     * when the MainActivity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(getApplicationContext()).load(userManager.getUserProfilePicture()).apply(new RequestOptions().placeholder(R.drawable.defaultuser).error(R.drawable.defaultuser)).into(profilePicture);
    }

    /**
     * Overrides the onCreate method to set up the main activity.
     * Initializes UI elements, listeners, Database, and item adapters.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, contains the data it
     *                           most recently supplied in onSaveInstanceState.
     *                           Otherwise, it is null.
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
        sortButton = findViewById(R.id.sort_items_button);

        profilePicture = findViewById(R.id.profile_image);
        profilePicture.setOnClickListener(v -> {
            navigateToUserProfile();
        });

        Glide.with(getApplicationContext()).load(userManager.getUserProfilePicture()).apply(new RequestOptions().placeholder(R.drawable.defaultuser).error(R.drawable.defaultuser)).into(profilePicture);

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
        deleteButton = findViewById(R.id.delete_items_button);
        deleteButton.setOnClickListener(v -> {
            ((ItemAdapter) itemsView.getAdapter()).deleteSelectedItems();
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
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
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

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortItemsFragment sortItemsFragment = new SortItemsFragment();

                if (sortRadioTag != null) {
                    Bundle args = new Bundle();
                    args.putSerializable("tagObject", (Serializable) sortRadioTag);
                    if (sortRadioTag.toString().equals("TAG")) {
                        args.putString("tagString", sortTagString);
                    }
                    sortItemsFragment.setArguments(args);
                }
                sortItemsFragment.show(getSupportFragmentManager(), "SORT_ITEMS");
            }
        });

        db = Database.getInstance();
        db.addArrayAsListener(items, itemAdapter);
        db.addTotalListener(totalListener);
    }

    /**
     * Navigates to the user profile activity when the profile picture is clicked.
     */
    private void navigateToUserProfile() {
        Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
        startActivity(i);
    }

    /**
     * Handles the user's response to adding a new item.
     *
     * @param item The item to be added.
     */
    @Override
    public void onOKPressed(Item item) {
        db.addItem(item);
    }

    /**
     * Updates the total cost display after any changes are made.
     */
    @Override
    public void updateTotalCost() { //add up all costs of expenses within list, update display
        totalCostView.setText("Total Valuation $" + String.format("%.2f", totalListener.getTotal()));
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Initiates the editing of a specific item.
     *
     * @param item The item to be edited.
     */
    @Override
    public void editItem(Item item) {
        new EditItemFragment(item).show(getSupportFragmentManager(), "EDIT_ITEM");
    }

    /**
     * Initiates the viewing of a specific item.
     *
     * @param item The item to be viewed.
     */
    public void viewItem(Item item) {
        new ViewItemFragment(item).show(getSupportFragmentManager(), "VIEW_ITEM");
    }

    /**
     * Handles the user's request to delete a specific item.
     *
     * @param item The item to be deleted.
     */
    @Override
    public void onDeletePressed(Item item) { //delete selected expense, update display
        db.deleteItem(item);
    }

    /**
     * Handles the user's response to saving item filters.
     *
     * @param i The ItemFilter containing filter criteria.
     */
    @Override
    public void onFiltersSaved(ItemFilter i) {
        itemFilter = i;
        itemList.filterItems(itemFilter);
        items.clear();
        items.addAll(itemList.getFilteredItems());
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Handles the user's response to saving the selected radio button for sorting.
     *
     * @param tag       The tag associated with the selected radio button.
     * @param tagString The tag string associated with the selected radio button (if applicable).
     */
    @Override
    public void onRadioButtonSaved(Object tag, String tagString) {
        sortRadioTag = tag;
        switch (tag.toString()) {
            case "DATE_OLDEST":
                sortByDate(true);
                break;
            case "DATE_NEWEST":
                sortByDate(false);
                break;
            case "PRICE_LOWEST":
                sortByPrice(true);
                break;
            case "PRICE_HIGHEST":
                sortByPrice(false);
                break;
            case "MAKE_AtoZ":
                sortByMake(true);
                break;
            case "MAKE_ZtoA":
                sortByMake(false);
                break;
            case "DESCRIPTION_AtoZ":
                sortByDescription(true);
                break;
            case "DESCRIPTION_ZtoA":
                sortByDescription(false);
                break;
            case "TAG":
                sortTagString = tagString;
                sortByTag(tagString);
                break;
            default:
                break;
        }
    }

    /**
     * Handles the user's request to clear item filters.
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
     * Handles the user's response to editing a specific item.
     *
     * @param item The item to be edited.
     */
    @Override
    public void onItemEdited(Item item) {
        // Interfaces can't have same named methods
        //TODO: change way of passing state to the main activity
        db.editItem(item, () -> {
            // This code will run after the item is successfully edited
            if (itemFilter.isFilterActive()) {
                onFiltersSaved(itemFilter);
            }
        });
    }

    /**
     * Updates total cost after any changes are made
     */
    @Override
    public void updateTotalCostAfterEdit() {
        // Interfaces can't have same named methods
        //TODO: change way of passing state to the main activity
        updateTotalCost();
    }

    /**
     * Bulk delete function, deletes all selected items
     */
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

    /**
     * Sorts the items by date in either ascending or descending order.
     *
     * @param ascending True for ascending order, false for descending order.
     */
    public void sortByDate(boolean ascending) {
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return ascending ? item1.getPurchaseDate().compareTo(item2.getPurchaseDate()) : item2.getPurchaseDate().compareTo(item1.getPurchaseDate());
            }
        });
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Sorts the items by price in either ascending or descending order.
     *
     * @param ascending True for ascending order, false for descending order.
     */
    public void sortByPrice(boolean ascending) {
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return ascending ? item1.getValue().compareTo(item2.getValue()) : item2.getValue().compareTo(item1.getValue());
            }
        });
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Sorts the items by make in either ascending or descending order.
     *
     * @param ascending True for ascending order, false for descending order.
     */
    public void sortByMake(boolean ascending) {
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return ascending ? item1.getMake().compareTo(item2.getMake()) : item2.getMake().compareTo(item1.getMake());
            }
        });
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Sorts the items by description in either ascending or descending order.
     *
     * @param ascending True for ascending order, false for descending order.
     */
    public void sortByDescription(boolean ascending) {
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return ascending ? item1.getDescription().compareTo(item2.getDescription()) : item2.getDescription().compareTo(item1.getDescription());
            }
        });
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Sorts the items by a specified tag.
     *
     * @param tagString The tag string to be used for sorting.
     */
    public void sortByTag(String tagString) {
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                boolean hasTag1 = false;
                for (Tag t : item1.getTags()) {
                    if (t.getName().equals(tagString)) {
                        hasTag1 = true;
                    }
                }

                boolean hasTag2 = false;
                for (Tag t : item2.getTags()) {
                    if (t.getName().equals(tagString)) {
                        hasTag2 = true;
                    }
                }
                // Put items with the specified tag at the top
                if (hasTag1 && !hasTag2) {
                    return -1; // item1 comes first
                } else if (!hasTag1 && hasTag2) {
                    return 1;  // item2 comes first
                } else {
                    // For items with the same tag status, maintain their original order
                    return 0;
                }
            }
        });
        itemAdapter.notifyDataSetChanged();
    }
}