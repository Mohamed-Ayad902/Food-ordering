package com.example.e_commerce.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.e_commerce.R;
import com.example.e_commerce.models.Product;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private final OnclickListener listener;
    private final Context context;
    private List<Product> products;

    public SliderAdapterExample(Context context, List<Product> products, OnclickListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    public void setItems(List<Product> product) {
        this.products = product;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.products.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Product product) {
        this.products.add(product);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_best_offer, parent, false);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        Product product = products.get(position);

        viewHolder.textViewDescription.setText(product.getName());
        viewHolder.textViewPrice.setText(String.valueOf(product.getPrice()));
        Glide.with(viewHolder.itemView)
                .load(product.getImage())
//                .fitCenter()
                .centerCrop()
                .into(viewHolder.imageViewBackground);

        viewHolder.itemView.setOnClickListener(v -> listener.OnItemClick(product));
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return products.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription, textViewPrice;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewPrice = itemView.findViewById(R.id.tv_auto_image_slider_price);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;


        }
    }

    public interface OnclickListener {
        void OnItemClick(Product product);
    }

}
