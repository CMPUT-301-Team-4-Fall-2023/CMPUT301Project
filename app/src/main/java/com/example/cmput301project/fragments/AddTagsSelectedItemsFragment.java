package com.example.cmput301project.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.List;
import java.util.Set;

public class AddTagsSelectedItemsFragment extends DialogFragment {
    private AddTagsSelectedItemsFragment.OnFragmentInteractionListener listener;
    private ItemAdapter itemAdapter;
    private EditText inputTagEditText;
    private ChipGroup chipGroupTags;
    private Button addTagButton;
    private Database db;


    public AddTagsSelectedItemsFragment (ItemAdapter itemAdapter) {
        this.itemAdapter = itemAdapter;
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
        listener = (OnFragmentInteractionListener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_tags_selected,null);
        inputTagEditText = view.findViewById(R.id.selected_items_input_tag_text);
        chipGroupTags = view.findViewById(R.id.selected_items_chip_group_tags);
        addTagButton = view.findViewById(R.id.selected_items_add_tags_button);
        db = Database.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Dialog dialog = builder.setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK",null)
                .create(); //create a dialog with buttons and title

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
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
                            db.editItem(item);
                        }
                        dialog.dismiss(); // Add this line to dismiss the dialog
                    }
                });
            }
        });
        return dialog;
    }

    // Define an interface for communication with the hosting activity
    public interface OnFragmentInteractionListener {
    }

}
