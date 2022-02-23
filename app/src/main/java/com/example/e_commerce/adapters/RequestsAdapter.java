package com.example.e_commerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce.R;
import com.example.e_commerce.models.Request;
import com.example.e_commerce.models.Statics;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.jetbrains.annotations.NotNull;

public class RequestsAdapter extends FirestoreRecyclerAdapter<Request, RequestsAdapter.RequestsVH> {
    private int lastPosition = -1;
    private final Context context;
    private OnRequestClickListener listener;


    public RequestsAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Request> options, Context context, OnRequestClickListener listener) {
        super(options);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull RequestsVH holder, int position, @NonNull @NotNull Request model) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_animation);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
        holder.textViewID.setText(getSnapshots().getSnapshot(position).getId());
        holder.textViewName.setText(model.getUserName());
        holder.textViewAddress.setText(model.getAddress());
        holder.textViewPhone.setText(model.getPhoneNumber());
        holder.textViewStatus.setText(Statics.getStatus(model.getStatus()));
        holder.textViewCreated.setText(model.getCreated().toDate().toString());
    }

    @NonNull
    @NotNull
    @Override
    public RequestsVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_requests, parent, false);
        return new RequestsVH(view);
    }

    class RequestsVH extends RecyclerView.ViewHolder {
        private final TextView textViewID, textViewName, textViewPhone, textViewAddress, textViewStatus, textViewCreated;

        public RequestsVH(@NonNull @NotNull View itemView) {
            super(itemView);
            textViewID = itemView.findViewById(R.id.item_allReq_id);
            textViewName = itemView.findViewById(R.id.item_allReq_name);
            textViewPhone = itemView.findViewById(R.id.item_allReq_phone);
            textViewAddress = itemView.findViewById(R.id.item_allReq_address);
            textViewStatus = itemView.findViewById(R.id.item_allReq_status);
            textViewCreated = itemView.findViewById(R.id.item_allReq_created);

            itemView.setOnClickListener(v -> listener.OnClick(getSnapshots().get(getBindingAdapterPosition()),getSnapshots().getSnapshot(getBindingAdapterPosition()).getId()));

        }
    }

    public interface OnRequestClickListener {
        void OnClick(Request request,String requestId);
    }
}
