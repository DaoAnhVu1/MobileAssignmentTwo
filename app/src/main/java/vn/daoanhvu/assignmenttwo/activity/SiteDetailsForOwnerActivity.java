package vn.daoanhvu.assignmenttwo.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.model.Site;

public class SiteDetailsForOwnerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_details_for_owner_activity);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Retrieve data from the intent
        Site site = (Site) getIntent().getSerializableExtra("site");

        // Check if the data is not null
        if (site != null) {
            // Set the data to your UI elements
            TextView titleTextView = findViewById(R.id.title);
            titleTextView.setText("Site Details");

            ImageView imageView = findViewById(R.id.image);
            // Use Picasso to load the image
            Picasso.get().load(site.getImageUrl()).into(imageView);

            TextView siteNameTextView = findViewById(R.id.siteName);
            siteNameTextView.setText("Name: " + site.getName());

            TextView siteDateTextView = findViewById(R.id.siteDate);
            siteDateTextView.setText("Date: " + site.getDate());

            TextView siteTimeTextView = findViewById(R.id.siteTime);
            siteTimeTextView.setText("Time: " + site.getTime());

            TextView siteAddressTextView = findViewById(R.id.siteAddress);
            siteAddressTextView.setText("Address: " + site.getAddress());

            MaterialButton updateButton = findViewById(R.id.updateButton);
            updateButton.setOnClickListener(v -> showSummaryDialog());
        }
    }

    private void showSummaryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.summarize_dialog, null);

        EditText keywordEditText = dialogView.findViewById(R.id.keywordEditText);
        MaterialButton cancelFilter = dialogView.findViewById(R.id.cancelFilter);
        MaterialButton submitFilter = dialogView.findViewById(R.id.submitFilter);

        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

        cancelFilter.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        submitFilter.setOnClickListener(v -> {
            String keyword = keywordEditText.getText().toString();

            alertDialog.dismiss();
        });

    }
}
