package vn.daoanhvu.assignmenttwo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import vn.daoanhvu.assignmenttwo.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);
        MaterialButton logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // Clear all data
            editor.apply();
            Intent intent = new Intent(AdminActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        });
    }
}