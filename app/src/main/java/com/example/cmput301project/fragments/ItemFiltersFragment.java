/**
 * Dialog fragment displayed when the filters button is clicked. Allows users to set filters for
 * items based on criteria such as date range, keywords, and makes. Provides input fields for users
 * to enter filter values and interactively set date ranges. Utilizes a listener interface to
 * communicate with the hosting activity, notifying it when filters are saved or cleared. Handles
 * user interactions, including setting and clearing filters, and invokes the listener accordingly.
 */

package com.example.cmput301project.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cmput301project.R;
import com.example.cmput301project.itemClasses.ItemFilter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ItemFiltersFragment extends DialogFragment {
    private TextView editFromDate;
    private TextView editToDate;
    private Button addKeywordButton;
    private EditText editKeyword;
    private ChipGroup chipGroupKeywords;
    private EditText editTag;
    private EditText editMake;
    private OnFragmentInteractionListener listener;

    public ItemFiltersFragment() {

    }

    /**
     * Called when the fragment is attached to an activity. Sets the listener if the activity
     * implements the OnFragmentInteractionListener interface.
     *
     * @param context The context in which the fragment is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + "OnFragmentInteractionListener is not implemented");
        }
    }

    /**
     * Interface for communication between this fragment and the hosting activity.
     * Defines methods to notify the activity when filters are saved or cleared.
     */
    public interface OnFragmentInteractionListener {
        /**
         * Called when filters are saved. Passes the applied filters as an ItemFilter object.
         *
         * @param itemFilter The object containing the applied filters.
         */
        void onFiltersSaved(ItemFilter itemFilter);

        /**
         * Called when filters are cleared.
         */
        void onFiltersCleared();
    }

    /**
     * Creates the dialog with the specified layout and initializes UI elements.
     * Sets up button click listeners and handles user interactions.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state.
     * @return The created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_filters, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        editFromDate = view.findViewById(R.id.from_date_text_view);
        editToDate = view.findViewById(R.id.to_date_text_view);

        editFromDate.setOnClickListener(this::displayCalendar);
        editToDate.setOnClickListener(this::displayCalendar);

        addKeywordButton = view.findViewById(R.id.add_keyword_button);
        editKeyword = view.findViewById(R.id.filter_keywords_edit_text);
        chipGroupKeywords = view.findViewById(R.id.chip_group_keywords); // Initialize chipGroupTags
        editMake = view.findViewById(R.id.filter_make_edit_text);
        editTag = view.findViewById(R.id.filter_tag_edit_text);

        Bundle args = getArguments();
        if (!args.isEmpty()) {
            String fromString = args.getString("from");
            String toString = args.getString("to");
            String[] keywords = args.getStringArray("keywords");
            String make = args.getString("make");
            String tag = args.getString("tag");
            builder.setNeutralButton("Clear", null);
            if(fromString != null) {
                editFromDate.setText(fromString);
            }
            if(toString != null) {
                editToDate.setText(toString);
            }
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
                .setPositiveButton("Apply", null)
                .setNeutralButton("Clear", null)
                .create(); //create a dialog with buttons and title

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) { //need to overwrite the default behavior of buttons, which is to dismiss the dialog
                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button clearButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);

                clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editFromDate.setText("MM/DD/YYYY");
                        editToDate.setText("MM/DD/YYYY");
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

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

                        if (!fromDateString.equals("MM/DD/YYYY") && !toDateString.equals("MM/DD/YYYY")) {
                            try {
                                Date fromDate = dateFormat.parse(fromDateString);
                                Date toDate = dateFormat.parse(toDateString);
                                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                itemFilter.setFrom(fromDate);
                                itemFilter.setTo(toDate);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (!fromDateString.equals("MM/DD/YYYY")){
                            try{
                                Date fromDate = dateFormat.parse(fromDateString);
                                itemFilter.setFrom(fromDate);
                                itemFilter.setTo(new Date());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (!toDateString.equals("MM/DD/YYYY")){
                            try{
                                Date toDate = dateFormat.parse(toDateString);
                                itemFilter.setFrom(new Date(0));
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

    /**
     * This will display the calendar widget set to the correct date if the user has chosen one
     * previously
     * @param v the textview that the user clicked on
     */
    private void displayCalendar(View v) {
        Calendar cal = Calendar.getInstance();
        TextView textView = (TextView) v;
        DatePickerDialog.OnDateSetListener date;
        if (!textView.getText().toString().equals("MM/DD/YYYY")) {
            String[] oldDate = textView.getText().toString().split("/");
            // THE MONTHS HAVE -1 BECAUSE WHOEVER MADE THIS JAVA LIBRARY STARTS THE MONTHS AT 0??????
            cal.set(Calendar.MONTH, Integer.parseInt(oldDate[0]) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(oldDate[1]));
            cal.set(Calendar.YEAR, Integer.parseInt(oldDate[2]));
            date = (fromView, year, month, day) -> {
                cal.set(Calendar.MONTH, Integer.parseInt(oldDate[0]) - 1);
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(oldDate[1]));
                cal.set(Calendar.YEAR, Integer.parseInt(oldDate[2]));
            };
        }else {
            date = (fromView, year, month, day) -> {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
            };
        }
        DatePickerDialog dialog = new DatePickerDialog(getContext(), date, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            updateLabel(v, year, month, dayOfMonth);
        });
        dialog.show();
    }

    /**
     * This will update the textview v with the new date (formatted properly)
     * @param v The textview that the new date will be put into
     * @param year The new year
     * @param month The new month
     * @param day The new day
     */
    private void updateLabel(View v, int year, int month, int day) {
        if (v instanceof TextView) {
            TextView textView = (TextView) v;
            String myFormat = "MM/dd/yyyy";
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
            textView.setText(dateFormat.format(cal.getTime()));
        }
    }
}