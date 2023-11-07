package com.example.cmput301project.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cmput301project.itemClasses.Item;
import com.example.cmput301project.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ViewItemFragment extends DialogFragment {
    private TextView itemName;
    private TextView itemDescription;
    private TextView itemPrice;
    private TextView itemSerial;
    private TextView itemModel;
    private TextView itemMake;
    private TextView itemDate;
    private TextView itemTags;
    private TextView itemComment;
    private Item viewedItem;
    private OnFragmentInteractionListener listener;
    public ViewItemFragment(Item item){
        this.viewedItem = item;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + "OnFragmentInteractionListener is not attached.");
        }
    }

    public interface OnFragmentInteractionListener {
        void editItem(Item item);
        void onDeletePressed(Item item);
        void updateTotalCost();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_item_layout, null);

        itemName = view.findViewById(R.id.view_item_name);
        itemDescription = view.findViewById(R.id.view_item_description);
        itemPrice = view.findViewById(R.id.view_item_price);
        itemSerial = view.findViewById(R.id.view_item_serial);
        itemModel = view.findViewById(R.id.view_item_model);
        itemMake = view.findViewById(R.id.view_item_make);
        itemDate = view.findViewById(R.id.view_item_date);
        itemComment = view.findViewById(R.id.view_item_comment);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        itemName.setText("Name: "+ viewedItem.getName());
        itemDescription.setText("Description: "+ viewedItem.getDescription());
        itemPrice.setText("Price: "+ viewedItem.getValue().toString());
        itemModel.setText("Model: "+ viewedItem.getModel());
        itemSerial.setText("Serial #: "+ viewedItem.getSerialNumber().toString());
        itemMake.setText("Make: "+ viewedItem.getMake());
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        itemDate.setText("Date: "+ df.format(viewedItem.getPurchaseDate()));
        itemComment.setText("Comments: "+ viewedItem.getComment());

        Dialog dialog = builder //set dialog buttons and perform methods defined on MainActivity
                .setView(view)
                .setTitle("Item Details")
                .setPositiveButton("Done", null)
                .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.editItem(viewedItem);
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDeletePressed(viewedItem);
                        listener.updateTotalCost();
                    }
                })
                .create();

        return dialog;
    }
}
