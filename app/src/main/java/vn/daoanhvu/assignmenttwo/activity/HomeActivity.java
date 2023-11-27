package vn.daoanhvu.assignmenttwo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.daoanhvu.assignmenttwo.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        MaterialButton logOutButton = findViewById(R.id.logout);

        TextView textView = findViewById(R.id.home);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        textView.setText(currentUser.getDisplayName());

        logOutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        });
    }
}