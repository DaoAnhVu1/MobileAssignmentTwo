package vn.daoanhvu.assignmenttwo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import vn.daoanhvu.assignmenttwo.R;

public class SiteCenterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_center_activity);
        ShapeableImageView profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(SiteCenterActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        RelativeLayout yourSite = findViewById(R.id.yourSite);
        RelativeLayout joinedSite = findViewById(R.id.joinedSite);
        yourSite.setOnClickListener(v -> {
            Intent intent = new Intent(SiteCenterActivity.this, CreatedSiteActivity.class);
            startActivity(intent);
        });
        joinedSite.setOnClickListener(v-> {
            Intent intent = new Intent(SiteCenterActivity.this, JoinedSiteActivity.class);
            startActivity(intent);
        });
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
        TextView logo = findViewById(R.id.logo);
        logo.setOnClickListener(v -> {
            onBackPressed();
        });

    }
}