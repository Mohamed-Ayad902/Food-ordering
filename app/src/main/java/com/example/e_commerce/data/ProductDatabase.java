package com.example.e_commerce.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.e_commerce.converters.RatingConverter;
import com.example.e_commerce.models.Order;
import com.example.e_commerce.models.Product;

@Database(entities = {Product.class, Order.class}, version = 4)
@TypeConverters(RatingConverter.class)
public abstract class ProductDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "products_database";
    private static ProductDatabase instance;

    public abstract ProductDao dao();

    public static synchronized ProductDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ProductDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
