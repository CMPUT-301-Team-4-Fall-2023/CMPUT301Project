package com.example.cmput301project.itemClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmput301project.R;
import com.example.cmput301project.activities.MainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
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
        TextView itemDescription = view.findViewById(R.id.item_description);
        TextView itemSerial = view.findViewById(R.id.item_serial);
        TextView itemModel = view.findViewById(R.id.item_model);
        TextView itemMake = view.findViewById(R.id.item_make);
        TextView itemComment = view.findViewById(R.id.item_comment);

        itemName.setText(item.getName());
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String newDateString = df.format(item.getPurchaseDate());
        itemMonth.setText(newDateString);
        itemCharge.setText("$" + String.format("%.2f", item.getValue()));
        itemDescription.setText(item.getDescription());
        itemSerial.setText(item.getSerialNumber().toString());
        itemModel.setText(item.getModel());
        itemMake.setText(item.getMake());
        itemComment.setText(item.getComment());


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

    public void deleteSelectedItems() {
        for (Item item : selectedItems) {
            items.remove(item);
        }
        notifyDataSetChanged();
        selectedItems.clear();
    }
}
