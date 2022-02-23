package com.example.e_commerce.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.e_commerce.models.Order;
import com.example.e_commerce.models.Product;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ProductDao {

    @Insert
    Completable insertProduct(Product product);

    @Delete
    Completable deleteProduct(Product product);

    @Query("select * from product_table")
    Observable<List<Product>> getProducts();

    @Query("select exists (select 1 from product_table where id=:productId)")
    boolean isFavourite(String productId);


    @Insert
    Completable insertOrder(Order order);

    @Query("select * from orders_table")
    Single<List<Order>> getOrders();

    @Query("delete from orders_table")
    Completable deleteAllOrders();

    @Update
    int updateOrder(Order order);

    @Delete
    Completable deleteOrder(Order order);

}
