package vn.daoanhvu.assignmenttwo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import vn.daoanhvu.assignmenttwo.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ImageView backButton = findViewById(R.id.backButton);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ShapeableImageView profileImage=  findViewById(R.id.profileImage);
        if (currentUser != null) {
            String photoUrl = Objects.requireNonNull(currentUser.getPhotoUrl()).toString();
            Picasso.get().load(photoUrl).into(profileImage);
        }

        backButton.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}