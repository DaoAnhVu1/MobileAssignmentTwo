package vn.daoanhvu.assignmenttwo.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.adapter.UserListAdapter;
import vn.daoanhvu.assignmenttwo.model.Site;
import vn.daoanhvu.assignmenttwo.model.User;

public class AdminSiteDetails extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listView;
    private List<User> userList;
    private UserListAdapter userListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_site_details_activity);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

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

            TextView ownerTextView = findViewById(R.id.owner);
            ownerTextView.setText("Owner: " + site.getOwnerName());

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
}