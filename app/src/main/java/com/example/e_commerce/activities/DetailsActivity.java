package com.example.e_commerce.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.e_commerce.R;
import com.example.e_commerce.data.ProductDatabase;
import com.example.e_commerce.models.Order;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.models.Rating;
import com.example.e_commerce.models.Statics;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";


    private TextView textViewName, textViewPrice, textViewDescription, textViewShopName, textViewShopLocation, textViewRating, textViewAvailable;
    private Button btn_AddToCart;
    private ImageView imageView;


    private Product currentProduct;
    private int count = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        textViewName = findViewById(R.id.details_tv_Name);
        textViewPrice = findViewById(R.id.details_tv_price);
        textViewDescription = findViewById(R.id.details_tv_description);
        textViewShopName = findViewById(R.id.details_tv_shopName);
        textViewShopLocation = findViewById(R.id.details_tv_location);
        textViewRating = findViewById(R.id.details_tv_rating);
        textViewAvailable = findViewById(R.id.details_tv_available);
        btn_AddToCart = findViewById(R.id.details_button_addToCart);
        imageView = findViewById(R.id.fragment_details_imageView);

        if (getIntent() != null) {
            currentProduct = getIntent().getParcelableExtra(Statics.PRODUCT_EXTRA);
            getProduct();
        }

        btn_AddToCart.setOnClickListener(v -> showBottomDialog());

        List<Rating> ratingList = currentProduct.getRatings();

    }

    @SuppressLint("SetTextI18n")
    private void getProduct() {
        if (currentProduct != null) {

            textViewName.setText(currentProduct.getName());
            textViewPrice.setText(String.valueOf(currentProduct.getPrice()));
            textViewDescription.setText(currentProduct.getDescription());
            textViewShopName.setText(currentProduct.getShopName());
            textViewShopLocation.setText(currentProduct.getShopAddress());
            textViewRating.setText(String.valueOf(currentProduct.getRating()));

            if (currentProduct.getAvailable())
                textViewAvailable.setText("available");
            else
                textViewAvailable.setText("out of stock");

            if (currentProduct.getImage() != null)
                Glide.with(this).load(currentProduct.getImage()).into(imageView);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showBottomDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);


        Button btn_sheet = dialog.findViewById(R.id.bottomSheet_btn_addToCart);
        TextView textViewName = dialog.findViewById(R.id.bottomSheet_tv_itemName);
        TextView textViewPrice = dialog.findViewById(R.id.bottomSheet_tv_itemPrice);
        ImageView btn_plus = dialog.findViewById(R.id.bottomSheet_btn_plus);
        ImageView btn_minus = dialog.findViewById(R.id.bottomSheet_btn_minus);
        TextView textViewCount = dialog.findViewById(R.id.bottomSheet_tv_count);

        textViewName.setText(currentProduct.getName());
        textViewPrice.setText(String.valueOf(currentProduct.getPrice()));
        textViewCount.setText(String.valueOf(count));

        btn_plus.setOnClickListener(v -> {
            textViewCount.setText(String.valueOf(++count));
            textViewPrice.setText(String.valueOf(count * currentProduct.getPrice()));
        });

        btn_minus.setOnClickListener(v -> {
            if (count > 1) {
                textViewCount.setText(String.valueOf(--count));
                textViewPrice.setText(String.valueOf(count * currentProduct.getPrice()));
            }
        });

        btn_sheet.setOnClickListener(v -> addToCart(dialog));


        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

    }

    private void addToCart(Dialog dialog) {
        Order order = new Order(currentProduct.getId(), currentProduct.getName(), count, currentProduct.getPrice());

        ProductDatabase.getInstance(DetailsActivity.this).dao().insertOrder(order)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                dialog.dismiss();
                Log.d(TAG, "mohamed onSubscribe: order inserted  " + order.getOrderId());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "mohamed onComplete: " + order.getOrderId());
                Toast.makeText(DetailsActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Toast.makeText(DetailsActivity.this, "Error :'(", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "mohamed onError: " + e.getMessage());
            }
        });
    }

}