package vn.daoanhvu.assignmenttwo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.model.Site;

public class SiteDetailsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_details_activity);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

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
            Intent intent = getIntent();
            boolean joined = intent.getBooleanExtra("joined", false);
            MaterialButton joinButton = findViewById(R.id.joinButton);
            if (!joined) {
                joinButton.setOnClickListener(v -> {
                    joinSite(site.getId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                });
            } else  {
                    joinButton.setText("View summary");
                    joinButton.setOnClickListener(v-> {
                        showSummaryDialog(site.getId());
                    });
            }

        }
    }

    private void showSummaryDialog(String siteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.summarize_dialog_for_user, null);

      TextView keywordEditText = dialogView.findViewById(R.id.keywordEditText);

        DocumentReference siteRefONE = db.collection("sites").document(siteId);
        siteRefONE.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("summary")) {
                    String summary = documentSnapshot.getString("summary");
                    if (summary == null || summary.isEmpty()) {
                        keywordEditText.setText("The owner of this site have not updated this");
                    } else {
                        keywordEditText.setText(summary);
                    }
                }
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });

        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void joinSite(String siteId, String userId) {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("user").document(userId);

        userRef.update("joinedSites", FieldValue.arrayUnion(siteId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("joinSite", "User joined site successfully");
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    Log.e("joinSite", "Error joining site for user: " + e.getMessage());
                    // Handle failure
                });

        DocumentReference siteRef = FirebaseFirestore.getInstance().collection("sites").document(siteId);
        siteRef.update("participants", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("joinSite", "User added as participant to site successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("joinSite", "Error adding user as participant to site: " + e.getMessage());
                    // Handle failure
                });

        Intent intent = new Intent(SiteDetailsActivity.this, SiteCenterActivity.class);
        startActivity(intent);
    }
}
