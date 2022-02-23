package com.example.e_commerce.base_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce.R;
import com.example.e_commerce.activities.DetailsActivity;
import com.example.e_commerce.activities.ViewAllProductsActivity;
import com.example.e_commerce.adapters.CategoryAdapter;
import com.example.e_commerce.adapters.SliderAdapterExample;
import com.example.e_commerce.adapters.SuggestedProductsAdapter;
import com.example.e_commerce.data.ProductDatabase;
import com.example.e_commerce.models.Category;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.models.Statics;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private CategoryAdapter categoryAdapter;
    private SuggestedProductsAdapter suggestedAdapter;
    private RecyclerView recyclerViewCategory, recyclerViewSuggested;
    private SliderAdapterExample sliderAdapter;
    private List<Product> productList;


    @Override
    public void onStart() {
        super.onStart();
        categoryAdapter.startListening();
        new Handler().postDelayed(() -> suggestedAdapter.startListening(), 550);
    }

    @Override
    public void onStop() {
        super.onStop();
        categoryAdapter.stopListening();
        suggestedAdapter.stopListening();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        productList = new ArrayList<>();
        productList = Statics.getProductList();
        if (productList != null)
            Log.d(TAG, "mohamed get statics onCreate: " + productList.size());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewCategory = view.findViewById(R.id.recyclerViewHomeCategory);
        recyclerViewSuggested = view.findViewById(R.id.recyclerViewSuggestedForYou);
        SliderView sliderView = view.findViewById(R.id.imageSlider);


        sliderAdapter = new SliderAdapterExample(getActivity(), productList, product -> {
            Intent intent = new Intent(getContext(), DetailsActivity.class);
            intent.putExtra(Statics.PRODUCT_EXTRA, product);
            startActivity(intent);
        });

        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.SWAP);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

//        Query query = FirebaseFirestore.getInstance().collection(Statics.COLLECTION_ALL_PRODUCTS).whereGreaterThanOrEqualTo("discount", 1).limit(6);
//        query.get().addOnCompleteListener(task -> {
//
//            if (task.isSuccessful()) {
//                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                    Product productModel = document.toObject(Product.class);
//                    if (productModel != null) {
//                        productModel.setId(document.getId());
//                        new Handler().postDelayed(() -> sliderAdapter.addItem(productModel), 200);
//                    }
//                }
//                Log.d(TAG, "mohamed slider all fetched onCreateView: " + task.getResult().getDocuments().size());
//            }
//        });


        recyclerViewCategory();
        recyclerViewSuggested();
        return view;
    }

    private void recyclerViewSuggested() {
        Query query = FirebaseFirestore.getInstance().collection(Statics.COLLECTION_ALL_PRODUCTS).limit(5);
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();

        suggestedAdapter = new SuggestedProductsAdapter(options, getActivity(), new SuggestedProductsAdapter.OnSuggestedClickListener() {
            @Override
            public void OnClick(Product product) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra(Statics.PRODUCT_EXTRA, product);
                startActivity(intent);
            }

            @Override
            public void OnFavouriteClicked(Product product, int position, ImageView imageViewFavourite) {
                deleteOrAddToFavourite(product, ProductDatabase.getInstance(getActivity()).dao().isFavourite(product.getId()), imageViewFavourite);
            }
        });

        recyclerViewSuggested.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewSuggested.setHasFixedSize(false);
        recyclerViewSuggested.setItemAnimator(null);
        recyclerViewSuggested.setAdapter(suggestedAdapter);
        Log.d(TAG, "mohamed recyclerViewSuggested: ");

    }

    private void deleteOrAddToFavourite(Product product, boolean b, ImageView imageViewFavourite) {
        if (b) {
            ProductDatabase.getInstance(getActivity()).dao().deleteProduct(product)
                    .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            Log.d(TAG, "mohamed onSubscribe: remove from fav " + product.getId());
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "mohamed onComplete: remove from fav " + product.getId());
                            imageViewFavourite.setImageResource(R.drawable.ic_not_favourite);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.e(TAG, "mohamed onError: remove from fav " + e.getCause());
                        }
                    });
        } else {
            ProductDatabase.getInstance(getActivity()).dao().insertProduct(product)
                    .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    Log.d(TAG, "mohamed onSubscribe: add to fav  " + product.getId());
                }

                @Override
                public void onComplete() {
                    Log.d(TAG, "mohamed onComplete: add to fav " + product.getId());
                    imageViewFavourite.setImageResource(R.drawable.ic_favourite);
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    Log.e(TAG, "mohamed onError: add to fav " + e.getCause());
                }
            });
        }
    }

    private void recyclerViewCategory() {
        Query query = FirebaseFirestore.getInstance().collection(Statics.COLLECTION_ALL_CATEGORIES);
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();

        categoryAdapter = new CategoryAdapter(getActivity(), options,
                documentSnapshot -> {
                    Category selectedCategory = documentSnapshot.toObject(Category.class);
                    String category = Objects.requireNonNull(selectedCategory).getCategory_name();
                    Intent intent = new Intent(getActivity(), ViewAllProductsActivity.class);
                    intent.putExtra(Statics.EXTRA_CATEGORY, category);
                    startActivity(intent);
                });

        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setHasFixedSize(true);
        recyclerViewCategory.setItemAnimator(null);
        recyclerViewCategory.setAdapter(categoryAdapter);
        Log.d(TAG, "mohamed recyclerViewCategory: ");
    }

}

