package vn.daoanhvu.assignmenttwo.activity;

import android.content.Intent;
import android.os.Bundle;

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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ShapeableImageView profileImage=  findViewById(R.id.profileImage);
        if (currentUser != null) {
            String photoUrl = Objects.requireNonNull(currentUser.getPhotoUrl()).toString();
            Picasso.get().load(photoUrl).into(profileImage);
        }

        MaterialButton logOutButton = findViewById(R.id.logout);

        logOutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        });
    }
}