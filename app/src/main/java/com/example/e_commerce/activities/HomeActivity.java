package com.example.e_commerce.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.e_commerce.R;
import com.example.e_commerce.base_fragments.CartFragment;
import com.example.e_commerce.base_fragments.FavoriteFragment;
import com.example.e_commerce.base_fragments.HomeFragment;
import com.example.e_commerce.base_fragments.UserFragment;
import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity {

    private final int ID_HOME = 1, ID_CART = 2, ID_FAVORITE = 3, ID_USER = 4;

    private FirebaseAuth.AuthStateListener listener;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener != null)
            mAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        MeowBottomNavigation bottomNavigation = findViewById(R.id.meow);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_CART, R.drawable.ic_shopping_cart));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_FAVORITE, R.drawable.ic_favourite_nav_bar));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_USER, R.drawable.ic_user));


        mAuth = FirebaseAuth.getInstance();


        listener = firebaseAuth -> {
            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                finish();
            }
        };


        bottomNavigation.setOnShowListener(model -> {
            Fragment fragment = null;
            switch (model.getId()) {
                case ID_HOME:
                    fragment = new HomeFragment();
                    break;
                case ID_CART:
                    fragment = new CartFragment();
                    break;
                case ID_FAVORITE:
                    fragment = new FavoriteFragment();
                    break;
                case ID_USER:
                    fragment = new UserFragment();
                    break;
            }
            loadSelectedFragment(fragment);
            return null;
        });

        bottomNavigation.show(ID_HOME, false);

    }

    private void loadSelectedFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container, fragment).commit();
    }
}