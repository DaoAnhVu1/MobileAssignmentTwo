package vn.daoanhvu.assignmenttwo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import vn.daoanhvu.assignmenttwo.R;

public class AuthenticationActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_activity);

        TextView textViewAlready = findViewById(R.id.already);
        textViewAlready.setOnClickListener(v -> {
            startActivity(new Intent(AuthenticationActivity.this, RegisterActivity.class));
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        MaterialButton googleSignInButton = findViewById(R.id.googleSignIn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
        googleSignInButton.setOnClickListener(view -> signInWithGoogle());

        MaterialButton signInButton = findViewById(R.id.signIn);
        signInButton.setOnClickListener(view -> signInWithEmailPassword());
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInWithEmailPassword() {
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String userId = mAuth.getCurrentUser().getUid();
                        db.collection("user").document(userId).get().addOnCompleteListener(documentTask -> {
                            if (documentTask.isSuccessful()) {
                                DocumentSnapshot document = documentTask.getResult();
                                if (!document.exists()) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("userId", userId);
                                    user.put("email", email);
                                    db.collection("user")
                                            .document(userId)
                                            .set(user)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("VUI", "DocumentSnapshot added with ID: " + userId);
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w("VUI", "Error adding document", e);
                                            });
                                } else if (document.exists()) {
                                    checkUserRoleAndRedirect(document);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(AuthenticationActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    private void checkUserRoleAndRedirect(DocumentSnapshot document) {
        Object roleObj = document.get("role");

        // Check for null before comparing
        if (roleObj != null && roleObj.equals("admin")) {
            Intent intent = new Intent(AuthenticationActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.getIdToken(), null))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String userId = mAuth.getCurrentUser().getUid();
                        String userName = mAuth.getCurrentUser().getDisplayName();
                        String email = mAuth.getCurrentUser().getEmail();
                        db.collection("user").document(userId).get().addOnCompleteListener(documentTask -> {
                            if (documentTask.isSuccessful()) {
                                DocumentSnapshot document = documentTask.getResult();
                                if (!document.exists()) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("userId", userId);
                                    user.put("username", userName);
                                    user.put("email", email);
                                    db.collection("user")
                                            .document(userId)
                                            .set(user)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("VUI", "DocumentSnapshot added with ID: " + userId);
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w("VUI", "Error adding document", e);
                                            });
                                } else if (document.exists()) {
                                    checkUserRoleAndRedirect(document);
                                }
                            } else {
                                // Handle the error
                                Log.w("BUON", "Error getting document", documentTask.getException());
                            }
                        });
                    }
                });
    }
}

