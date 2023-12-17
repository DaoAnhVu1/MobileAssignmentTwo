package vn.daoanhvu.assignmenttwo.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.adapter.UserListAdapter;
import vn.daoanhvu.assignmenttwo.model.User;

public class AdminUsersActivity extends AppCompatActivity {
    private List<User> userList;
    private UserListAdapter userListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_users_activity);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v-> {
            onBackPressed();
        });
        ListView listView = findViewById(R.id.userList);
        userList = new ArrayList<>();
        userListAdapter = new UserListAdapter(userList);
        listView.setAdapter(userListAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            if (user.getUsername().equals("Admin")) {
                                continue;
                            }
                            userList.add(user);
                        }
                        userListAdapter.notifyDataSetChanged();
                    }
                });
    }
}
