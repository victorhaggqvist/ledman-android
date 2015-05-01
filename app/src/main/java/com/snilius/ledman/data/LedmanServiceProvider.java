package com.snilius.ledman.data;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.snilius.ledman.BuildConfig;
import com.snilius.ledman.util.TokenFactory;

import java.util.Date;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import timber.log.Timber;

/**
 * @author victor
 * @since 2/8/15
 */
public class LedmanServiceProvider {

    private static String mApikey;
    private static String mEndpoint;

    public static void setApikey(String apikey){
        mApikey = apikey;
    }

    public static void setEndpoint(String endpoint) {
        mEndpoint = endpoint;
    }

    public static LedmanService getService(){
        if (Holder.service == null) {
            Timber.i("creating rest adapter");

            RequestInterceptor authInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    TokenFactory factory = new TokenFactory(mApikey);
                    request.addQueryParam("token", factory.getCredentials().getToken());
                    request.addQueryParam("timestamp", String.valueOf(factory.getCredentials().getTimestamp()));
                }
            };

            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .registerTypeAdapter(Date.class, new DateTypeAdapter())
                    .create();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(mEndpoint)
                    .setConverter(new GsonConverter(gson))
                    .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                    .setRequestInterceptor(authInterceptor)
                    .build();

            Holder.service = restAdapter.create(LedmanService.class);
        } else {
            Timber.i("using existing rest adapter");
        }

        return Holder.service;
    }

    public static class Holder {
        private static LedmanService service = getService();
    }
}
