package vn.daoanhvu.assignmenttwo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.model.Site;

public class SiteDetailsActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_details_activity);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        Site site = (Site) getIntent().getSerializableExtra("site");

        if (site != null) {
            TextView titleTextView = findViewById(R.id.title);
            titleTextView.setText("Site Details");

            ImageView imageView = findViewById(R.id.image);
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
            MaterialButton joinButton = findViewById(R.id.joinButton);
                joinButton.setOnClickListener(v -> {
                    joinSite(site.getId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                });
            }
    }


    private void joinSite(String siteId, String userId) {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("user").document(userId);

        userRef.update("joinedSites", FieldValue.arrayUnion(siteId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("joinSite", "User joined site successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("joinSite", "Error joining site for user: " + e.getMessage());
                });

        DocumentReference siteRef = FirebaseFirestore.getInstance().collection("sites").document(siteId);
        siteRef.update("participants", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("joinSite", "User added as participant to site successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("joinSite", "Error adding user as participant to site: " + e.getMessage());
                });
        siteRef.get().addOnCompleteListener(task-> {
            if (task.isSuccessful()) {
               String ownerId = (String) task.getResult().get("ownerId");
               DocumentReference ownerRef = FirebaseFirestore.getInstance().collection("user").document(ownerId);
               ownerRef.get().addOnCompleteListener(ownerTask-> {
                   if (ownerTask.isSuccessful()) {
                       String fcmToken = (String) ownerTask.getResult().get("fcmToken");
                       sendNotification(fcmToken, "New participant", "There is a new participant join your site");
                   }
               });
            }
        });
        Intent intent = new Intent(SiteDetailsActivity.this, SiteCenterActivity.class);
        startActivity(intent);
    }

    private void sendNotification(String fcmToken, String title, String message) {
        new Thread(() -> {
            try {
                String fcmEndpoint = "https://fcm.googleapis.com/fcm/send";

                String serverKey = "AAAA2iyNkOw:APA91bH_h81tFopnGb-3huNR6c94mTblLpW4X8m9yv9Rl5SCdpeWA4_JMxI1qanTrB4A_eB3IJ_LuIOJo42BP_mT9pQEjeDKdXfOuuSDBQE_vl_G_3gUEHxUPiWUrhCOpQ2T26fGEiWW"; // Replace with your actual server key

                String jsonPayload = String.format(
                        "{\"to\":\"%s\",\"notification\":{\"title\":\"%s\",\"body\":\"%s\"}}",
                        fcmToken, title, message);

                URL url = new URL(fcmEndpoint);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Authorization", "key=" + serverKey);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(jsonPayload);
                outputStreamWriter.flush();
                outputStreamWriter.close();
                outputStream.close();

                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("FCM_NOTIFICATION", "Notification sent successfully!");
                } else {
                    // Log an error message for non-OK response codes
                    Log.e("FCM_NOTIFICATION", "Failed to send notification. Response Code: " + responseCode);
                }
                httpURLConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
