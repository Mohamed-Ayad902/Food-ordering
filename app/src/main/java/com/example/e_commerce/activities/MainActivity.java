package com.example.e_commerce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_commerce.R;
import com.example.e_commerce.models.Statics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_login = findViewById(R.id.btn_login);


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getBaseContext(), HomeActivity.class));
            finish();
        }

        btn_login.setOnClickListener(v -> {

            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create and launch sign-in intent
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTheme(R.style.LoginTheme)
                    .build();
            signInLauncher.launch(signInIntent);

        });

    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                Log.d(TAG, "mohamed onSignInResult: new user created -> " + user.getEmail());
                storeUser(user);
            } else
                Log.d(TAG, "mohamed onSignInResult: user returned back -> " + user.getEmail());

            String token = Statics.getTokenInstance();
            FirebaseFirestore.getInstance().collection(Statics.COLLECTION_USERS)
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .update("userToken", token).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "mohamed update token success : " + token);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Log.e(TAG, "mohamed update token onFailure: " + e.getCause());
                    Log.e(TAG, "mohamed update token onFailure: " + e.getMessage());
                }
            });

            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            startActivity(intent);
            finish();

        } else {
            if (response == null)
                Log.e(TAG, "mohamed onSignInResult: user canceled singing in  ");
            else {
                Toast.makeText(this, "" + Objects.requireNonNull(response.getError()).getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "mohamed onSignInResult: ", response.getError().getCause());
                Log.e(TAG, "mohamed onSignInResult: " + response.getError().getMessage());
            }
        }
    }

    private void storeUser(FirebaseUser firebaseUser) {

        Map<String, Object> user = new HashMap<>();
        user.put("userId", firebaseUser.getUid());
        user.put("userEmail", firebaseUser.getEmail());
        user.put("userPhoneNumber", firebaseUser.getPhoneNumber());
        user.put("isStaff", false);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "mohamed Fetching FCM registration token failed", task.getException());

                        FirebaseFirestore.getInstance().collection(Statics.COLLECTION_USERS)
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                .set(user)
                                .addOnSuccessListener(documentReference -> Log.d(TAG, "mohamed adding user onSuccess: "))
                                .addOnFailureListener(e -> Log.e(TAG, "mohamed onFailure: "));
                        return;
                    }
                    String token = task.getResult();
                    Log.d(TAG, "mohamed token onComplete: " + token);
                    user.put("userToken", token);

                    FirebaseFirestore.getInstance().collection(Statics.COLLECTION_USERS)
                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .set(user)
                            .addOnSuccessListener(documentReference -> Log.d(TAG, "mohamed adding user onSuccess: "))
                            .addOnFailureListener(e -> Log.e(TAG, "mohamed onFailure: "));
                });

    }

}