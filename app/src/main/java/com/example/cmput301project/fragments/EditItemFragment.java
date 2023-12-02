/**
 * Dialog fragment displayed when editing an existing item. Pre-populates input fields with
 * the details of the item being edited. Handles user input for item details such as name,
 * description, serial number, model, make, purchase date, price, and comments. Validates user
 * input for various fields, including character limits and format requirements. Allows users
 * to edit and add tags to the item. Utilizes a listener interface to communicate with the hosting
 * activity for item editing. Ensures proper handling of user interactions, validation errors, and
 * invokes the listener when an item is successfully edited. Collaborates with the hosting activity
 * to update the total cost of items after the edit.
 */


package com.example.cmput301project.fragments;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Double.parseDouble;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cmput301project.itemClasses.Item;
import com.example.cmput301project.R;
import com.example.cmput301project.itemClasses.Tag;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EditItemFragment extends DialogFragment {
    private TextView title;
    private EditText itemName;
    private EditText itemDescription;
    private EditText itemSerial;
    private EditText itemModel;
    private EditText itemDay;
    private EditText itemMonth;
    private EditText itemYear;
    private EditText itemComments;
    private EditText itemPrice;
    private EditText itemMake;
    private Item editItem;
    private Boolean invalidInput;
    private OnFragmentInteractionListener listener;
    private EditText inputTagEditText;
    private ChipGroup chipGroupTags;
    private Button addTagButton;


    public EditItemFragment(Item item) { //if called with an item passed in, we assume that we want to edit the item
        this.editItem = item;
    }

    // Method to check if the tag is already added
    private boolean isTagAlreadyAdded(String tagText) {
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupTags.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(tagText)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + "OnFragmentInteractionListener is not implemented");
        }
    }


    public interface OnFragmentInteractionListener {
        void onItemEdited(Item item);

        void updateTotalCostAfterEdit();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_item_layout, null);
        itemName = view.findViewById(R.id.name_edit_text); //find views on fragment to set text later
        itemDescription = view.findViewById(R.id.description_edit_text);
        itemSerial = view.findViewById(R.id.serial_edit_text);
        itemModel = view.findViewById(R.id.model_edit_text);
        itemMake = view.findViewById(R.id.make_edit_text);
        itemDay = view.findViewById(R.id.item_day_edit_text);
        itemMonth = view.findViewById(R.id.item_month_edit_text);
        itemYear = view.findViewById(R.id.item_year_edit_text);
        itemPrice = view.findViewById(R.id.price_edit_text);
        itemComments = view.findViewById(R.id.comments_edit_text);
        title = view.findViewById(R.id.add_item_title);
        inputTagEditText = view.findViewById(R.id.input_tag_edit_text); // Initialize inputTagEditText
        chipGroupTags = view.findViewById(R.id.chip_group_tags); // Initialize chipGroupTags
        addTagButton = view.findViewById(R.id.add_tags_button); // Initialize the addTagButton

        itemName.setText(editItem.getName()); //take data from item if constructed with item passes
        itemDescription.setText(editItem.getDescription());
        itemSerial.setText(editItem.getSerialNumber().toString());
        itemModel.setText(editItem.getModel());
        itemMake.setText(editItem.getMake());
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        itemDay.setText(sdf.format(editItem.getPurchaseDate()) + "");
        sdf = new SimpleDateFormat("MM");
        itemMonth.setText(sdf.format(editItem.getPurchaseDate()) + "");
        sdf = new SimpleDateFormat("yyyy");
        itemYear.setText(sdf.format(editItem.getPurchaseDate()) + "");
        itemPrice.setText(editItem.getValue().toString());
        itemComments.setText(editItem.getComment());

        for (Tag tag : editItem.getTags()) {
            Chip chip = new Chip(getContext());
            chip.setText(tag.getName());
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> chipGroupTags.removeView(chip));
            chipGroupTags.addView(chip);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Dialog dialog = builder.setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", null).create(); //create a dialog with buttons and title

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) { //need to overwrite the default behavior of buttons, which is to dismiss the dialog
                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                Button addButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE); // This is your existing OK button logic

                // Set the click listener for the add tag button
                addTagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tagText = inputTagEditText.getText().toString().trim();
                        if (!tagText.isEmpty()) {
                            if(isTagAlreadyAdded(tagText)) {
                                Toast.makeText(getContext(), "This tag has already been added", Toast.LENGTH_SHORT).show();
                            } else {
                                Chip chip = new Chip(getContext());
                                chip.setText(tagText);
                                chip.setCloseIconVisible(true);
                                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        chipGroupTags.removeView(chip);
                                    }
                                });
                                chipGroupTags.addView(chip);
                                inputTagEditText.setText(""); // Clear the EditText after adding the chip
                            }
                        }
                    }
                });

                okButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // Edit mode

                        title.setText("Edit Item");

                        // Extract input fields
                        String name = itemName.getText().toString().trim();
                        String description = itemDescription.getText().toString().trim();
                        String serialText = itemSerial.getText().toString().trim();
                        String model = itemModel.getText().toString().trim();
                        String make = itemMake.getText().toString().trim();
                        String priceText = itemPrice.getText().toString().trim();
                        String comments = itemComments.getText().toString().trim();
                        String day = itemDay.getText().toString().trim();
                        String month = itemMonth.getText().toString().trim();
                        String year = itemYear.getText().toString().trim();
                        editItem.clearTags(); // Clear existing tags
                        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                            Chip chip = (Chip) chipGroupTags.getChildAt(i);
                            editItem.addTag(new Tag(chip.getText().toString())); // Add each tag to the item
                        }

                        // Check if any field is empty
                        boolean anyFieldsEmpty = name.isEmpty() || description.isEmpty() ||
                                model.isEmpty() || make.isEmpty() || day.isEmpty() || month.isEmpty() ||
                                year.isEmpty() || priceText.isEmpty() || comments.isEmpty();

                        // Set serial number to 0 if non provided
                        Integer serial;
                        if (!serialText.isEmpty()) {
                            serial = Integer.parseInt(serialText);
                        } else {
                            serial = 0;
                        }

                        // Validate all fields
                        boolean isValidFields = isValidName(name) && isValidDescription(description) && isValidModel(model) && isValidMake(make) &&
                                isValidPrice(priceText) && isValidComment(comments) && isValidDay(day) && isValidMonth(month) && isValidYear(year);

                        // Edit item if fields valid
                        if (isValidFields && !anyFieldsEmpty) {
                            String date = month + "/" + day + "/" + year;
                            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                            Date parsedDate;
                            try {
                                parsedDate = df.parse(date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            Double price = Double.parseDouble(priceText);

                            // Edit the item
                            editItem.setName(itemName.getText().toString());
                            editItem.setDescription(itemDescription.getText().toString());
                            editItem.setSerialNumber(Integer.parseInt(itemSerial.getText().toString()));
                            editItem.setModel(itemModel.getText().toString());
                            editItem.setMake(itemMake.getText().toString());
                            editItem.setValue(parseDouble(itemPrice.getText().toString()));
                            editItem.setPurchaseDate(parsedDate);
                            editItem.setComment(itemComments.getText().toString());
                            listener.onItemEdited(editItem);
                            listener.updateTotalCostAfterEdit(); //recalculate monthly costs
                            dialog.dismiss();

                            // Error messages
                        } else {
                            if (!isValidName(name)) {
                                itemName.setError("Max 15 characters");
                            }
                            if (!isValidDescription(description)) {
                                itemDescription.setError("Max 50 characters");
                            }
                            if (!isValidModel(model)) {
                                itemModel.setError("Max 20 characters");
                            }
                            if (!isValidMake(make)) {
                                itemMake.setError("Max 20 characters");
                            }
                            if (!isValidPrice(priceText)) {
                                itemPrice.setError("Invalid price format");
                            }
                            if (!isValidComment(comments)) {
                                itemComments.setError("Max 25 characters");
                            }
                            if (!isValidDay(day)) {
                                itemDay.setError(("Invalid day"));
                            }
                            if (!isValidMonth(month)) {
                                itemMonth.setError(("Invalid month"));
                            }
                            if (!isValidYear(year)) {
                                itemYear.setError(("Invalid year"));
                            }
                            if (name.isEmpty()) {
                                itemName.setError("Item name required");
                            }
                            if (description.isEmpty()) {
                                itemDescription.setError("Item description required");
                            }
                            if (model.isEmpty()) {
                                itemModel.setError("Item model required");
                            }
                            if (make.isEmpty()) {
                                itemMake.setError("Item make required");
                            }
                            if (priceText.isEmpty()) {
                                itemPrice.setError("Item price required");
                            }
                            if (comments.isEmpty()) {
                                itemComments.setError("Item comments required");
                            }
                            if (day.isEmpty()) {
                                itemDay.setError("Date required");
                            }
                            if (month.isEmpty()) {
                                itemMonth.setError("Month required");
                            }
                            if (year.isEmpty()) {
                                itemYear.setError("Year required");
                            }
                        }
                    }
                });
            }
        });
        return dialog;
    }


    private boolean isValidName(String name) {
        if (name.length() > 15) {
            return false;
        }
        return true;
    }


    private boolean isValidDescription(String description) {
        if (description.length() > 50) {
            return false;
        }
        return true;
    }


    private boolean isValidModel(String model) {
        if (model.length() > 20) {
            return false;
        }
        return true;
    }


    private boolean isValidMake(String make) {
        if (make.length() > 20) {
            return false;
        }
        return true;
    }


    private boolean isValidPrice(String price) {
        String priceText = String.valueOf(price);
        return priceText.matches("^(0\\.\\d{1,2}|[1-9]\\d*\\.?\\d{0,2})$");
    }


    private boolean isValidComment(String comment) {
        if (comment.length() > 25) {
            return false;
        }
        return true;
    }


    private boolean isValidDay(String day) {
        if (!day.isEmpty()) {
            int dayValue = Integer.parseInt(day);
            return dayValue >= 1 && dayValue <= 31;
        }
        return false;
    }


    private boolean isValidMonth(String month) {
        if (!month.isEmpty()) {
            int monthValue = Integer.parseInt(month);
            return monthValue >= 1 && monthValue <= 12;
        }
        return false;
    }


    private boolean isValidYear(String year) {
        if (!year.isEmpty() && year.matches("\\d{4}")) {
            return true;
        }
        return false;
    }
}
