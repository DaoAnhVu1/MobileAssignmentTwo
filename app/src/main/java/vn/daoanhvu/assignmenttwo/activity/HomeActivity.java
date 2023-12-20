package vn.daoanhvu.assignmenttwo.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import vn.daoanhvu.assignmenttwo.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task-> {
            if (task.isSuccessful()) {
                System.out.println(task.getResult());
            }
        });
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        RelativeLayout findSite = findViewById(R.id.findSite);
        RelativeLayout siteCenter = findViewById(R.id.siteCenter);
        RelativeLayout aboutUs = findViewById(R.id.aboutUs);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ShapeableImageView profileImage=  findViewById(R.id.profileImage);
        if (currentUser != null) {
            String photoUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;

            if (photoUrl != null) {
                Picasso.get().load(photoUrl).into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.profile_image);
            }
        }

        findSite.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FindSiteActivity.class);
            startActivity(intent);
        });

        siteCenter.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SiteCenterActivity.class);
            startActivity(intent);
        });

        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        aboutUs.setOnClickListener(v-> {
            Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });
    }
}