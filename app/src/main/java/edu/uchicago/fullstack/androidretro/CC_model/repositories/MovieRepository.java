package edu.uchicago.fullstack.androidretro.CC_model.repositories;

import edu.uchicago.fullstack.androidretro.CC_model.apis.MovieService;
import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class MovieRepository {
    private static final String MOVIE_SEARCH_SERVICE_BASE_URL = "https://www.omdbapi.com/";

    public static MovieService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_SEARCH_SERVICE_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(MovieService.class);
    }


}
