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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cmput301project.itemClasses.ItemFilter;
import com.example.cmput301project.R;
import com.example.cmput301project.itemClasses.Tag;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SortItemsFragment extends DialogFragment {
    private RadioGroup radioGroup;
    private Object tag;
    private OnFragmentInteractionListener listener;

    public SortItemsFragment(){

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
        void onRadioButtonSaved(Object tag);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.sort_items, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        radioGroup = view.findViewById(R.id.sort_radio_group);

        Bundle args = getArguments();
        if (args != null) {
            Object tag = args.getSerializable("tagObject");
            RadioButton radioButton =  radioGroup.findViewWithTag(tag);
            radioButton.setChecked(true);
        }

        Dialog dialog = builder.setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK",null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int selectedID = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton = radioGroup.findViewById(selectedID);

                        if (radioButton != null) {
                            String tag = radioButton.getTag().toString();
                            listener.onRadioButtonSaved(tag);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

}
