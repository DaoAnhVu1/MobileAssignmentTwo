package vn.daoanhvu.assignmenttwo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import vn.daoanhvu.assignmenttwo.R;

public class SiteCenterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_center_activity);
        ShapeableImageView profileImage = findViewById(R.id.profileImage);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String photoUrl = Objects.requireNonNull(currentUser.getPhotoUrl()).toString();
            Picasso.get().load(photoUrl).into(profileImage);
        }
        TextView logo = findViewById(R.id.logo);
        logo.setOnClickListener(v -> {
            Intent intent = new Intent(SiteCenterActivity.this, HomeActivity.class);
            startActivity(intent);
        });

    }
}