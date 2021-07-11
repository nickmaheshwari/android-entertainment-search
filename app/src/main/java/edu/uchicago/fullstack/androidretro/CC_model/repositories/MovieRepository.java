package edu.uchicago.fullstack.androidretro.CC_model.repositories;

import androidx.lifecycle.MutableLiveData;

import edu.uchicago.fullstack.androidretro.CC_model.service.MovieService;
import edu.uchicago.fullstack.androidretro.CC_model.models.MovieResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieRepository {
    private static final String MOVIE_SEARCH_SERVICE_BASE_URL = "https://www.omdbapi.com/";

    private final MovieService movieSearchService;
    private final MutableLiveData<MovieResponse> movieResponseLiveData;


    public MovieRepository(MutableLiveData<MovieResponse> movieResponseLiveData) {
        //instantiate live data object
        this.movieResponseLiveData = movieResponseLiveData;

        //used for intercepting outgoing and incoming traffic
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        //use retrofit to create this service
        movieSearchService = new retrofit2.Retrofit.Builder()
                .baseUrl(MOVIE_SEARCH_SERVICE_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MovieService.class);
    }

    //corresponds to the GET call from API
    public void searchMovies(String keyword, String apiKey){
        movieSearchService.searchMovies(keyword, apiKey)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.body() != null) {
                            movieResponseLiveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        movieResponseLiveData.postValue(null);
                    }
                });
    }
}
