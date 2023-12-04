/**
 * Dialog fragment displayed when editing an existing item. Pre-populates input fields with
 * the details of the item being edited. Handles user input for item details such as name,
 * description, serial number, model, make, purchase date, price, and comments. Validates user
 * input for various fields, including character limits and format requirements. Allows users
 * to edit and add tags to the item. Utilizes a listener interface to communicate with the hosting
 * activity for item editing. Ensures proper handling of user interactions, validation errors, and
 * invokes the listener when an item is successfully edited. Collaborates with the hosting activity
 * to update the total cost of items after the edit.
 */

package com.example.cmput301project.fragments;

// Import statements

import static java.lang.Double.parseDouble;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cmput301project.Database;
import com.example.cmput301project.R;
import com.example.cmput301project.itemClasses.Item;
import com.example.cmput301project.itemClasses.Photograph;
import com.example.cmput301project.itemClasses.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class EditItemFragment extends DialogFragment {

    // Membership variable declaration
    private TextView title;
    private EditText itemName;
    private EditText itemDescription;
    private EditText itemSerial;
    private EditText itemModel;
    private TextView dateAdded;
    private EditText itemComments;
    private EditText itemPrice;
    private EditText itemMake;
    private Item editItem;
    private Boolean invalidInput;
    private OnFragmentInteractionListener listener;
    private EditText inputTagEditText;
    private ChipGroup chipGroupTags;
    private Button addTagButton;
    private Button scannerButton;
    private Uri imageURI = null;
    private Database db = Database.getInstance();
    public static final String TAG = "MAIN_TAG";
    private Button parseButton;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
    });
    private BarcodeScannerOptions barcodeScannerOptions;
    private BarcodeScanner barcodeScanner;
    private ImageView itemPicture;
    private Button deletePicture;

    /**
     * Constructor for the EditItemFragment class.
     *
     * @param item The item to be edited.
     */
    public EditItemFragment(Item item) { //if called with an item passed in, we assume that we want to edit the item
        this.editItem = item;
    }

    // Method to check if the tag is already added
    private boolean isTagAlreadyAdded(String tagText) {
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupTags.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(tagText)) {
                return true;
            }
        }
        return false;
    }

    private Button cameraButton;
    private Button galleryButton;
    private Uri cameraUri;

    /**
     * Takes a photo using the device's camera, stores it in the external media store,
     * and handles the result using the provided ActivityResultLauncher.
     */
    private void takePhotoCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "New Photo");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Item Photo");

        cameraUri = getActivity().getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        capturingImageResultLauncher.launch(intent);
    }

    /**
     * Handles the result of capturing an image using the camera.
     * If successful, uploads the image to the database, retrieves its download URL,
     * and displays the image using Glide library.
     */
    private final ActivityResultLauncher<Intent> capturingImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            Log.d(TAG, "onActivityResult: cameraUri: " + cameraUri);
            if (cameraUri != null) {
                Photograph photo = new Photograph(cameraUri.toString());
                photo.setName(UUID.randomUUID().toString());
                UploadTask uploadTask = db.addImage(photo.getName(), cameraUri);

                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return db.getImage(photo.getName());
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Glide.with(getActivity()).load(downloadUri).apply(new RequestOptions().placeholder(R.drawable.defaultuser).error(R.drawable.defaultuser)).into(itemPicture);
                            itemPicture.invalidate();
                            editItem.addPhotograph(photo);
                            deletePicture.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else {
                Log.d(TAG, "error taking picture with Camera");
            }
        }
    });

    /**
     * Handles the result of picking visual media (e.g., gallery images).
     * If successful, uploads the image to the database, retrieves its download URL,
     * and displays the image using Glide library.
     */
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), imageUri -> {
        if (imageUri != null) {
            Photograph photo = new Photograph(imageUri.toString());
            photo.setName(UUID.randomUUID().toString());
            UploadTask uploadTask = db.addImage(photo.getName(), imageUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return db.getImage(photo.getName());
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Glide.with(getActivity()).load(downloadUri).apply(new RequestOptions().placeholder(R.drawable.defaultuser).error(R.drawable.defaultuser)).into(itemPicture);
                        itemPicture.invalidate();
                        editItem.addPhotograph(photo);
                        deletePicture.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    });

    /**
     * Called when the fragment is attached to an activity.
     *
     * @param context The context to which the fragment is attached.
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
     * Interface for communication with the hosting activity.
     */
    public interface OnFragmentInteractionListener {
        void onItemEdited(Item item);

        void updateTotalCostAfterEdit();
    }

    /**
     * Creates and returns an AlertDialog for the fragment.
     *
     * @param savedInstanceState The saved instance state, if any.
     * @return The created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_item_layout, null);
        itemName = view.findViewById(R.id.name_edit_text); //find views on fragment to set text later
        itemDescription = view.findViewById(R.id.description_edit_text);
        itemSerial = view.findViewById(R.id.serial_edit_text);
        itemModel = view.findViewById(R.id.model_edit_text);
        itemMake = view.findViewById(R.id.make_edit_text);
        dateAdded = view.findViewById(R.id.date_added);
        itemPrice = view.findViewById(R.id.price_edit_text);
        itemComments = view.findViewById(R.id.comments_edit_text);
        itemPicture = view.findViewById(R.id.image_view);
        deletePicture = view.findViewById(R.id.delete_photo_button);
        deletePicture.setVisibility(View.GONE);


        dateAdded.setOnClickListener(this::displayCalendar);

        if(editItem.getPhotographs() != null && !editItem.getPhotographs().isEmpty()){
            db.getImage(editItem.getPhotographs().get(0).getName()).addOnSuccessListener(
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
                            deletePicture.setVisibility(View.VISIBLE);
                        }
                    }
            );
        }

        title = view.findViewById(R.id.add_item_title);
        inputTagEditText = view.findViewById(R.id.input_tag_edit_text); // Initialize inputTagEditText
        chipGroupTags = view.findViewById(R.id.chip_group_tags); // Initialize chipGroupTags
        addTagButton = view.findViewById(R.id.add_tags_button); // Initialize the addTagButton
        scannerButton = view.findViewById(R.id.scan_barcode_button);
        parseButton = view.findViewById(R.id.parse_barcode_button);
        barcodeScannerOptions = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);
        galleryButton = view.findViewById(R.id.gallery_button);
        cameraButton = view.findViewById(R.id.camera_button);
        itemPicture = view.findViewById(R.id.image_view);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    takePhotoCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });

        itemName.setText(editItem.getName()); //take data from item if constructed with item passes
        itemDescription.setText(editItem.getDescription());
        itemSerial.setText(editItem.getSerialNumber().toString());
        itemModel.setText(editItem.getModel());
        itemMake.setText(editItem.getMake());
        dateAdded.setText(new SimpleDateFormat("MM/dd/YYYY").format(editItem.getPurchaseDate()));
        itemPrice.setText(editItem.getValue().toString());
        itemComments.setText(editItem.getComment());

        if (editItem.getTags() != null) {
            for (Tag tag : editItem.getTags()) {
                Chip chip = new Chip(getContext());
                chip.setText(tag.getName());
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(v -> chipGroupTags.removeView(chip));
                chipGroupTags.addView(chip);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Dialog dialog = builder.setView(view).setNegativeButton("Cancel", null).setPositiveButton("OK", null).create(); //create a dialog with buttons and title

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) { //need to overwrite the default behavior of buttons, which is to dismiss the dialog
                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                Button addButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE); // This is your existing OK button logic

                deletePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editItem.setPhotographs(null);
                        deletePicture.setVisibility(View.GONE);
                        Glide.with(getActivity()).load("").into(itemPicture);
                    }
                });
                scannerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            pickImageCamera();
                        } else {
                            requestCameraPermission();
                        }
                    }
                });
                parseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (imageURI == null) {
                            //error
                        } else {
                            detectResultFromImage();
                        }
                    }
                });

                // Set the click listener for the add tag button
                addTagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tagText = inputTagEditText.getText().toString().trim();
                        if (!tagText.isEmpty()) {
                            if (isTagAlreadyAdded(tagText)) {
                                Toast.makeText(getContext(), "This tag has already been added", Toast.LENGTH_SHORT).show();
                            } else {
                                Chip chip = new Chip(getContext());
                                chip.setText(tagText);
                                chip.setCloseIconVisible(true);
                                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        chipGroupTags.removeView(chip);
                                    }
                                });
                                chipGroupTags.addView(chip);
                                inputTagEditText.setText(""); // Clear the EditText after adding the chip
                            }
                        }
                    }
                });

                okButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // Edit mode

                        title.setText("Edit Item");

                        // Extract input fields
                        String name = itemName.getText().toString().trim();
                        String description = itemDescription.getText().toString().trim();
                        String serialText = itemSerial.getText().toString().trim();
                        String model = itemModel.getText().toString().trim();
                        String make = itemMake.getText().toString().trim();
                        String priceText = itemPrice.getText().toString().trim();
                        String comments = itemComments.getText().toString().trim();
                        editItem.clearTags(); // Clear existing tags
                        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                            Chip chip = (Chip) chipGroupTags.getChildAt(i);
                            editItem.addTag(new Tag(chip.getText().toString())); // Add each tag to the item
                        }

                        // Check if any field is empty
                        boolean anyFieldsEmpty = name.isEmpty() || description.isEmpty() ||
                                model.isEmpty() || make.isEmpty() || priceText.isEmpty() || comments.isEmpty();

                        // Set serial number to 0 if non provided
                        String serial;
                        if (!serialText.isEmpty()) {
                            serial = serialText;
                        } else {
                            serial = "";
                        }

                        // Validate all fields
                        boolean isValidFields = isValidName(name) && isValidDescription(description) && isValidModel(model) && isValidMake(make) &&
                                isValidPrice(priceText) && isValidComment(comments);

                        // Edit item if fields valid
                        if (isValidFields && !anyFieldsEmpty) {
                            String date = dateAdded.getText().toString();
                            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                            Date parsedDate;
                            try {
                                parsedDate = df.parse(date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            Double price = Double.parseDouble(priceText);

                            // Edit the item
                            editItem.setName(itemName.getText().toString());
                            editItem.setDescription(itemDescription.getText().toString());
                            editItem.setSerialNumber(itemSerial.getText().toString());
                            editItem.setModel(itemModel.getText().toString());
                            editItem.setMake(itemMake.getText().toString());
                            editItem.setValue(parseDouble(itemPrice.getText().toString()));
                            editItem.setPurchaseDate(parsedDate);
                            editItem.setComment(itemComments.getText().toString());
                            listener.onItemEdited(editItem);
                            listener.updateTotalCostAfterEdit(); //recalculate monthly costs
                            dialog.dismiss();

                            // Error messages
                        } else {
                            if (!isValidName(name)) {
                                itemName.setError("Max 15 characters");
                            }
                            if (!isValidDescription(description)) {
                                itemDescription.setError("Max 50 characters");
                            }
                            if (!isValidModel(model)) {
                                itemModel.setError("Max 20 characters");
                            }
                            if (!isValidMake(make)) {
                                itemMake.setError("Max 20 characters");
                            }
                            if (!isValidPrice(priceText)) {
                                itemPrice.setError("Invalid price format");
                            }
                            if (!isValidComment(comments)) {
                                itemComments.setError("Max 25 characters");
                            }
                            if (name.isEmpty()) {
                                itemName.setError("Item name required");
                            }
                            if (description.isEmpty()) {
                                itemDescription.setError("Item description required");
                            }
                            if (model.isEmpty()) {
                                itemModel.setError("Item model required");
                            }
                            if (make.isEmpty()) {
                                itemMake.setError("Item make required");
                            }
                            if (priceText.isEmpty()) {
                                itemPrice.setError("Item price required");
                            }
                            if (comments.isEmpty()) {
                                itemComments.setError("Item comments required");
                            }
                        }
                    }
                });
            }
        });
        return dialog;
    }

    /**
     * Validates if the name input is valid.
     *
     * @param name The name input to be validated.
     * @return True if the name is valid, false otherwise.
     */
    private boolean isValidName(String name) {
        if (name.length() > 15) {
            return false;
        }
        return true;
    }

    /**
     * Validates if the description input is valid.
     *
     * @param description The description input to be validated.
     * @return True if the description is valid, false otherwise.
     */
    private boolean isValidDescription(String description) {
        if (description.length() > 50) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the given model string is valid.
     *
     * @param model The model string to be validated.
     * @return true if the model is valid (length is 20 characters or less), false otherwise.
     */
    private boolean isValidModel(String model) {
        if (model.length() > 20) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the given make string is valid.
     *
     * @param make The make string to be validated.
     * @return true if the make is valid (length is 20 characters or less), false otherwise.
     */
    private boolean isValidMake(String make) {
        if (make.length() > 20) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the given price string is a valid numeric format with up to two decimal places.
     *
     * @param price The price string to be validated.
     * @return true if the price is valid, false otherwise.
     */
    private boolean isValidPrice(String price) {
        String priceText = String.valueOf(price);
        return priceText.matches("^(0\\.\\d{1,2}|[1-9]\\d*\\.?\\d{0,2})$");
    }

    /**
     * Checks if the given comment string is valid.
     *
     * @param comment The comment string to be validated.
     * @return true if the comment is valid (length is 25 characters or less), false otherwise.
     */
    private boolean isValidComment(String comment) {
        if (comment.length() > 25) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the given day string represents a valid day of the month.
     *
     * @param day The day string to be validated.
     * @return true if the day is valid (within the range 1 to 31), false otherwise.
     */
    private boolean isValidDay(String day) {
        if (!day.isEmpty()) {
            int dayValue = Integer.parseInt(day);
            return dayValue >= 1 && dayValue <= 31;
        }
        return false;
    }

    /**
     * Checks if the given month string represents a valid month of the year.
     *
     * @param month The month string to be validated.
     * @return true if the month is valid (within the range 1 to 12), false otherwise.
     */
    private boolean isValidMonth(String month) {
        if (!month.isEmpty()) {
            int monthValue = Integer.parseInt(month);
            return monthValue >= 1 && monthValue <= 12;
        }
        return false;
    }

    /**
     * Checks if the given year string is a valid four-digit year.
     *
     * @param year The year string to be validated.
     * @return true if the year is valid (four digits), false otherwise.
     */
    private boolean isValidYear(String year) {
        if (!year.isEmpty() && year.matches("\\d{4}")) {
            return true;
        }
        return false;
    }

    /**
     * Picks an image from the camera.
     */
    private void pickImageCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageURI = getActivity().getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        cameraActivityResultLauncher.launch(intent);

    }

    /**
     * Requests camera permission.
     */
    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Log.d(TAG, "onActivityResult: imageURI: " + imageURI);
            } else {
                //Error
            }
        }
    });

    /**
     * Requests camera permission.
     */
    private void requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    /**
     * Detects barcode or QR code information from the image.
     */
    private void detectResultFromImage() {
        try {
            InputImage inputImage = InputImage.fromFilePath(getActivity(), imageURI);
            Task<List<Barcode>> barcodeResult = barcodeScanner.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                @Override
                public void onSuccess(List<Barcode> barcodes) {
                    extractBarCodeQRCodeInfo(barcodes);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    itemSerial.setError("Unable to parse that barcode. The image may be blurry, or the barcode is not supported.");
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Extracts barcode or QR code information from the detected barcodes.
     *
     * @param barcodes The list of detected barcodes.
     */
    private void extractBarCodeQRCodeInfo(List<Barcode> barcodes) {
        if (barcodes.isEmpty()) {
            itemSerial.setError("Unable to parse that barcode. The image may be blurry, or the barcode is not supported.");
        }
        for (Barcode barcode : barcodes) {
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();
            Log.d(TAG, "extractBarCodeQRCodeInfo: rawValue: " + rawValue);
            itemSerial.setText(barcode.getDisplayValue());
        }
    }
    private void displayCalendar(View v) {
        TextView textView = (TextView) v;
        Calendar cal = Calendar.getInstance();
        String[] oldDate = textView.getText().toString().split("/");
        // THE MONTHS HAVE -1 BECAUSE WHOEVER MADE THIS JAVA LIBRARY STARTS THE MONTHS AT 0??????
        cal.set(Calendar.MONTH, Integer.parseInt(oldDate[0])-1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(oldDate[1]));
        cal.set(Calendar.YEAR, Integer.parseInt(oldDate[2]));
        DatePickerDialog.OnDateSetListener date = (fromView, year, month, day) -> {
            cal.set(Calendar.MONTH, Integer.parseInt(oldDate[0])-1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(oldDate[1]));
            cal.set(Calendar.YEAR, Integer.parseInt(oldDate[2]));
        };
        DatePickerDialog dialog = new DatePickerDialog(getContext(), date, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            updateLabel(textView, year, month, dayOfMonth);
        });
        dialog.show();
    }

    private void updateLabel(View v, int year, int month, int day) {
        TextView textView = (TextView) v;
        String myFormat = "MM/dd/yyyy";
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        textView.setText(dateFormat.format(cal.getTime()));
    }
}
