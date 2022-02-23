package com.example.e_commerce.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.e_commerce.R;
import com.example.e_commerce.adapters.SliderAdapterExample;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.models.Statics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";

    private TextView textView;
    private Animation animation;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        textView = findViewById(R.id.tv_shopEasy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        animation = AnimationUtils.loadAnimation(this, R.anim.scale_animation);

        textView.setAnimation(animation);


        productList = new ArrayList<>();

        Query query = FirebaseFirestore.getInstance().collection(Statics.COLLECTION_ALL_PRODUCTS).whereGreaterThanOrEqualTo("discount", 1).limit(6);
        query.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Product productModel = document.toObject(Product.class);
                    if (productModel != null) {
                        productModel.setId(document.getId());
                        productList.add(productModel);
                    }
                }
                Statics.setProductList(productList);
                Log.d(TAG, "mohamed statics done onCreate: " + productList.size());

                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
        });


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 4000);


    }
}