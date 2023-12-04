/**
 * Dialog fragment displayed when adding a new item. Handles user input for item details such as name,
 * description, serial number, model, make, purchase date, price, and comments. Validates user input
 * for various fields, including character limits and format requirements. Allows users to add tags to
 * the item. Utilizes a listener interface to communicate with the hosting activity for item addition.
 * Ensures proper handling of user interactions, validation errors, and invokes the listener when a new
 * item is successfully added. Collaborates with the hosting activity to update the total cost of items.
 */

package com.example.cmput301project.fragments;

// Import Statements

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import com.example.cmput301project.itemClasses.UniqueId;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Dialog fragment displayed when adding a new item. Handles user input for item details such as name,
 * description, serial number, model, make, purchase date, price, and comments. Validates user input
 * for various fields, including character limits and format requirements. Allows users to add tags to
 * the item. Utilizes a listener interface to communicate with the hosting activity for item addition.
 * Ensures proper handling of user interactions, validation errors, and invokes the listener when a new
 * item is successfully added. Collaborates with the hosting activity to update the total cost of items.
 */
public class AddItemFragment extends DialogFragment {

    // Membership variable declaration
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
    private Item newItem;
    private OnFragmentInteractionListener listener;
    private EditText inputTagEditText;
    private ChipGroup chipGroupTags;
    private ImageView itemPicture;
    private Database db = Database.getInstance();
    private Button addTagButton;
    private Button scannerButton;
    private Uri imageURI = null;
    public static final String TAG = "MAIN_TAG";
    private Button parseButton;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
    });
    private BarcodeScannerOptions barcodeScannerOptions;
    private BarcodeScanner barcodeScanner;
    private Button cameraButton;
    private Button galleryButton;
    private Button deletePicture;
    private Uri cameraUri;

    /**
     * Takes a photo using the device's camera and handles the result, including uploading the photo to a database,
     * updating the UI, and managing the associated item's photographs.
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
     * Callback for handling the result of capturing an image using the device's camera.
     * Processes the captured image, uploads it to a database, and updates the UI accordingly.
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
                            if (newItem == null) {
                                newItem = new Item();
                            }
                            newItem.addPhotograph(photo);
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
     * Callback for handling the result of picking visual media (e.g., images) from the device.
     * Processes the picked image, uploads it to a database, and updates the UI accordingly.
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
                        if (newItem == null) {
                            newItem = new Item();
                        }
                        newItem.addPhotograph(photo);
                        deletePicture.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    });

    /**
     * Called when the fragment is attached to the context. Ensures that the hosting activity
     * implements the necessary listener interface; otherwise, a RuntimeException is thrown.
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
     * Interface for communication between the AddItemFragment and its hosting activity.
     * Defines methods for handling the OK button press and updating the total cost of items.
     */
    public interface OnFragmentInteractionListener {
        void onOKPressed(Item item);

        void updateTotalCost();
    }

    /**
     * Checks if a tag with the given text has already been added to the ChipGroup.
     *
     * @param tagText The text of the tag to check.
     * @return True if the tag is already added, false otherwise.
     */
    private boolean isTagAlreadyAdded(String tagText) {
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupTags.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(tagText)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initializes the AddItemFragment, setting up UI elements and event listeners for buttons.
     * Retrieves references to various UI elements such as EditText, Button, and ImageView.
     * Initializes the barcode scanner and its options. Sets up the camera and gallery buttons for image picking.
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return The configured dialog.
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
        itemDay = view.findViewById(R.id.item_day_edit_text);
        itemMonth = view.findViewById(R.id.item_month_edit_text);
        itemYear = view.findViewById(R.id.item_year_edit_text);
        itemPrice = view.findViewById(R.id.price_edit_text);
        itemComments = view.findViewById(R.id.comments_edit_text);
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
        deletePicture = view.findViewById(R.id.delete_photo_button);
        deletePicture.setVisibility(View.GONE);

        deletePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newItem.setPhotographs(null);
                deletePicture.setVisibility(View.GONE);
                Glide.with(getActivity()).load("").into(itemPicture);
            }
        });
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Dialog dialog = builder.setView(view).setNegativeButton("Cancel", null).setPositiveButton("OK", null).create(); //create a dialog with buttons and title

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) { //need to overwrite the default behavior of buttons, which is to dismiss the dialog
                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button addButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE); // This is your existing OK button logic

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
                        // Add mode

                        // Extract input fields
                        String name = itemName.getText().toString().trim();
                        String description = itemDescription.getText().toString().trim();
                        String serialText = itemSerial.getText().toString().trim();
                        String model = itemModel.getText().toString().trim();
                        String make = itemMake.getText().toString().trim();
                        String day = itemDay.getText().toString().trim();
                        String month = itemMonth.getText().toString().trim();
                        String year = itemYear.getText().toString().trim();
                        String priceText = itemPrice.getText().toString().trim();
                        String comments = itemComments.getText().toString().trim();

                        // Check if any field is empty
                        boolean anyFieldsEmpty = name.isEmpty() || description.isEmpty() || model.isEmpty() || make.isEmpty() || day.isEmpty() || month.isEmpty() || year.isEmpty() || priceText.isEmpty() || comments.isEmpty();

                        // Set serial number to 0 if non provided
                        String serial;
                        if (!serialText.isEmpty()) {
                            serial = serialText;
                        } else {
                            serial = "";
                        }

                        // Validate all fields
                        boolean isValidFields = isValidName(name) && isValidDescription(description) && isValidModel(model) && isValidMake(make) && isValidPrice(priceText) && isValidComment(comments) && isValidDay(day) && isValidMonth(month) && isValidYear(year);

                        // Add new item if fields valid
                        if (isValidFields && !anyFieldsEmpty) {
                            String date = month + "/" + day + "/" + year;
                            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                            Date parsedDate;
                            try {
                                parsedDate = df.parse(date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            Double price = Double.parseDouble(priceText);

                            // Create a new Item
                            if (newItem == null) {
                                newItem = new Item(name, parsedDate, description, make, model, serial, price, comments);
                            } else {
                                newItem.setUniqueId(new UniqueId());
                                newItem.setName(name);
                                newItem.setPurchaseDate(parsedDate);
                                newItem.setDescription(description);
                                newItem.setMake(make);
                                newItem.setModel(model);
                                newItem.setSerialNumber(serial);
                                newItem.setValue(price);
                                newItem.setComment(comments);
                            }

                            // Add tags from the chipGroupTags to the newItem
                            for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                                Chip chip = (Chip) chipGroupTags.getChildAt(i);
                                Tag tag = new Tag(chip.getText().toString());
                                newItem.addTag(tag); // Use the new method in the Item class
                            }

                            // Call the listener with the new item
                            listener.onOKPressed(newItem);
                            listener.updateTotalCost();
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
                            if (!isValidDay(day)) {
                                itemDay.setError(("Invalid day"));
                            }
                            if (!isValidMonth(month)) {
                                itemMonth.setError(("Invalid month"));
                            }
                            if (!isValidYear(year)) {
                                itemYear.setError(("Invalid year"));
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
                            if (day.isEmpty()) {
                                itemDay.setError("Date required");
                            }
                            if (month.isEmpty()) {
                                itemMonth.setError("Month required");
                            }
                            if (year.isEmpty()) {
                                itemYear.setError("Year required");
                            }
                        }
                    }
                });
            }
        });
        return dialog;
    }

    /**
     * Validates the item name based on a maximum character limit.
     *
     * @param name The name to validate.
     * @return True if the name is valid, false otherwise.
     */
    private boolean isValidName(String name) {
        if (name.length() > 15) {
            return false;
        }
        return true;
    }

    /**
     * Validates the item variable based on a maximum character limit.
     *
     * @param description The variable to validate.
     * @return True if the variable is valid, false otherwise.
     */
    private boolean isValidDescription(String description) {
        if (description.length() > 50) {
            return false;
        }
        return true;
    }

    /**
     * Validates the item variable based on a maximum character limit.
     *
     * @param model The variable to validate.
     * @return True if the variable is valid, false otherwise.
     */
    private boolean isValidModel(String model) {
        if (model.length() > 20) {
            return false;
        }
        return true;
    }

    /**
     * Validates the item variable based on a maximum character limit.
     *
     * @param make The variable to validate.
     * @return True if the variable is valid, false otherwise.
     */
    private boolean isValidMake(String make) {
        if (make.length() > 20) {
            return false;
        }
        return true;
    }

    /**
     * Validates the item price based on regex
     *
     * @param price The variable to validate.
     * @return True if the name is valid, false otherwise.
     */
    private boolean isValidPrice(String price) {
        String priceText = String.valueOf(price);
        return priceText.matches("^(0\\.\\d{1,2}|[1-9]\\d*\\.?\\d{0,2})$");
    }

    /**
     * Validates the item variable based on a maximum character limit.
     *
     * @param comment The variable to validate.
     * @return True if the variable is valid, false otherwise.
     */
    private boolean isValidComment(String comment) {
        if (comment.length() > 25) {
            return false;
        }
        return true;
    }

    /**
     * Validates the day if it is between 1 and 31
     *
     * @param day The variable to validate.
     * @return True if the variable is valid, false otherwise.
     */
    private boolean isValidDay(String day) {
        if (!day.isEmpty()) {
            int dayValue = Integer.parseInt(day);
            return dayValue >= 1 && dayValue <= 31;
        }
        return false;
    }

    /**
     * Validates the month if it is between 1 and 12
     *
     * @param month The variable to validate.
     * @return True if the variable is valid, false otherwise.
     */
    private boolean isValidMonth(String month) {
        if (!month.isEmpty()) {
            int monthValue = Integer.parseInt(month);
            return monthValue >= 1 && monthValue <= 12;
        }
        return false;
    }

    /**
     * Validates the year based on regex
     *
     * @param year The variable to validate.
     * @return True if the variable is valid, false otherwise.
     */
    private boolean isValidYear(String year) {
        if (!year.isEmpty() && year.matches("\\d{4}")) {
            return true;
        }
        return false;
    }

    /**
     * Initiates the process of capturing an image using the device's camera. Creates a new image URI
     * and launches the camera application with the specified image capture intent. The captured image
     * is stored at the provided URI.
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
     * Callback for handling the result of the camera activity. Logs the image URI if the capture is
     * successful; otherwise, handles errors appropriately.
     *
     * @param result The result of the camera activity.
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
     * Launches the request for camera permission using the appropriate launcher. Ensures that the
     * application has the necessary permissions to access the device's camera.
     */
    private void requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    /**
     * Uses ML Kit Barcode Scanning to process the captured image and extract barcode information.
     * Handles success and failure cases, displaying an error message if barcode parsing is unsuccessful.
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
     * Extracts information from a list of barcodes, displaying an error message if the list is empty.
     * For each barcode, retrieves bounding box details, corner points, and the raw value. Logs the raw
     * value and sets it as the text for the item's serial number EditText.
     *
     * @param barcodes List of barcodes to extract information from.
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
}
