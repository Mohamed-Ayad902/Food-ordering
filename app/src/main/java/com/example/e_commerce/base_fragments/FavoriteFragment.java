package com.example.e_commerce.base_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.e_commerce.R;
import com.example.e_commerce.activities.DetailsActivity;
import com.example.e_commerce.activities.ViewAllProductsActivity;
import com.example.e_commerce.adapters.FavouritesAdapter;
import com.example.e_commerce.data.ProductDatabase;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.models.Statics;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoriteFragment extends Fragment {
    private static final String TAG = "FavoriteFragment";

    private RecyclerView recyclerViewFavourites;
    private FavouritesAdapter adapter;
    private List<Product> productList;
    private TextView textView;
    private LottieAnimationView lottieAnimationView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerViewFavourites = view.findViewById(R.id.favourite_fragment_recyclerView);
        textView = view.findViewById(R.id.favourite_tv_empty);
        lottieAnimationView = view.findViewById(R.id.lottie_no_favourite);

        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        ProductDatabase.getInstance(getActivity()).dao().getProducts()
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Product>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "mohamed onSubscribe: ");
                    }

                    @Override
                    public void onNext(@NonNull List<Product> products) {
                        Log.d(TAG, "mohamed onNext: " + products.size());
                        productList = products;

                        if (productList.size() == 0) {
                            textView.setVisibility(View.VISIBLE);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                        }else {
                            textView.setVisibility(View.GONE);
                            lottieAnimationView.setVisibility(View.GONE);
                        }

                        adapter = new FavouritesAdapter(productList, getActivity(), new FavouritesAdapter.OnFavouriteClickListener() {
                            @Override
                            public void OnFavouriteClick(Product product, int position, View imageViewFavourite) {
                                deleteOrAddToFavourite(product, position, ProductDatabase.getInstance(getActivity()).dao().isFavourite(product.getId()));
                            }

                            @Override
                            public void OnItemClick(Product product) {
                                Intent intent = new Intent(getContext(), DetailsActivity.class);
                                intent.putExtra(Statics.PRODUCT_EXTRA, product);
                                startActivity(intent);
                            }
                        });

                        recyclerViewFavourites.setAdapter(adapter);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "mohamed onError: " + e.getCause());
                        Log.e(TAG, "mohamed onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "mohamed onComplete: " + productList.size());
                    }
                });
    }

    private void deleteOrAddToFavourite(Product product, int position, boolean b) {
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
                            productList.remove(position);
                            adapter.notifyItemRemoved(position);
                            if (productList.size() == 0) {
                                textView.setVisibility(View.VISIBLE);
                                lottieAnimationView.setVisibility(View.VISIBLE);
                            }
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
                    adapter.notifyItemInserted(position);
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    Log.e(TAG, "mohamed onError: add to fav " + e.getCause());
                }
            });
        }
    }
}