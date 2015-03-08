package com.snilius.ledman.data;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author victor
 * @since 2/8/15
 */
public interface LedmanService {
    @POST("/on")
    String on();

    @POST("/off")
    String off();

    @GET("/status")
    Status status();

    @PUT("/set/{color}")
    String set(@Path("color") String color, @Query("level") String level);
}
