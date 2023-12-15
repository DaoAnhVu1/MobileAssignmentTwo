package vn.daoanhvu.assignmenttwo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.List;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.model.Site;

public class ProfileActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        ImageView backButton = findViewById(R.id.backButton);
        TextView siteJoinedText = findViewById(R.id.siteJoined);
        TextView siteCreatedText = findViewById(R.id.siteCreated);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userRef = db.collection("user").document(currentUser.getUid());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Object joinedSite = document.get("joinedSites");
                    if (joinedSite instanceof List) {
                        siteJoinedText.setText(Integer.toString(((List<?>) joinedSite).size()));
                    }
                }
            }
        });

        db.collection("sites").whereEqualTo("ownerId", currentUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                siteCreatedText.setText(Integer.toString(task.getResult().size()));
            }
        });

        ShapeableImageView profileImage = findViewById(R.id.profileImage);
        if (currentUser != null) {
            String photoUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;

            if (photoUrl != null) {
                // Load the user's profile image using Picasso
                Picasso.get().load(photoUrl).into(profileImage);
            } else {
                // Set a default image from your drawable resources
                profileImage.setImageResource(R.drawable.profile_image);
            }
        }

        MaterialButton logoutButton = findViewById(R.id.logout);

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(ProfileActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            deleteFCMToken();
            finish();
        });

        backButton.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void deleteFCMToken() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user").document(userId)
                    .update("fcmToken", null);
        }

        FirebaseMessaging.getInstance().deleteToken();
    }
}
