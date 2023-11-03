package com.example.cmput301project;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import android.app.AlertDialog;
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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Date;

public class AddItemFragment extends DialogFragment {
    private TextView title;
    private EditText itemName;
    private EditText itemDescription;
    private EditText itemSerial;
    private EditText itemModel;
    private EditText itemDay;
    private EditText itemMonth;
    private EditText itemYear;
    private EditText itemComments;
    private EditText itemPrice;
    private EditText itemMake;
    private Item editItem;
    private Boolean editMode;
    private Boolean invalidInput;
    private OnFragmentInteractionListener listener;
    public AddItemFragment(Item item){ //if called with an item passed in, we assume that we want to edit the item
        this.editItem = item;
        this.editMode = TRUE;
    }
    public AddItemFragment(){
        this.editMode = FALSE;
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
        void onOKPressed(Item item);
        void updateTotalCost();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_item_layout, null);
        itemName = view.findViewById(R.id.name_edit_text); //find views on fragment to set text later
        itemDescription = view.findViewById(R.id.description_edit_text);
        itemSerial = view.findViewById(R.id.serial_edit_text);
        itemModel = view.findViewById(R.id.model_edit_text);
        itemMake = view.findViewById(R.id.make_edit_text);
        itemDay = view.findViewById(R.id.item_day_edit_text);
        itemMonth = view.findViewById(R.id.item_month_edit_text);
        itemYear = view.findViewById(R.id.item_year_edit_text);
        itemPrice = view.findViewById(R.id.price_edit_text);
        itemComments = view.findViewById(R.id.comments_edit_text);
        title = view.findViewById(R.id.add_item_title);
        if (editMode){
            itemName.setText(editItem.getName()); //take data from item if constructed with item passes
            itemDescription.setText(editItem.getDescription());
            itemSerial.setText(editItem.getSerialNumber().toString());
            itemModel.setText(editItem.getModel());
            itemMake.setText(editItem.getMake());
            itemDay.setText(editItem.getPurchaseDate().getDay()+"");
            itemMonth.setText((editItem.getPurchaseDate().getMonth()+1)+"");
            itemYear.setText((editItem.getPurchaseDate().getYear()+1900)+"");
            itemPrice.setText(editItem.getValue().toString());
            itemComments.setText(editItem.getComment());
        }

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

                            if (editMode){ //edit mode
                                title.setText("Edit Item");
                                String day = itemDay.getText().toString();
                                String month = itemMonth.getText().toString();
                                String year = itemYear.getText().toString();
                                editItem.setName(itemName.getText().toString());
                                editItem.setDescription(itemDescription.getText().toString());
                                editItem.setSerialNumber(Integer.parseInt(itemSerial.getText().toString()));
                                editItem.setModel(itemModel.getText().toString());
                                editItem.setMake(itemMake.getText().toString());
                                editItem.setValue(parseDouble(itemPrice.getText().toString()));
                                String date = month + "/" + day + "/" + year;
                                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                Date parsedDate;
                                try {
                                    parsedDate = df.parse(date);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                editItem.setPurchaseDate(parsedDate);
                                editItem.setComment(itemComments.getText().toString());
                                listener.updateTotalCost(); //recalculate monthly costs
                                dialog.dismiss();
                            }
                            else{ //add mode
                                title.setText("Add Item");
                                String name = itemName.getText().toString();
                                String description = itemDescription.getText().toString();
                                Integer serial = parseInt(itemSerial.getText().toString());
                                String model = itemModel.getText().toString();
                                String make = itemMake.getText().toString();
                                String day = itemDay.getText().toString();
                                String month = itemMonth.getText().toString();
                                String year = itemYear.getText().toString();
                                Double price = parseDouble(itemPrice.getText().toString());
                                String comments = itemComments.getText().toString();
                                String date = month + "/" + day + "/" + year;
                                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                Date parsedDate;
                                try {
                                    parsedDate = df.parse(date);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                listener.onOKPressed(new Item(name,parsedDate,description,make,model,serial,price,comments));
                                listener.updateTotalCost();
                                dialog.dismiss();
                            }

                    }
                });
            }
        });


        return dialog;
    }
}
