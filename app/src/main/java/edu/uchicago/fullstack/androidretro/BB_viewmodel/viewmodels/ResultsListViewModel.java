package edu.uchicago.fullstack.androidretro.BB_viewmodel.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.uchicago.fullstack.androidretro.CC_model.models.MovieResponse;
import edu.uchicago.fullstack.androidretro.CC_model.repositories.MovieRepository;

public class ResultsListViewModel extends AndroidViewModel {
    private MovieRepository movieRepository;
    private MutableLiveData<MovieResponse> movieResponseLiveData;

    public ResultsListViewModel(@NonNull Application application){
        super(application);
        movieResponseLiveData = new MutableLiveData<>();
        movieRepository = new MovieRepository(movieResponseLiveData);
    }

    //perform search
    public void searchMovies(String keyword, String apiKey){
        movieRepository.searchMovies(keyword, apiKey);
    }

    //getter
    public LiveData<MovieResponse> getMovieResponseLiveData(){
        return movieResponseLiveData;
    }

    //effective setter from cached Serialized Object
    public void refreshFromCache(MovieResponse movieResponse){
        movieResponseLiveData.postValue(movieResponse);
    }


}