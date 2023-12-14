package vn.daoanhvu.assignmenttwo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import vn.daoanhvu.assignmenttwo.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ImageView backButton = findViewById(R.id.backButton);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
            SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // Clear all data
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
                    .update("fcmToken", null)  ;
        }

        FirebaseMessaging.getInstance().deleteToken();
    }
}