/**
 * A dialog fragment that allows users to add tags to a selected list of items. Users can input
 * new tags, which are then displayed as chips for review. The dialog ensures that duplicate tags
 * are not added to the tag list and checks whether any of the selected items already contain the
 * specified tag. Upon confirmation, the added tags are associated with the selected items and
 * stored in the database. Implements an interface for communication with the hosting activity.
 * Requires an {@link com.example.cmput301project.itemClasses.ItemAdapter} to manage the list of selected items.
 */

package com.example.cmput301project.fragments;

// Import statements

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cmput301project.Database;
import com.example.cmput301project.R;
import com.example.cmput301project.itemClasses.Item;
import com.example.cmput301project.itemClasses.ItemAdapter;
import com.example.cmput301project.itemClasses.Tag;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class AddTagsSelectedItemsFragment extends DialogFragment {
    private AddTagsSelectedItemsFragment.OnFragmentInteractionListener listener;
    private ItemAdapter itemAdapter;
    private EditText inputTagEditText;
    private ChipGroup chipGroupTags;
    private Button addTagButton;
    private Database db;

    /**
     * Constructor for the AddTagsSelectedItemsFragment class.
     *
     * @param itemAdapter The ItemAdapter to manage the list of selected items.
     */
    public AddTagsSelectedItemsFragment(ItemAdapter itemAdapter) {
        this.itemAdapter = itemAdapter;
    }

    /**
     * Checks if a tag is already added to the chip group.
     *
     * @param tagText The text of the tag to check.
     * @return True if the tag is already added, false otherwise.
     */
    private boolean isTagAlreadyAdded(String tagText) {
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupTags.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(tagText)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any of the selected items already contain the specified tag.
     *
     * @param tagText The text of the tag to check.
     * @return True if any selected item contains the tag, false otherwise.
     */
    private boolean isItemContainsTag(String tagText) {
        for (Item item : itemAdapter.getSelectedItems()) { // Assuming getItems() returns a list of all items
            ArrayList<Tag> tags = item.getTags(); // Assuming getTags() returns the set of tags of the item
            for (Tag tag : tags) {
                if (tag.getName().equalsIgnoreCase(tagText)) { // Assuming getName() returns the tag's name
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Called when the fragment is attached to an activity.
     *
     * @param context The context to which the fragment is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentInteractionListener) context;
    }

    /**
     * Creates and returns an AlertDialog for the fragment.
     *
     * @param savedInstanceState The saved instance state, if any.
     * @return The created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_tags_selected, null);
        inputTagEditText = view.findViewById(R.id.selected_items_input_tag_text);
        chipGroupTags = view.findViewById(R.id.selected_items_chip_group_tags);
        addTagButton = view.findViewById(R.id.selected_items_add_tags_button);
        db = Database.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Dialog dialog = builder.setView(view).setNegativeButton("Cancel", null).setPositiveButton("OK", null).create(); //create a dialog with buttons and title

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // Set the click listener for the add tag button
                addTagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tagText = inputTagEditText.getText().toString().trim();
                        if (!tagText.isEmpty()) {
                            if (isTagAlreadyAdded(tagText)) {
                                Toast.makeText(getContext(), "This tag is already in the tag list", Toast.LENGTH_SHORT).show();
                            } else if (isItemContainsTag(tagText)) {
                                Toast.makeText(getContext(), "One of your selected items already contains this tag", Toast.LENGTH_SHORT).show();
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

                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (Item item : itemAdapter.getSelectedItems()) {
                            for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                                Chip chip = (Chip) chipGroupTags.getChildAt(i);
                                String tagText = chip.getText().toString();

                                // Create a Tag object from tagText and associate it with the current itemToTag
                                Tag tag = new Tag(tagText); // You may need to adjust how you create a Tag object
                                item.addTag(tag);
                            }

                            db.editItem(item, () -> {});
                        }
                        itemAdapter.clearSelectedItems();
                        dialog.dismiss(); // Add this line to dismiss the dialog
                    }
                });
            }
        });
        return dialog;
    }

    /**
     * Interface for communication with the hosting activity.
     */
    public interface OnFragmentInteractionListener {
    }
}
