/**
 * Dialog fragment displayed when the filters button is clicked. Allows users to set filters for
 * items based on criteria such as date range, keywords, and makes. Provides input fields for users
 * to enter filter values and interactively set date ranges. Utilizes a listener interface to
 * communicate with the hosting activity, notifying it when filters are saved or cleared. Handles
 * user interactions, including setting and clearing filters, and invokes the listener accordingly.
 */


package com.example.cmput301project.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cmput301project.itemClasses.ItemFilter;
import com.example.cmput301project.R;
import com.example.cmput301project.itemClasses.Tag;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemFiltersFragment extends DialogFragment {
    private EditText editFromDate;
    private EditText editToDate;
    private Button addKeywordButton;
    private EditText editKeyword;
    private ChipGroup chipGroupKeywords;
    private EditText editTag;
    private EditText editMake;
    private OnFragmentInteractionListener listener;

    public ItemFiltersFragment(){

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + "OnFragmentInteractionListener is not implemented");
        }
    }

    public interface OnFragmentInteractionListener {
        void onFiltersSaved(ItemFilter itemFilter);
        void onFiltersCleared();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_filters, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        editFromDate = view.findViewById(R.id.from_date_edit_text);
        editToDate = view.findViewById(R.id.to_date_edit_text);

        addKeywordButton = view.findViewById(R.id.add_keyword_button);
        editKeyword = view.findViewById(R.id.filter_keywords_edit_text);
        chipGroupKeywords = view.findViewById(R.id.chip_group_keywords); // Initialize chipGroupTags
        editMake = view.findViewById(R.id.filter_make_edit_text);
        editTag = view.findViewById(R.id.filter_tag_edit_text);

        Bundle args = getArguments();
        if (args != null) {
            String fromString = args.getString("from");
            String toString = args.getString("to");
            String[] keywords = args.getStringArray("keywords");
            String make = args.getString("make");
            String tag = args.getString("tag");
            builder.setNeutralButton("Clear", null);
            editFromDate.setText(fromString);
            editToDate.setText(toString);
            editMake.setText(make);
            editTag.setText(tag);
            if (keywords != null) {
                for (String k : keywords) {
                    Chip chip = new Chip(getContext());
                    chip.setText(k);
                    chip.setCloseIconVisible(true);
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chipGroupKeywords.removeView(chip);
                        }
                    });
                    chipGroupKeywords.addView(chip);
                }
            }
        }

        Dialog dialog = builder.setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK",null)
                .create(); //create a dialog with buttons and title

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) { //need to overwrite the default behavior of buttons, which is to dismiss the dialog
                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button clearButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);

                clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editFromDate.setText("");
                        editToDate.setText("");
                        chipGroupKeywords.removeAllViews();
                        editMake.setText("");
                        editTag.setText("");
                        listener.onFiltersCleared();
                    }
                });

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ItemFilter itemFilter = new ItemFilter();

                        String fromDateString = editFromDate.getText().toString().trim();
                        String toDateString = editToDate.getText().toString().trim();
                        String makeString = editMake.getText().toString().trim();
                        String tagString = editTag.getText().toString().trim();


                        // Check if both date fields are empty
                        if (!fromDateString.isEmpty() || !toDateString.isEmpty()) {

                            // Validate date format
                            if (!isValidDateFormat(fromDateString) || !isValidDateFormat(toDateString)) {

                                if (!isValidDateFormat(fromDateString)) {
                                    editFromDate.setError("Invalid date format");
                                }
                                if (!isValidDateFormat(toDateString)) {
                                    editToDate.setError("Invalid date format");
                                }
                                return;
                            }

                        }




                        if (!fromDateString.isEmpty() && !toDateString.isEmpty()) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                Date fromDate = dateFormat.parse(fromDateString);
                                Date toDate = dateFormat.parse(toDateString);
                                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                itemFilter.setFrom(fromDate);
                                itemFilter.setTo(toDate);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!makeString.isEmpty()) {
                            itemFilter.setMake(makeString);
                        }
                        if (!tagString.isEmpty()) {
                            itemFilter.setTag(tagString);
                        }

                        for (int i = 0; i < chipGroupKeywords.getChildCount(); i++) {
                            Chip chip = (Chip) chipGroupKeywords.getChildAt(i);
                            String keyword = chip.getText().toString();
                            itemFilter.addKeyword(keyword);
                        }

                        listener.onFiltersSaved(itemFilter);
                        dialog.dismiss();
                    }
                });

                addKeywordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String keywordText = editKeyword.getText().toString().trim();
                        if (!keywordText.isEmpty()) {
                            Chip chip = new Chip(getContext());
                            chip.setText(keywordText);
                            chip.setCloseIconVisible(true);
                            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    chipGroupKeywords.removeView(chip);
                                }
                            });
                            chipGroupKeywords.addView(chip);
                            editKeyword.setText(""); // Clear the EditText after adding the chip
                        }
                    }
                });
            }
        });

        return dialog;
    }

    // Add a helper method to validate the date format
    private boolean isValidDateFormat(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            dateFormat.setLenient(false);
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

}