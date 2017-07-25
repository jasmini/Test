package com.jasmini.test.showlist;

import com.jasmini.test.model.ListDetails;
import com.jasmini.test.model.UserDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;



public interface ListDetailsEndPoint {
    @GET("api2.php")
    Call<List<ListDetails>> getListDetails();
}
