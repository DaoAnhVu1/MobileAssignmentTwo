package vn.daoanhvu.assignmenttwo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import vn.daoanhvu.assignmenttwo.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_activity);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v-> {
            onBackPressed();
        });
    }
}