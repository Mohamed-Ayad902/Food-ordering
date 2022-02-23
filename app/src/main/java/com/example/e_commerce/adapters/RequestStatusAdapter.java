package com.example.e_commerce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce.R;
import com.example.e_commerce.models.Order;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RequestStatusAdapter extends RecyclerView.Adapter<RequestStatusAdapter.RequestVH> {

    private List<Order> orderList;

    public RequestStatusAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @NotNull
    @Override
    public RequestVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_status, parent, false);
        return new RequestVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RequestVH holder, int position) {
        holder.textViewTotal.setText(String.valueOf(orderList.get(position).getPrice() * orderList.get(position).getQuantity()));
        holder.textViewName.setText(orderList.get(position).getProductName());
        holder.textViewCount.setText(String.valueOf(orderList.get(position).getQuantity()));
        holder.textViewPriceOne.setText(String.valueOf(orderList.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class RequestVH extends RecyclerView.ViewHolder {
        private final TextView textViewName, textViewPriceOne, textViewCount, textViewTotal;

        public RequestVH(@NonNull @NotNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.item_req_status_name);
            textViewPriceOne = itemView.findViewById(R.id.item_req_status_priceOne);
            textViewCount = itemView.findViewById(R.id.item_req_status_count);
            textViewTotal = itemView.findViewById(R.id.item_req_status_totalPrice);

        }
    }
}
