package vn.daoanhvu.assignmenttwo.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.adapter.SiteAdapter;
import vn.daoanhvu.assignmenttwo.model.Site;

public class FindSiteActivity extends AppCompatActivity {
    private GridView siteListView;
    private List<Site> siteList;
    private SiteAdapter siteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_site_activity);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
        MaterialButton filterButton = findViewById(R.id.filter_button);
        MaterialButton mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(FindSiteActivity.this, SitesOnMap.class);
            startActivity(intent);
        });
        filterButton.setOnClickListener(v -> showFilterDialog());
        siteListView = findViewById(R.id.siteGrid);
        siteList = new ArrayList<>();
        siteAdapter = new SiteAdapter(siteList);
        siteListView.setAdapter(siteAdapter);
        fetchDataFromFirestore();
        siteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Site clickedSite = siteList.get(position);
                Intent intent = new Intent(FindSiteActivity.this, SiteDetailsActivity.class);
                intent.putExtra("site", clickedSite);
                startActivity(intent);
            }
        });

    }

    private void fetchDataFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }
        siteList.clear();
        db.collection("sites")
                .whereNotEqualTo("ownerId", currentUser.getUid())
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

    private void showFilterDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_dialog, null);
        dialogBuilder.setView(dialogView);

        EditText keywordEditText = dialogView.findViewById(R.id.keywordEditText);

        TextView dateEdit = dialogView.findViewById(R.id.dateText);

        dateEdit.setOnClickListener(v -> showDatePicker(dateEdit));

        MaterialButton cancelFilterButton = dialogView.findViewById(R.id.cancelFilter);
        MaterialButton submitFilterButton = dialogView.findViewById(R.id.submitFilter);
        AlertDialog filterDialog = dialogBuilder.create();
        cancelFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataFromFirestore();
                filterDialog.dismiss();
            }
        });

        submitFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = keywordEditText.getText().toString();
                filterData(keyword, dateEdit.getText().toString());
                filterDialog.dismiss();
            }
        });
        filterDialog.show();
    }

    private void showDatePicker(TextView dateEdit) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%02d", selectedDay, selectedMonth + 1, selectedYear % 100);
                    dateEdit.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void filterData(String keyword, String date) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }

        siteList.clear();

        db.collection("sites")
                .whereNotEqualTo("ownerId", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Site site = document.toObject(Site.class);
                                site.setId(document.getId());
                                boolean isNameMatch = site.getName().toLowerCase().contains(keyword.toLowerCase());
                                List<String> participants = site.getParticipants();
                                boolean isDateMatch = date.isEmpty() || isDateAfter(site.getDate(), date);
                                if (isNameMatch && isDateMatch && (participants == null || !participants.contains(currentUser.getUid()))) {
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

    private boolean isDateAfter(String siteDate, String inputDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

        try {
            Date siteDateObj = sdf.parse(siteDate);
            Date inputDateObj = sdf.parse(inputDate);

            return ((Date) siteDateObj).after(inputDateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }
}