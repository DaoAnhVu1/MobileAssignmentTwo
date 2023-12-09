package vn.daoanhvu.assignmenttwo.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

import vn.daoanhvu.assignmenttwo.R;

public class CreateSiteActivity extends AppCompatActivity {

    LinearLayout dateEdit;
    LinearLayout timeEdit;
    LinearLayout addressSection;

    TextView dateEditText;
    TextView timeEditText;

    TextView addressText;

    private static final int LOCATION_PICKER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_site_activity);

        dateEdit = findViewById(R.id.dateSection);
        timeEdit = findViewById(R.id.timeSection);
        dateEditText = findViewById(R.id.dateText);
        timeEditText = findViewById(R.id.timeText);
        addressSection = findViewById(R.id.addressSection);
        addressText = findViewById(R.id.addressEdit);

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
                String selectedLatLng = data.getStringExtra("selectedLatLng");
                addressText.setText(selectedAddress);
                // Display a toast with the selected address and LatLng
                String toastMessage = "Selected Address: " + selectedAddress + "\nSelected LatLng: " + selectedLatLng;
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
