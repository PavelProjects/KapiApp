package com.pavel.kapicard.Api;

import com.pavel.kapicard.model.Card;
import com.pavel.kapicard.model.Client;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Api {
    @GET("kapibike-1.0-SNAPSHOT/card/{id}")
    Call<Card> getCardById(@Header("Authorization") String authkey, @Path("id") String id);
    @GET("kapibike-1.0-SNAPSHOT/card/phone/{phone}")
    Call<Card> cardByPhone (@Header("Authorization") String authkey, @Path("phone") int phone);
    @PUT("kapibike-1.0-SNAPSHOT/card/balance/change")
    Call<Void> changeBalance(@Header("Authorization") String authkey, @Body Card card);
    @POST("kapibike-1.0-SNAPSHOT/card/add")
    Call<Void> addCard(@Header("Authorization") String authkey, @Body Card card);
    @GET("kapibike-1.0-SNAPSHOT/card/all")
    Call<List<Card>> allCards (@Header("Authorization") String authkey);


    @GET("kapibike-1.0-SNAPSHOT/user/auth")
    Call<Void> authUser (@Header("Authorization") String authkey);
    @PUT("kapibike-1.0-SNAPSHOT/user/client/change")
    Call<Void> changeOwnerData (@Header("Authorization") String authkey, @Body Client client);

}
