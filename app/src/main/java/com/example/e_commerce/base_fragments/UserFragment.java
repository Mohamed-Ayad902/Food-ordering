package com.example.e_commerce.base_fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce.R;
import com.example.e_commerce.activities.RequestStatusActivity;
import com.example.e_commerce.adapters.RequestsAdapter;
import com.example.e_commerce.models.Request;
import com.example.e_commerce.models.Statics;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class UserFragment extends Fragment {
    private RecyclerView recyclerView;
    private RequestsAdapter adapter;
    private TextView btn_logOut;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switch_notifications;
    private boolean isAllow;


    private static final String TAG = "UserFragment";

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_logOut.setOnClickListener(v -> FirebaseAuth.getInstance().signOut());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        isAllow = preferences.getBoolean("allowNotification", true);
        switch_notifications.setChecked(isAllow);

        switch_notifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isAllow = true;
                subscribe();
            } else {
                isAllow = false;
                unSubscribe();
            }
        });
    }


    private void subscribe() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("allowNotification", true);
        editor.apply();

        FirebaseMessaging.getInstance().subscribeToTopic("news")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "mohamed subTopic failed onComplete: " + Objects.requireNonNull(task.getException()).getCause());
                    } else {
                        Log.d(TAG, "mohamed subTopic onComplete: ");
                    }

                });

    }

    private void unSubscribe() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("allowNotification", false);
        editor.apply();
        FirebaseMessaging.getInstance().unsubscribeFromTopic("news")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "mohamed unSubTopic onComplete: " + Objects.requireNonNull(task.getException()).getCause());
                    } else
                        Log.d(TAG, "mohamed unSubTopic onComplete: ");
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        recyclerView = view.findViewById(R.id.user_recyclerView);
        btn_logOut = view.findViewById(R.id.user_logOut);
        switch_notifications = view.findViewById(R.id.switch_notifications);

        init();
        return view;
    }


    private void init() {
//        Query query = FirebaseFirestore.getInstance().collection(Statics.COLLECTION_REQUESTS)
//                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .orderBy("status", Query.Direction.ASCENDING);
//
//        FirestoreRecyclerOptions<Request> options = new FirestoreRecyclerOptions.Builder<Request>()
//                .setQuery(query, Request.class)
//                .build();
//
//        adapter = new RequestsAdapter(options, getActivity(), new RequestsAdapter.OnRequestClickListener() {
//            @Override
//            public void OnClick(Request request, String requestId) {
//                Statics.setInstance(request);
//                Statics.setRequestId(requestId);
//                startActivity(new Intent(getActivity(), RequestStatusActivity.class));
//            }
//        });
//
////        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setItemAnimator(null);


        Query query = FirebaseFirestore.getInstance().collection(Statics.COLLECTION_REQUESTS)
                .orderBy("status", Query.Direction.ASCENDING)
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirestoreRecyclerOptions<Request> options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(query, Request.class)
                .build();


        adapter = new RequestsAdapter(options, getActivity(), new RequestsAdapter.OnRequestClickListener() {
            @Override
            public void OnClick(Request request, String requestId) {
                Toast.makeText(getActivity(), "" + request.getStatus(), Toast.LENGTH_SHORT).show();
            }
        });

//        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);


    }

}