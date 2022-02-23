package com.example.e_commerce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.e_commerce.R;
import com.example.e_commerce.adapters.PaginationAdapter;
import com.example.e_commerce.data.ProductDatabase;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.models.Statics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ViewAllProductsActivity extends AppCompatActivity {
    private static final String TAG = "ViewAllProductsActivity";

    private PaginationAdapter adapter;
    private List<Product> productList;
    private boolean isScrolling, isLastItemReached;
    private final int limit = 7;
    private DocumentSnapshot lastVisible;
    private RecyclerView recyclerView;


    private String selectedCategory;
    private EditText editTextSearch;
    private TextView textView;
    private LottieAnimationView animationNoResult, animationLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_products);

        selectedCategory = getIntent().getStringExtra(Statics.EXTRA_CATEGORY);
        recyclerView = findViewById(R.id.viewAllProducts_recyclerView);
        editTextSearch = findViewById(R.id.view_all_et_search);
        animationNoResult = findViewById(R.id.lottie_no_result);
        animationLoading = findViewById(R.id.lottie_loading);
        textView = findViewById(R.id.text_no_result);
        productList = new ArrayList<>();


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textView.setVisibility(View.GONE);
                animationNoResult.setVisibility(View.GONE);
                animationNoResult.clearAnimation();
                animationLoading.setVisibility(View.GONE);
                animationNoResult.loop(false);

                if (s.toString().trim().isEmpty() || count == 0)
                    pagingRecyclerView();
                else
                    search(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        adapter = new PaginationAdapter(productList, this, new PaginationAdapter.OnFavouriteClickListener() {

            @Override
            public void OnFavouriteClick(Product product, int position, ImageView view) {
                deleteOrAddToFavourite(product, ProductDatabase.getInstance(ViewAllProductsActivity.this).dao().isFavourite(product.getId()), view);
            }

            @Override
            public void OnItemClick(Product product) {
                Intent intent = new Intent(getBaseContext(), DetailsActivity.class);
                intent.putExtra(Statics.PRODUCT_EXTRA, product);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        pagingRecyclerView();


    }


    private void search(String s) {
        Query query = FirebaseFirestore.getInstance().collection(Statics.COLLECTION_ALL_PRODUCTS).whereEqualTo(Statics.CATEGORY, selectedCategory)
                .orderBy("name")
                .startAt(s).endAt(s + '\uf8ff').limit(limit);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                productList.clear();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Product product = snapshot.toObject(Product.class);
                    productList.add(product);
                    Log.d(TAG, "mohamed search product added to list onSuccess: " + product.getName());
                }
                adapter.notifyDataSetChanged();
                if (productList.size() == 0) {
                    textView.setVisibility(View.VISIBLE);
                    animationNoResult.setAnimation(R.raw.no_results);
                    animationNoResult.setSpeed(2);
                    animationNoResult.setVisibility(View.VISIBLE);
                    animationNoResult.playAnimation();
                }


                RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                            isScrolling = true;
                            animationLoading.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                        int firstVisibleItemPosition = Objects.requireNonNull(linearLayoutManager).findFirstVisibleItemPosition();
                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int totalItemCount = linearLayoutManager.getItemCount();

                        if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                            isScrolling = false;
                            Log.d(TAG, "mohamed search onScrolled again : " + productList.size());
                            Query nextQuery = FirebaseFirestore.getInstance().collection(Statics.COLLECTION_ALL_PRODUCTS)
                                    .whereEqualTo(Statics.CATEGORY, selectedCategory)
                                    .orderBy("name")
                                    .startAt(s).endAt(s + '\uf8ff')
                                    .startAfter(lastVisible).limit(limit);

                            nextQuery.get().addOnCompleteListener(t -> {
                                if (t.isSuccessful()) {
                                    for (DocumentSnapshot d : Objects.requireNonNull(t.getResult())) {
                                        Product productModel = d.toObject(Product.class);
                                        Objects.requireNonNull(productModel).setId(d.getId());
                                        productList.add(productModel);
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (t.getResult().size() - 1 > 0)
                                        lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);

                                    if (t.getResult().size() < limit) {
                                        isLastItemReached = true;
                                        animationLoading.setVisibility(View.GONE);
                                        Log.d(TAG, "mohamed search onScrolled end : " + productList.size());
                                    }
                                }
                            });
                        }
                    }
                };
                recyclerView.addOnScrollListener(onScrollListener);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(ViewAllProductsActivity.this, "error search", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "mohamed search onFailure: " + e.getMessage());
                Log.d(TAG, "mohamed search onFailure: " + e.getCause());
                animationNoResult.setAnimation(R.raw.error);
                animationNoResult.setSpeed(1);
                animationNoResult.loop(true);
                animationNoResult.setVisibility(View.VISIBLE);
                animationNoResult.playAnimation();
            }
        });
    }

    private void pagingRecyclerView() {
        productList.clear();
        Query query = FirebaseFirestore.getInstance().collection(Statics.COLLECTION_ALL_PRODUCTS).limit(limit).whereEqualTo(Statics.CATEGORY, selectedCategory);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Product productModel = document.toObject(Product.class);
                        Objects.requireNonNull(productModel).setId(document.getId());
                        productList.add(productModel);
                    }
                    adapter.notifyDataSetChanged();
//                    if (task.getResult().size() - 1 > 0)    // to avoid app crash if list is empty
                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true;
                                animationLoading.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                            int firstVisibleItemPosition = Objects.requireNonNull(linearLayoutManager).findFirstVisibleItemPosition();
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();

                            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                isScrolling = false;
                                Log.d(TAG, "mohamed onScrolled again : " + productList.size());
                                Query nextQuery = FirebaseFirestore.getInstance().collection(Statics.COLLECTION_ALL_PRODUCTS).whereEqualTo(Statics.CATEGORY, selectedCategory).startAfter(lastVisible).limit(limit);

                                nextQuery.get().addOnCompleteListener(t -> {
                                    if (t.isSuccessful()) {
                                        for (DocumentSnapshot d : Objects.requireNonNull(t.getResult())) {
                                            Product productModel = d.toObject(Product.class);
                                            Objects.requireNonNull(productModel).setId(d.getId());
                                            productList.add(productModel);
                                        }
                                        adapter.notifyDataSetChanged();
//                                        if (task.getResult().size() - 1 > 0)    // to avoid app crash if list is empty dh m4 48al
                                        if (t.getResult().size() - 1 > 0)
                                            lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);

                                        if (t.getResult().size() < limit) {
                                            isLastItemReached = true;
                                            animationLoading.setVisibility(View.GONE);
                                            Log.d(TAG, "mohamed onScrolled end : " + productList.size());
                                        }
                                    }
                                });
                            }
                        }
                    };
                    recyclerView.addOnScrollListener(onScrollListener);
                }
            }
        });

    }

    private void deleteOrAddToFavourite(Product product, boolean b, ImageView imageViewFavourite) {
        if (b) {
            ProductDatabase.getInstance(ViewAllProductsActivity.this).dao().deleteProduct(product)
                    .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                            Log.d(TAG, "mohamed onSubscribe: remove from fav " + product.getId());
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "mohamed onComplete: remove from fav " + product.getId());
                            imageViewFavourite.setImageResource(R.drawable.ic_not_favourite);
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            Log.e(TAG, "mohamed onError: remove from fav " + e.getCause());
                        }
                    });
        } else {
            ProductDatabase.getInstance(ViewAllProductsActivity.this).dao().insertProduct(product)
                    .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                    Log.d(TAG, "mohamed onSubscribe: add to fav  " + product.getId());
                }

                @Override
                public void onComplete() {
                    Log.d(TAG, "mohamed onComplete: add to fav " + product.getId());
                    imageViewFavourite.setImageResource(R.drawable.ic_favourite);
                }

                @Override
                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                    Log.e(TAG, "mohamed onError: add to fav " + e.getCause());
                }
            });
        }
    }

}