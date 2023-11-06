package com.example.cmput301project.Fragments;

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

import com.example.cmput301project.ItemClasses.ItemFilter;
import com.example.cmput301project.R;

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
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_filters, null);

        editFromDate = view.findViewById(R.id.from_date_edit_text);
        editToDate = view.findViewById(R.id.to_date_edit_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Dialog dialog = builder.setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK",null).create(); //create a dialog with buttons and title

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) { //need to overwrite the default behavior of buttons, which is to dismiss the dialog
                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
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
                                itemFilter.setFrom(fromDate);
                                itemFilter.setTo(toDate);
                                itemFilter.setFilterDate(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onFiltersSaved(itemFilter);
                    }
                });
            }
        });

        return dialog;
    }

}
