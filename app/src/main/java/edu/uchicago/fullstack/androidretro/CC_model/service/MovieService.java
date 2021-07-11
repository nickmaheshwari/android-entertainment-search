package edu.uchicago.fullstack.androidretro.CC_model.service;

import edu.uchicago.fullstack.androidretro.CC_model.models.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieService {

    @GET("/")
    Call<MovieResponse> searchMovies(
            @Query("s") String search,
            @Query("apikey") String apiKey
    );
}
