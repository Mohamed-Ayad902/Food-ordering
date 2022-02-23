package com.example.e_commerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.e_commerce.R;
import com.example.e_commerce.data.ProductDatabase;
import com.example.e_commerce.models.Product;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.jetbrains.annotations.NotNull;


public class SuggestedProductsAdapter extends FirestoreRecyclerAdapter<Product, SuggestedProductsAdapter.SuggestedViewHolder> {

    private final Context context;
    private final OnSuggestedClickListener listener;
    private int lastPosition = -1;

    public SuggestedProductsAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Product> options, Context context, OnSuggestedClickListener listener) {
        super(options);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull SuggestedViewHolder holder, int position, @NonNull @NotNull Product model) {
        holder.ratingBar.setRating((float) model.getRating());
        holder.textViewName.setText(model.getName());
        holder.textViewShopName.setText(model.getShopName());
        holder.textViewPrice.setText(String.valueOf(model.getPrice()));

        if (ProductDatabase.getInstance(context).dao().isFavourite(model.getId()))
            Glide.with(context).load(R.drawable.ic_favourite).into(holder.imageViewFavourite);
        else
            Glide.with(context).load(R.drawable.ic_not_favourite).into(holder.imageViewFavourite);

        if (model.getImage() != null)
            Glide.with(context).load(model.getImage())
                    .transform(new CenterCrop(), new RoundedCorners(35))
                    .into(holder.imageView);
        else
            Glide.with(context).load(R.drawable.logo)
                    .transform(new CenterCrop(), new RoundedCorners(35))
                    .into(holder.imageView);

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_animation);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    @NonNull
    @NotNull
    @Override
    public SuggestedViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggested_for_you, parent, false);
        return new SuggestedViewHolder(view);
    }

    class SuggestedViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView, imageViewFavourite;
        private final TextView textViewPrice, textViewName, textViewShopName;
        private final RatingBar ratingBar;

        public SuggestedViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_imageView_suggested);
            imageViewFavourite = itemView.findViewById(R.id.item_suggested_iv_favourite);
            textViewPrice = itemView.findViewById(R.id.item_tv_suggested_price);
            textViewName = itemView.findViewById(R.id.item_tv_suggested_name);
            textViewShopName = itemView.findViewById(R.id.item_suggested_tv_shopName);
            ratingBar = itemView.findViewById(R.id.item_suggested_ratingBar);

            itemView.setOnClickListener(v -> listener.OnClick(getSnapshots().getSnapshot(getAbsoluteAdapterPosition()).toObject(Product.class)));
            imageViewFavourite.setOnClickListener(v -> listener.OnFavouriteClicked(getSnapshots().getSnapshot(getAbsoluteAdapterPosition()).toObject(Product.class), getAbsoluteAdapterPosition(), imageViewFavourite));

        }
    }

    public interface OnSuggestedClickListener {
        void OnClick(Product product);

        void OnFavouriteClicked(Product product, int position, ImageView imageViewFavourite);
    }

}
