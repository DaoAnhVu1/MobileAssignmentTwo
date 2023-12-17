package vn.daoanhvu.assignmenttwo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.adapter.SiteAdapter;
import vn.daoanhvu.assignmenttwo.model.Site;

public class JoinedSiteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joined_site_activity);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
        GridView gridView = findViewById(R.id.siteGrid);
        List<Site> siteList = new ArrayList<>();
        SiteAdapter siteAdapter = new SiteAdapter(siteList);
        gridView.setAdapter(siteAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("sites")
                .whereArrayContains("participants", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Site site = document.toObject(Site.class);
                                site.setId(document.getId());
                                siteList.add(site);
                            }

                            Collections.sort(siteList, (site1, site2) -> compareDates(site1.getDate(), site2.getDate()));

                            siteAdapter.notifyDataSetChanged();
                        }
                    }
                });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Site clickedSite = siteList.get(position);

                Intent intent = new Intent(JoinedSiteActivity.this, SiteDetailsForParticipantActivity.class);
                intent.putExtra("site", clickedSite);
                startActivity(intent);
            }
        });
    }

    private int compareDates(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        try {
            Date date1Obj = sdf.parse(date1);
            Date date2Obj = sdf.parse(date2);
            return date1Obj.compareTo(date2Obj);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
