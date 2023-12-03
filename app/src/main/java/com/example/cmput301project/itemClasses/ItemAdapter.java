/**
 * Custom ArrayAdapter for managing the display of Item objects in a ListView.
 * This adapter handles the visualization of item data, including name, date, cost,
 * description, serial number, model, make, comment, and tags. It provides a custom
 * view for each item in the associated ListView, allowing the user to interact with
 * and manipulate individual items. The adapter supports the selection of items through
 * checkboxes, enabling batch operations. It also integrates buttons for editing and
 * viewing item details directly from the displayed list. The deletion of selected items
 * is facilitated through the 'deleteSelectedItems' method. The class ensures proper
 * handling of item data and user interactions within the context of a ListView.
 */


package com.example.cmput301project.itemClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmput301project.Database;
import com.example.cmput301project.R;
import com.example.cmput301project.activities.MainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemAdapter extends ArrayAdapter<Item> {
    private ArrayList<Item> items;
    private Context context;
    private Set<Item> selectedItems;

    public ItemAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
        this.selectedItems = new HashSet<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_content, parent, false);
        }

        Item item = items.get(position);

        //sets display data of items displayed in the items list (without comment)
        TextView itemName = view.findViewById(R.id.item_name);
        TextView itemMonth = view.findViewById(R.id.item_date);
        TextView itemCharge = view.findViewById(R.id.item_cost);

        itemName.setText(item.getName());
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String newDateString = df.format(item.getPurchaseDate());
        itemMonth.setText(newDateString);
        itemCharge.setText("$" + String.format("%.2f", item.getValue()));

        CheckBox checkBox = view.findViewById(R.id.checkbox); // Assuming checkbox ID is 'checkbox' in your item_content.xml
        checkBox.setChecked(selectedItems.contains(item));
        checkBox.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                selectedItems.add(item);
            } else {
                selectedItems.remove(item);
            }
        });

        Button editButton = view.findViewById(R.id.edit_item_button);
        Button viewButton = view.findViewById(R.id.view_item_button);

        editButton.setOnClickListener(v -> ((MainActivity) context).editItem(item));
        viewButton.setOnClickListener(v -> ((MainActivity) context).viewItem(item));

        return view;
    }

    /**
     * Bulk delete function, deletes all selected items
     */
    public void deleteSelectedItems() {
        Database db = Database.getInstance(); //hack: remove
        for (Item item : selectedItems) {
            db.deleteItem(item);
        }
        selectedItems.clear();
    }

    public Set<Item> getSelectedItems() {
        return selectedItems;
    }


    /**
     * Clears the selection of items.
     * This method empties the set of selected items, ensuring no items are marked as selected.
     */
    public void clearSelectedItems() {
        selectedItems.clear();
        notifyDataSetChanged();  // Notifying the adapter to refresh the list view
    }

}
