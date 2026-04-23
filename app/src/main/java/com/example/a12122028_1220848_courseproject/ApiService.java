package com.example.a12122028_1220848_courseproject;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("7ef9b31c-3d40-493b-9ec5-f41f56b01c7a")
    Call<List<Category>> getCategories();
}
