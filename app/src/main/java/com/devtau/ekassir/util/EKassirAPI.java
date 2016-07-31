package com.devtau.ekassir.util;

import com.devtau.ekassir.util.Constants;
import com.devtau.ekassir.model.Order;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface EKassirAPI {
    //Base URL: always ends with /
    //@Url: DO NOT start with /
    @GET(Constants.URL_ENDPOINT)
    Call<List<Order>> executeRequest();
}
