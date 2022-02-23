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
import com.example.e_commerce.models.Product;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PaginationAdapter extends RecyclerView.Adapter<PaginationAdapter.ViewAllPaginationViewHolder> {

    private final List<Product> productList;
    private final Context context;
    private final OnFavouriteClickListener listener;
    private int lastPosition = -1;

    public PaginationAdapter(List<Product> productList, Context context, OnFavouriteClickListener listener) {
        this.productList = productList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewAllPaginationViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggested_for_you, parent, false);
        return new ViewAllPaginationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewAllPaginationViewHolder holder, int position) {
        holder.textViewName.setText(productList.get(position).getName());
        holder.textViewPrice.setText(String.valueOf(productList.get(position).getPrice()));
        holder.textViewShopName.setText(productList.get(position).getShopName());
        holder.ratingBar.setRating((float) productList.get(position).getRating());

        if (productList.get(position).getImage() != null)
            Glide.with(context).load(productList.get(position).getImage())
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

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ViewAllPaginationViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView, imageViewFavourite;
        private final TextView textViewPrice, textViewName, textViewShopName;
        private final RatingBar ratingBar;

        public ViewAllPaginationViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_imageView_suggested);
            imageViewFavourite = itemView.findViewById(R.id.item_suggested_iv_favourite);
            textViewPrice = itemView.findViewById(R.id.item_tv_suggested_price);
            textViewName = itemView.findViewById(R.id.item_tv_suggested_name);
            textViewShopName = itemView.findViewById(R.id.item_suggested_tv_shopName);
            ratingBar = itemView.findViewById(R.id.item_suggested_ratingBar);


            imageViewFavourite.setOnClickListener(v -> listener.OnFavouriteClick(productList.get(getBindingAdapterPosition()), getBindingAdapterPosition(), imageViewFavourite));
            itemView.setOnClickListener(v -> listener.OnItemClick(productList.get(getAbsoluteAdapterPosition())));
        }
    }

    public interface OnFavouriteClickListener {
        void OnFavouriteClick(Product product, int position, ImageView view);

        void OnItemClick(Product product);
    }
}
