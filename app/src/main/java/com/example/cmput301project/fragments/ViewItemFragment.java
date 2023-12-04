/**
 * Dialog fragment that opens when viewing item details. Displays comprehensive information about
 * the selected item, including its name, date, price, serial number, model, make, comments, and
 * associated tags. Utilizes a listener interface to communicate with the hosting activity, allowing
 * the user to edit or delete the viewed item. Provides a visually appealing layout to present item
 * details. Implements buttons for user interaction, such as editing, deleting, and dismissing the
 * details view. The hosting activity is notified of user actions through the listener interface.
 */


package com.example.cmput301project.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cmput301project.Database;
import com.example.cmput301project.itemClasses.Item;
import com.example.cmput301project.R;
import com.example.cmput301project.itemClasses.Photograph;
import com.example.cmput301project.itemClasses.Tag;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

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

    private ImageView itemPicture;

    private Database db = Database.getInstance();

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
        itemTags = view.findViewById(R.id.view_item_tags);
        itemPicture = view.findViewById(R.id.image_view);

        if(!viewedItem.getPhotographs().isEmpty()){
            db.getImage(viewedItem.getPhotographs().get(0).getName()).addOnSuccessListener(
                    new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getActivity())
                                    .load(uri)
                                    .apply(new RequestOptions()
                                            .placeholder(R.drawable.defaultuser)
                                            .error(R.drawable.defaultuser))
                                    .into(itemPicture);
                            itemPicture.invalidate();
                        }
                    }
            );
        }

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

        // Assuming the Item class has a method getTags() that returns a List of Tag objects
        StringBuilder tagsBuilder = new StringBuilder();
        if(viewedItem.getTags() != null){
            for (Tag tag : viewedItem.getTags()) {
                if (tagsBuilder.length() > 0) {
                    tagsBuilder.append(" | "); // Separator for multiple tags
                }
                tagsBuilder.append(tag.getName()); // Append the tag name to the builder
            }
        }
        // Set the tags text from the StringBuilder
        itemTags.setText("Tags: " + tagsBuilder.toString());

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
