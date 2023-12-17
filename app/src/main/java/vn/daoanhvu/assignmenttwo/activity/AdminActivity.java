package vn.daoanhvu.assignmenttwo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RelativeLayout;

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
            SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(AdminActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        });

        RelativeLayout site = findViewById(R.id.sites);
        RelativeLayout user = findViewById(R.id.user);

        site.setOnClickListener(v-> {
            Intent intent = new Intent(AdminActivity.this, AdminSitesActivity.class);
            startActivity(intent);
        });

        user.setOnClickListener(v-> {
            Intent intent = new Intent(AdminActivity.this, AdminUsersActivity.class);
            startActivity(intent);
        });
    }
}