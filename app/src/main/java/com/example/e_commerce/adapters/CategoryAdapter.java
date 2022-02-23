package com.example.e_commerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e_commerce.R;
import com.example.e_commerce.models.Category;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;


public class CategoryAdapter extends FirestoreRecyclerAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private OnCategoryClickListener listener;
    private int lastPosition = -1;

    public CategoryAdapter(Context context, @NonNull FirestoreRecyclerOptions<Category> options, OnCategoryClickListener listener) {
        super(options);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category category) {
        if (!category.getCategory_image().equals(""))
            Glide.with(context).load(category.getCategory_image()).into(holder.imageViewCategory);
        holder.textViewCategory.setText(category.getCategory_name());

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.category_anim);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull @NotNull CategoryViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewCategory;
        private final TextView textViewCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCategory = itemView.findViewById(R.id.iv_item_categoryImage);
            textViewCategory = itemView.findViewById(R.id.tv_item_categoryName);

            itemView.setOnClickListener(v -> listener.onClick(getSnapshots().getSnapshot(getBindingAdapterPosition())));

        }
    }

    public interface OnCategoryClickListener {
        void onClick(DocumentSnapshot documentSnapshot);
    }
}
