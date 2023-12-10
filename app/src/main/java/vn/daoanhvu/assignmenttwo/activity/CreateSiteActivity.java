package vn.daoanhvu.assignmenttwo.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.daoanhvu.assignmenttwo.R;

public class CreateSiteActivity extends AppCompatActivity {

    LinearLayout dateEdit;
    LinearLayout timeEdit;
    LinearLayout addressSection;

    EditText nameEdit;
    TextView dateEditText;
    TextView timeEditText;
    TextView addressText;
    MaterialButton submitButton;

    ImageView imageView;
    private Uri selectedImageUri;
    private String uploadedImageUrl;
    private String storedLatlng;

    private static final int LOCATION_PICKER_REQUEST_CODE = 1;
    private static final int IMAGE_PICKER_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_site_activity);

        nameEdit = findViewById(R.id.nameEdit);
        dateEdit = findViewById(R.id.dateSection);
        timeEdit = findViewById(R.id.timeSection);
        dateEditText = findViewById(R.id.dateText);
        timeEditText = findViewById(R.id.timeText);
        addressSection = findViewById(R.id.addressSection);
        addressText = findViewById(R.id.addressEdit);
        submitButton = findViewById(R.id.submitButton);
        imageView = findViewById(R.id.imageView);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> onBackPressed());

        // Set onClickListener for dateEdit
        dateEdit.setOnClickListener(v -> showDatePicker());

        // Set onClickListener for timeEdit
        timeEdit.setOnClickListener(v -> showTimePicker());

        addressSection.setOnClickListener(v -> {
            Intent intent = new Intent(CreateSiteActivity.this, LocationPickerActivity.class);
            startActivityForResult(intent, LOCATION_PICKER_REQUEST_CODE);
        });

        imageView.setOnClickListener(v -> openImagePicker());

        submitButton.setOnClickListener(v -> createSite());

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE);
    }

    private void createSite() {
        String name = nameEdit.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String address = addressText.getText().toString().trim();

        if (name.isEmpty()  || date.isEmpty() || time.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return; // Exit the method if any field is empty
        }

        if (storedLatlng == null || storedLatlng.isEmpty()) {
            Toast.makeText(this, "Location not selected", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> siteData = new HashMap<>();
        siteData.put("name", name);
        siteData.put("date", date);
        siteData.put("time", time);
        siteData.put("ownerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        siteData.put("address", address);
        siteData.put("imageUrl", uploadedImageUrl);
        siteData.put("latlng", storedLatlng);

        // Add the siteData to Firebase (replace "sites" with your desired collection name)
        FirebaseFirestore.getInstance().collection("sites")
                .add(siteData)
                .addOnSuccessListener(documentReference -> {
                    // Document was successfully added
                    Toast.makeText(this, "Site created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(this, "Error creating site", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update dateEdit with the selected date
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%02d", selectedDay, selectedMonth + 1, selectedYear % 100);
                    dateEditText.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // Update timeEdit with the selected time
                    String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeEditText.setText(selectedTime);
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle the result from LocationPickerActivity
            if (data != null) {
                String selectedAddress = data.getStringExtra("selectedAddress");
                String selectedLat = data.getStringExtra("selectedLat");
                String selectedLong = data.getStringExtra("selectedLng");
                storedLatlng = selectedLat + "," + selectedLong;
                addressText.setText(selectedAddress);
            }
        } else if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Handle the result from image picker
            selectedImageUri = data.getData();
            // Upload the image to Firebase Storage and update the imageView on successful upload
            uploadImageToFirebase();
        }
    }

    private String generateImageName() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String randomString = Long.toHexString(Double.doubleToLongBits(Math.random()));
        return "image_" + timeStamp + "_" + randomString + ".jpg";
    }

    private void uploadImageToFirebase() {
        if (selectedImageUri != null) {
            String imageName = generateImageName();
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images").child(imageName);

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Use Picasso to load the image into the ImageView
                            Picasso.get().load(uri.toString()).into(imageView);

                            // Save the downloaded image URL
                            uploadedImageUrl = uri.toString();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful image upload
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
