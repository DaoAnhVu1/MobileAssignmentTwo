package vn.daoanhvu.assignmenttwo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.adapter.SiteAdapter;
import vn.daoanhvu.assignmenttwo.model.Site;

public class CreatedSiteActivity extends AppCompatActivity {
    private GridView siteListView;
    private List<Site> siteList;
    private SiteAdapter siteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.created_site_activity);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v-> {
            onBackPressed();
        });
        MaterialButton createButton = findViewById(R.id.create);
        createButton.setOnClickListener(v-> {
            Intent intent = new Intent(CreatedSiteActivity.this, CreateSiteActivity.class);
            startActivity(intent);
        });
        siteListView = findViewById(R.id.siteGrid);
        siteList = new ArrayList<>();
        siteAdapter = new SiteAdapter(siteList);
        siteListView.setAdapter(siteAdapter);
        siteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Site clickedSite = siteList.get(position);

                Intent intent = new Intent(CreatedSiteActivity.this, SiteDetailsForOwnerActivity.class);
                intent.putExtra("site", clickedSite);

                startActivity(intent);
            }
        });
        fetchDataFromFirestore();
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

    private void fetchDataFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }
        siteList.clear();
        db.collection("sites")
                .whereEqualTo("ownerId", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Site site = document.toObject(Site.class);
                                site.setId(document.getId());
                                List<String> participants = site.getParticipants();
                                if (participants == null || !participants.contains(currentUser.getUid())) {
                                    siteList.add(site);
                                }
                            }

                            Collections.sort(siteList, new Comparator<Site>() {
                                @Override
                                public int compare(Site site1, Site site2) {
                                    return compareDates(site1.getDate(), site2.getDate());
                                }
                            });

                            siteAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}