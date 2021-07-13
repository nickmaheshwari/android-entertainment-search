package edu.uchicago.fullstack.androidretro.CC_model.apis;

import edu.uchicago.fullstack.androidretro.CC_model.models.MovieResponse;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieService {

    @GET("/")
    Single<MovieResponse> searchMovies(
            @Query("s") String search,
            @Query("apikey") String apiKey,
            @Query("page") int page
    );
    
}
