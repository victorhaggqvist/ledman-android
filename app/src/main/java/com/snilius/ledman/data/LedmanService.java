package com.snilius.ledman.data;

import retrofit.http.GET;
import retrofit.http.POST;

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
    String status();
}
