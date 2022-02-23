package com.example.e_commerce.base_fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.e_commerce.R;
import com.example.e_commerce.adapters.CartAdapter;
import com.example.e_commerce.data.ProductDatabase;
import com.example.e_commerce.fcm.APIInterface;
import com.example.e_commerce.fcm.Notification;
import com.example.e_commerce.fcm.RemoteMassage;
import com.example.e_commerce.fcm.RetrofitClient;
import com.example.e_commerce.models.Order;
import com.example.e_commerce.models.Request;
import com.example.e_commerce.models.Statics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CartFragment extends Fragment {
    private static final String TAG = "CartFragment";
    private double totalPrice;
    private int currentOrderQuantity;
    private CoordinatorLayout coordinatorLayout;
    private String name, address, phoneNumber, comment;

    private CartAdapter cartAdapter;

    private LottieAnimationView animationCartEmpty;
    private RecyclerView recyclerViewOrders;
    private TextView textViewTotalPrice, textViewCartEmpty;
    private Button btn_confirm;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                Toast.makeText(getActivity(), "refresh your cart please :)", Toast.LENGTH_LONG).show();
            }
        });
        RxJavaPlugins.setErrorHandler(throwable -> Log.e(TAG, "mohamed i got the error onCreate: " + throwable.getCause() + "\n the massage : " + throwable.getMessage()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerViewOrders = view.findViewById(R.id.fragment_cart_recyclerView);
        btn_confirm = view.findViewById(R.id.fragment_cart_btn_confirmOrder);
        textViewTotalPrice = view.findViewById(R.id.fragment_cart_tv_totalPrice);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        textViewCartEmpty = view.findViewById(R.id.cart_tv_cartEmpty);
        animationCartEmpty = view.findViewById(R.id.lottie_empty_cart);

        initRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btn_confirm.setOnClickListener(v -> {
            getOrder();
        });

    }

    private void getOrder() {
        ProductDatabase.getInstance(getActivity()).dao().getOrders()
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new SingleObserver<List<Order>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                Log.d(TAG, "mohamed get order onSubscribe: ");
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Order> orderList) {
                Log.d(TAG, "mohamed get orders onSuccess: " + orderList.get(0).toString());
                showDialog(orderList);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.e(TAG, "onError: " + e.getCause());
                Log.e(TAG, "onError: " + e.toString());
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showDialog(List<Order> orderList) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_confirm_order);

        TextInputLayout editTextUserName = dialog.findViewById(R.id.bottomSheet_et_userName);
        TextInputLayout editTextAddress = dialog.findViewById(R.id.bottomSheet_et_address);
        TextInputLayout editTextComment = dialog.findViewById(R.id.bottomSheet_et_comment);
        TextInputLayout editTextPhoneNumber = dialog.findViewById(R.id.bottomSheet_et_phoneNumber);
        LottieAnimationView animationRequestOk = dialog.findViewById(R.id.bottomSheet_lottie_req_ok);
        Button btn_ok = dialog.findViewById(R.id.bottomSheet_btnOk);
        TextView cancel = dialog.findViewById(R.id.bottomSheet_cancel);


        btn_ok.setOnClickListener(v -> {
            editTextUserName.setError(null);
            editTextAddress.setError(null);
            editTextPhoneNumber.setError(null);

            if (Objects.requireNonNull(editTextUserName.getEditText()).getText().toString().trim().isEmpty())
                editTextUserName.setError("Name is required");
            else if (Objects.requireNonNull(editTextAddress.getEditText()).getText().toString().trim().isEmpty())
                editTextAddress.setError("Address is required");
            else if (Objects.requireNonNull(editTextPhoneNumber.getEditText()).toString().trim().isEmpty())
                editTextPhoneNumber.setError("phone is required");
            else {
                name = Objects.requireNonNull(editTextUserName.getEditText()).getText().toString().trim();
                address = Objects.requireNonNull(editTextAddress.getEditText()).getText().toString().trim();
                phoneNumber = Objects.requireNonNull(editTextPhoneNumber.getEditText()).getText().toString().trim();
                comment = Objects.requireNonNull(editTextComment.getEditText()).getText().toString().trim();
                addOrderToFirebase(orderList, dialog);
            }
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    private void addOrderToFirebase(List<Order> orderList, Dialog dialog) {
        LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.bottomSheet_lottie_req_ok);
        TextInputLayout editTextUserName = dialog.findViewById(R.id.bottomSheet_et_userName);
        TextInputLayout editTextAddress = dialog.findViewById(R.id.bottomSheet_et_address);
        TextInputLayout editTextComment = dialog.findViewById(R.id.bottomSheet_et_comment);
        TextInputLayout editTextPhoneNumber = dialog.findViewById(R.id.bottomSheet_et_phoneNumber);
        Button btn_ok = dialog.findViewById(R.id.bottomSheet_btnOk);
        TextView cancel = dialog.findViewById(R.id.bottomSheet_cancel);

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Request request = new Request(orderList, userId, address, phoneNumber, totalPrice, name, 0, new Timestamp(new Date()));
        if (!comment.trim().isEmpty())
            request.setComment(comment);

        FirebaseFirestore.getInstance().collection(Statics.COLLECTION_REQUESTS).add(request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {


                        editTextUserName.setVisibility(View.INVISIBLE);
                        editTextAddress.setVisibility(View.INVISIBLE);
                        editTextPhoneNumber.setVisibility(View.INVISIBLE);
                        editTextComment.setVisibility(View.INVISIBLE);

                        lottieAnimationView.setVisibility(View.VISIBLE);
                        lottieAnimationView.playAnimation();
                        dialog.setCancelable(false);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cartAdapter.setOrderList(orderList);
                                cartAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }, 4000);


                        ProductDatabase.getInstance(getActivity()).dao()
                                .deleteAllOrders().observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {

                            @Override
                            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                                Log.d(TAG, "mohamed delete order onSubscribe: ");
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "mohamed delete order onComplete: ");
                                sendNotification();
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                Log.e(TAG, "mohamed delete order onError: " + e.toString());
                                Log.e(TAG, "mohamed delete order onError: " + e.getCause());
                            }
                        });
                    }
                });

    }

    private void sendNotification() {
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("isStaff", true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String token = documentSnapshot.getString("userToken");
                    Log.d(TAG, "mohamed get notification onSuccess: " + token);

                    RemoteMassage model = new RemoteMassage(token, new Notification(
                            "you have a new order from : " + name,
                            "the order is supposed to be shipped at : " + address));

                    Retrofit retrofit = RetrofitClient.getInstance();
                    APIInterface apiInterface = retrofit.create(APIInterface.class);

                    apiInterface.sendNotification(model).observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<RemoteMassage>() {
                                @Override
                                public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                                    Log.d(TAG, "mohamed sending onSubscribe: ");
                                }

                                @Override
                                public void onNext(@io.reactivex.rxjava3.annotations.NonNull RemoteMassage remoteMassage) {
                                    Log.d(TAG, "mohamed sending onNext : ");
                                }

                                @Override
                                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                    Log.e(TAG, "mohamed sending onError: " + e.getMessage());
                                    Log.e(TAG, "mohamed sending onError: " + e.getCause());
                                    Toast.makeText(getActivity(), "notification error", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onComplete() {
                                    Log.d(TAG, "mohamed sending onComplete: ");
                                }
                            });
                }
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            CartAdapter.CartViewHolder cartViewHolder = (CartAdapter.CartViewHolder) viewHolder;
            cartViewHolder.deleteOrder();
        }

    };

    private void initRecyclerView() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewOrders);

        ProductDatabase.getInstance(getActivity()).dao().getOrders()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new SingleObserver<List<Order>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                Log.d(TAG, "mohamed onSubscribe: ");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<Order> orderList) {
                Log.d(TAG, "mohamed onSuccess: " + orderList.toString());
                cartAdapter = new CartAdapter(orderList, new CartAdapter.OnButtonsClickListener() {
                    @Override
                    public void OnPlus(Order order) {
                        currentOrderQuantity = order.getQuantity() + 1;
                        order.setQuantity(currentOrderQuantity);
                        if (ProductDatabase.getInstance(getActivity()).dao().updateOrder(order) == 1) {
                            cartAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void OnMinus(Order order) {
                        if (order.getQuantity() > 1) {
                            currentOrderQuantity = order.getQuantity() - 1;
                            order.setQuantity(currentOrderQuantity);
                            if (ProductDatabase.getInstance(getActivity()).dao().updateOrder(order) == 1) {
                                cartAdapter.notifyDataSetChanged();

                            }
                        }
                    }

                    @Override
                    public void OnDelete(Order order, int position) {
                        ProductDatabase.getInstance(getActivity()).dao().deleteOrder(order)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new CompletableObserver() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                                        Log.d(TAG, "mohamed delete one product onSubscribe: ");
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d(TAG, "mohamed delete one product onComplete: ");
                                        orderList.remove(position);
                                        cartAdapter.notifyItemRemoved(position);

                                        if (orderList.size() == 0) {
                                            textViewTotalPrice.setVisibility(View.GONE);
                                            btn_confirm.setVisibility(View.GONE);
                                            animationCartEmpty.setVisibility(View.VISIBLE);
                                            textViewCartEmpty.setVisibility(View.VISIBLE);
                                        }

                                        Snackbar.make(coordinatorLayout, "Item deleted", Snackbar.LENGTH_LONG)
                                                .setAction("Undo", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ProductDatabase.getInstance(getActivity()).dao().insertOrder(order).observeOn(AndroidSchedulers.mainThread())
                                                                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                                                            @Override
                                                            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                                                                Log.d(TAG, "mohamed return product onSubscribe: ");
                                                            }

                                                            @Override
                                                            public void onComplete() {
                                                                Log.d(TAG, "mohamed return product onComplete: ");
                                                                orderList.add(position, order);
                                                                cartAdapter.notifyItemInserted(position);

                                                                if (orderList.size() > 0) {
                                                                    animationCartEmpty.setVisibility(View.GONE);
                                                                    textViewCartEmpty.setVisibility(View.GONE);
                                                                    textViewTotalPrice.setVisibility(View.VISIBLE);
                                                                    btn_confirm.setVisibility(View.VISIBLE);
                                                                }

                                                            }

                                                            @Override
                                                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                                                Log.e(TAG, "mohamed return product onError: " + e.getCause());
                                                            }
                                                        });
                                                    }
                                                }).show();
                                    }

                                    @Override
                                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                        Log.e(TAG, "mohamed delete one product onError: " + e.getCause());
                                    }
                                });
                    }
                });

                recyclerViewOrders.setHasFixedSize(true);
                cartAdapter.notifyDataSetChanged();

                if (orderList.size() == 0) {
                    textViewTotalPrice.setVisibility(View.GONE);
                    btn_confirm.setVisibility(View.GONE);
                    animationCartEmpty.setVisibility(View.VISIBLE);
                    textViewCartEmpty.setVisibility(View.VISIBLE);
                } else {
                    animationCartEmpty.setVisibility(View.GONE);
                    textViewCartEmpty.setVisibility(View.GONE);
                    textViewTotalPrice.setVisibility(View.VISIBLE);
                    btn_confirm.setVisibility(View.VISIBLE);
                }

                if (orderList.size() != 0)
                    recyclerViewOrders.setAdapter(cartAdapter);

                for (Order order : orderList) {
                    totalPrice += order.getPrice() * order.getQuantity();
                }
//                textViewTotalPrice.setText("Total price : " + String.format(Locale.ENGLISH, "%,.2f", totalPrice));
                textViewTotalPrice.setText("Total price : " + totalPrice);
                Log.d(TAG, "mohamed orders count is onSuccess: " + orderList.size());
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.e(TAG, "mohamed onError: " + e.getCause());
            }
        });


    }
}