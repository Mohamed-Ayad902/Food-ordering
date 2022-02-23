package com.example.e_commerce.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.e_commerce.R;
import com.example.e_commerce.adapters.RequestStatusAdapter;
import com.example.e_commerce.models.Order;
import com.example.e_commerce.models.Request;
import com.example.e_commerce.models.Statics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestStatusActivity extends AppCompatActivity {

    private static final String TAG = "RequestStatusActivity";

    private LottieAnimationView lottieAnimationView;
    private Request request;
    private List<Order> orderList;
    private RecyclerView recyclerView;
    private RequestStatusAdapter adapter;
    private TextView textViewTitle, textViewId, textViewPhone, textViewTotal, textViewAddress, textViewCreated;
    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_status);

        lottieAnimationView = findViewById(R.id.request_status_lottie);
        textViewAddress = findViewById(R.id.request_status_address);
        textViewTotal = findViewById(R.id.request_status_totalPrice);
        textViewPhone = findViewById(R.id.request_status_phone);
        textViewCreated = findViewById(R.id.request_status_created);
        textViewId = findViewById(R.id.request_status_id);
        textViewTitle = findViewById(R.id.request_status_tv);
        recyclerView = findViewById(R.id.request_status_recyclerView);

        request = Statics.getInstance();
        status = request.getStatus();
        orderList = request.getOrders();

        adapter = new RequestStatusAdapter(orderList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        switch (status) {
            case 0:
                lottieAnimationView.setAnimation(R.raw.placed);
                break;
            case 1:
                lottieAnimationView.setAnimation(R.raw.cooking);
                break;
            case 2:
                lottieAnimationView.setAnimation(R.raw.on_my_way);
                break;
            default:
                lottieAnimationView.setAnimation(R.raw.shipped);
        }

        textViewTitle.setText(Statics.statusTitle(status));
        textViewId.setText(Statics.getRequestId());
        textViewPhone.setText(request.getPhoneNumber());
        textViewAddress.setText(request.getAddress());
        textViewTotal.setText(String.valueOf(request.getTotalPrice()));
        textViewCreated.setText(request.getCreated().toDate().toString());

    }

}