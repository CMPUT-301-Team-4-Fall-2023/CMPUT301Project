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

import com.example.cmput301project.R;

/**
 * Dialog fragment displayed when the sort button is clicked. Allows users to sort items
 * based on criteria such as tags. Provides input fields for users to enter tag values
 * and select sorting options using radio buttons. Utilizes a listener interface to
 * communicate with the hosting activity, notifying it when sorting criteria are selected.
 * Handles user interactions, including selecting sorting options and invoking the listener accordingly.
 */
public class SortItemsFragment extends DialogFragment {
    private RadioGroup radioGroup;
    private EditText tagEditText;
    private Object tag;
    private OnFragmentInteractionListener listener;

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
     * Defines a method to notify the activity when a sorting option is selected.
     */
    public interface OnFragmentInteractionListener {
        /**
         * Called when a sorting option is selected. Passes the selected sorting option (tag or other)
         * and the tag value if applicable.
         *
         * @param tag       The selected sorting option.
         * @param tagString The tag value if the sorting option is "TAG," otherwise null.
         */
        void onRadioButtonSaved(Object tag, String tagString);

    }

    /**
     * Creates the dialog with the specified layout and initializes UI elements.
     * Sets up radio button listeners and handles user interactions.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state.
     * @return The created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.sort_items, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        radioGroup = view.findViewById(R.id.sort_radio_group);
        tagEditText = view.findViewById(R.id.radio_tag_edit);

        Bundle args = getArguments();
        if (args != null) {
            Object tag = args.getSerializable("tagObject");
            if (tag.toString().equals("TAG")) {
                String tagString = args.getString("tagString");
                tagEditText.setText(tagString);
            }
            RadioButton radioButton = radioGroup.findViewWithTag(tag);
            radioButton.setChecked(true);
        }

        Dialog dialog = builder.setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", null)
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
                        String tagString = null;
                        if (radioButton != null) {
                            String tag = radioButton.getTag().toString();
                            if (tag.equals("TAG")) {
                                if (tagEditText.getText().toString().trim().isEmpty()) {
                                    tagEditText.setError("No tag given");
                                    return;
                                } else {
                                    tagString = tagEditText.getText().toString().trim();
                                }
                            }
                            listener.onRadioButtonSaved(tag, tagString);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

}
