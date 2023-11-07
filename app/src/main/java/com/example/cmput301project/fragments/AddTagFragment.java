package com.example.cmput301project.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.cmput301project.R;
import com.example.cmput301project.itemClasses.Tag;
import java.util.ArrayList;

public class AddTagFragment extends DialogFragment {
    private Button addButton;
    private EditText tagEdit;
    private TextView tagView;

    private ArrayList<Tag> tags;

    public AddTagFragment(ArrayList<Tag> tags){
        this.tags = tags;
    }

    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        void tagsAdded(ArrayList<Tag> tags);
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_tag_layout, null);
        addButton = view.findViewById(R.id.addButton);
        tagView = view.findViewById(R.id.tag_view_text);
        tagEdit = view.findViewById(R.id.tag_edit_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Dialog dialog = builder.setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", null).create(); //create a dialog with buttons and title

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

            }
        });
        return dialog;
    }
}
