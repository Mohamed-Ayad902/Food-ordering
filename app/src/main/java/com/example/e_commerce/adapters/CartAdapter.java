package com.example.e_commerce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce.R;
import com.example.e_commerce.models.Order;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Order> orderList;
    private OnButtonsClickListener listener;

    public CartAdapter(List<Order> orderList, OnButtonsClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_orders, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CartViewHolder holder, int position) {
        holder.textViewProductName.setText(orderList.get(position).getProductName());
        holder.textViewProductQuantity.setText(String.valueOf(orderList.get(position).getQuantity()));
        holder.textViewProductPrice.setText(String.valueOf(orderList.get(position).getPrice()));
        holder.textViewTotalPrice.setText(String.valueOf(orderList.get(position).getPrice() * orderList.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewProductName, textViewProductPrice, textViewProductQuantity, textViewTotalPrice;
        private final ImageView imageViewPlus, imageViewMinus;

        public CartViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.item_cart_productName);
            textViewProductPrice = itemView.findViewById(R.id.item_cart_productPrice);
            textViewTotalPrice = itemView.findViewById(R.id.item_cart_itemTotalPrice);
            textViewProductQuantity = itemView.findViewById(R.id.item_cart_productQuantity);
            imageViewMinus = itemView.findViewById(R.id.item_cart_btn_minus);
            imageViewPlus = itemView.findViewById(R.id.item_cart_btn_plus);

            imageViewMinus.setOnClickListener(v -> listener.OnMinus(orderList.get(getBindingAdapterPosition())));
            imageViewPlus.setOnClickListener(v -> listener.OnPlus(orderList.get(getBindingAdapterPosition())));

        }

        public void deleteOrder() {
            listener.OnDelete(orderList.get(getBindingAdapterPosition()),getBindingAdapterPosition());
        }
    }

    public interface OnButtonsClickListener {
        void OnPlus(Order order);
        void OnMinus(Order order);
        void OnDelete(Order order,int position);
    }
}
