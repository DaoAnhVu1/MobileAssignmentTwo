package vn.daoanhvu.assignmenttwo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import vn.daoanhvu.assignmenttwo.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        RelativeLayout findSite = findViewById(R.id.findSite);
        RelativeLayout createSite = findViewById(R.id.createSite);
        RelativeLayout siteCenter = findViewById(R.id.siteCenter);
        RelativeLayout aboutUs = findViewById(R.id.aboutUs);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ShapeableImageView profileImage=  findViewById(R.id.profileImage);
        if (currentUser != null) {
            String photoUrl = Objects.requireNonNull(currentUser.getPhotoUrl()).toString();
            Picasso.get().load(photoUrl).into(profileImage);
        }

        findSite.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FindSiteActivity.class);
            startActivity(intent);
        });

        createSite.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CreateSiteActivity.class);
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
    }
}