package com.github.clans.daviart.api;

import android.text.TextUtils;

import com.github.clans.daviart.models.Credentials;
import com.github.clans.daviart.models.NewestArts;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class DeviantArtApi {

    private static final String BASE_URL = "https://www.deviantart.com/";
    private static final String VERSION = "v1";
    private static final String AUTHENTICATION = "oauth2";
    private static final String ENDPOINT = "api/" + VERSION + "/" + AUTHENTICATION + "/";

    // TODO: encrypt for production
    private static final String CLIENT_SECRET = "c3266c3e9240f25e55fe3f09f95a680b";
    private static final int CLIENT_ID = 4751;

    private static DeviantArtApi api;
    private static DeviantArtService service;

    private DeviantArtApi() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        service = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build()
                .create(DeviantArtService.class);
    }

    public static DeviantArtApi getInstance() {
        if (api == null) {
            api = new DeviantArtApi();
        }

        return api;
    }

    private interface DeviantArtService {

        @GET(AUTHENTICATION + "/token?grant_type=client_credentials")
        rx.Observable<Credentials> getAccessToken(@Query("client_id") int clientId, @Query("client_secret") String clientSecret);

        @GET(ENDPOINT + "browse/newest?mature_content=false&limit=20")
        rx.Observable<NewestArts> getNewestByCategory(@Query("category_path") String category,
                                                      @Query("access_token") String accessToken);
    }

    public rx.Observable<Credentials> getAccessToken() {
        return service.getAccessToken(CLIENT_ID, CLIENT_SECRET);
    }

    public rx.Observable<NewestArts> getNewestArts(String category, String accessToken) {
        return service.getNewestByCategory(TextUtils.isEmpty(category) ? "/" : category, accessToken);
    }
}
