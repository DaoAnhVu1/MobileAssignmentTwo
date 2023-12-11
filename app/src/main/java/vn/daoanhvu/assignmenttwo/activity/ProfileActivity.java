package vn.daoanhvu.assignmenttwo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
            Intent intent = new Intent(ProfileActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        });

        backButton.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}