package com.example.e_commerce.converters;

import androidx.room.TypeConverter;

import com.example.e_commerce.models.Rating;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class RatingConverter {

    @TypeConverter
    public String fromRateToSting(List<Rating> rating) {
        return new Gson().toJson(rating);
    }

    @TypeConverter
    public List<Rating> fromStringToRate(String stringRate) {
        Type typeList = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(stringRate, typeList);
    }

}
