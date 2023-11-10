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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemFiltersFragment extends DialogFragment {
    private EditText editFromDate;
    private EditText editToDate;
    private EditText editKeywords;
    private EditText editMakes;
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
        editKeywords = view.findViewById(R.id.keywords_edit_text);
        editMakes = view.findViewById(R.id.makes_edit_text);

        Bundle args = getArguments();
        if (args != null) {
            String fromString = args.getString("from");
            String toString = args.getString("to");
            builder.setNeutralButton("Clear", null);
            editFromDate.setText(fromString);
            editToDate.setText(toString);
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
                        editKeywords.setText("");
                        editMakes.setText("");
                        listener.onFiltersCleared();
                    }
                });

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ItemFilter itemFilter = new ItemFilter();

                        String fromDateString = editFromDate.getText().toString().trim();
                        String toDateString = editToDate.getText().toString().trim();

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
                        listener.onFiltersSaved(itemFilter);
                        dialog.dismiss();
                    }
                });
            }
        });

        return dialog;
    }

}
