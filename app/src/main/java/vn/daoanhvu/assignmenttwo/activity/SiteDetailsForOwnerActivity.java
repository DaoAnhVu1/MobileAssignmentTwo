package vn.daoanhvu.assignmenttwo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.adapter.UserListAdapter;
import vn.daoanhvu.assignmenttwo.model.Site;
import vn.daoanhvu.assignmenttwo.model.User;

public class SiteDetailsForOwnerActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listView;
    private List<User> userList;
    private UserListAdapter userListAdapter;
    private Site siteSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_details_for_owner_activity);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        Site site = (Site) getIntent().getSerializableExtra("site");
        siteSave = site;

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

            MaterialButton updateButton = findViewById(R.id.updateButton);
            updateButton.setOnClickListener(v -> showSummaryDialog(site.getId()));
            TextView participantsText = findViewById(R.id.participants);
            List<String> participants = site.getParticipants();
            if (participants == null || participants.size() == 0) {
                participantsText.setText("There is no participant");
            } else {
                ListView listView = findViewById(R.id.listview);
                userList = new ArrayList<>();
                userListAdapter = new UserListAdapter(userList);
                listView.setAdapter(userListAdapter);
            }


            if (participants != null && !participants.isEmpty()) {
                for (String id : participants) {
                    db.collection("user").document(id).get().addOnSuccessListener(
                            new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        User user = documentSnapshot.toObject(User.class);
                                        userList.add(user);
                                        userListAdapter.notifyDataSetChanged();
                                    } else {
                                        System.out.println("Document does not exist");
                                    }
                                }
                            }
                    );
                }
            }

        }
    }

    private void showSummaryDialog(String siteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.summarize_dialog, null);

        EditText keywordEditText = dialogView.findViewById(R.id.keywordEditText);
        MaterialButton cancelFilter = dialogView.findViewById(R.id.cancelFilter);
        MaterialButton submitFilter = dialogView.findViewById(R.id.submitFilter);

        DocumentReference siteRefONE = db.collection("sites").document(siteId);
        siteRefONE.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                if (documentSnapshot.contains("summary")) {
                    String summary = documentSnapshot.getString("summary");
                    keywordEditText.setText(summary);
                }
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });

        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

        cancelFilter.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        submitFilter.setOnClickListener(v -> {
            String keyword = keywordEditText.getText().toString();
            DocumentReference siteRef = FirebaseFirestore.getInstance().collection("sites").document(siteId);

            siteRef.update("summary", keyword).addOnSuccessListener(aVoid -> {
                siteRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> participantIds = documentSnapshot.toObject(Site.class).getParticipants();

                        for (String participantId : participantIds) {
                            FirebaseFirestore.getInstance().collection("user").document(participantId).get()
                                    .addOnSuccessListener(userDocument -> {
                                        if (userDocument.exists()) {
                                            String fcmToken = userDocument.getString("fcmToken");

                                            sendNotification(fcmToken, "Site Updated", "Site " + siteSave.getName() + " just got its summary updated");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle any errors
                                    });
                        }
                    }
                });
            });

            alertDialog.dismiss();
        });
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
