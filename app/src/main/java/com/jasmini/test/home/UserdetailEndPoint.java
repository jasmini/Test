

package com.jasmini.test.home;

import com.jasmini.test.model.UserDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserdetailEndPoint {

    @GET("api1.php")
    Call<List<UserDetails>> getUserDetails();
}
