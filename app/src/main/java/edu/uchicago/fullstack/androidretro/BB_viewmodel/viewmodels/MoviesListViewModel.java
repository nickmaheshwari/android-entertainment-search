package edu.uchicago.fullstack.androidretro.BB_viewmodel.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import edu.uchicago.fullstack.androidretro.CC_model.cache.Cache;
import edu.uchicago.fullstack.androidretro.CC_model.models.MovieResponse;
import edu.uchicago.fullstack.androidretro.CC_model.models.Search;
import edu.uchicago.fullstack.androidretro.CC_model.repositories.MovieRepository;
import edu.uchicago.fullstack.androidretro.CC_model.apis.MovieService;
import edu.uchicago.fullstack.androidretro.CC_model.utils.LoadState;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class MoviesListViewModel extends ViewModel {
    //the data
    public LiveData<PagedList<Search>> moviesList;


    //helpers
    private final CompositeDisposable compositeDisposable;
    private final MovieDataSourceFactory movieDataSourceFactory;


    public MoviesListViewModel() {
        super();
        compositeDisposable = new CompositeDisposable();
        movieDataSourceFactory = new MovieDataSourceFactory(compositeDisposable, MovieRepository.getService());
        final int PAGE_SIZE = 10;
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        moviesList = new LivePagedListBuilder(movieDataSourceFactory, config).build();

    }

    //get the state
    public LiveData<LoadState> getState() {

        return Transformations.switchMap(
                movieDataSourceFactory.googlePagedKeyedDataSourceMutableLiveData,
                MoviePagedKeyedDataSource::getState
        );
    }

    //return the list
    public LiveData<PagedList<Search>> getMoviesList() {
        return moviesList;
    }


    public void retry() {
        movieDataSourceFactory.googlePagedKeyedDataSourceMutableLiveData.getValue().retry();
    }

    public Boolean listIsEmpty() {
        if (moviesList.getValue() == null) {
            return true;
        } else {
            return moviesList.getValue().isEmpty();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }


}

//inner class
class MovieDataSourceFactory extends DataSource.Factory<Integer, Search> {
    public MutableLiveData<MoviePagedKeyedDataSource> googlePagedKeyedDataSourceMutableLiveData = new MutableLiveData<MoviePagedKeyedDataSource>();
    public CompositeDisposable compositeDisposable;
    public MovieService movieService;

    public MovieDataSourceFactory(CompositeDisposable compositeDisposable, MovieService movieService) {
        this.compositeDisposable = compositeDisposable;
        this.movieService = movieService;
    }

    @NonNull
    @Override
    public DataSource<Integer, Search> create() {
        MoviePagedKeyedDataSource moviePagedKeyedDataSource = new MoviePagedKeyedDataSource(movieService, compositeDisposable);
        googlePagedKeyedDataSourceMutableLiveData.postValue(moviePagedKeyedDataSource);
        return moviePagedKeyedDataSource;
    }
}

//inner class
class MoviePagedKeyedDataSource extends PageKeyedDataSource<Integer, Search> {

    MovieService movieService;
    CompositeDisposable compositeDisposable;
    MutableLiveData<LoadState> state = new MutableLiveData<>();
    Completable retryCompletable = null;


    private final String API_KEY = "dc077f32";
    int startPage = 1;

    public MutableLiveData<LoadState> getState() {
        return state;
    }

    MoviePagedKeyedDataSource(MovieService movieService,
                              CompositeDisposable compositeDisposable) {
        super();
        //this registers the MoviesPagedKeyedDataSource as a subscriber.
        //notice the method below annotated with @Subscribe(threadMode = ThreadMode.MAIN)
        EventBus.getDefault().register(this);
        this.movieService = movieService;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Search> callback) {
        updateState(LoadState.LOADING);

        //this is a set of Disposables
        compositeDisposable.add(
                movieService.searchMovies(Cache.getInstance().getKeyword(), API_KEY, startPage)
                        .subscribe(new BiConsumer<MovieResponse, Throwable>() {
                            @Override
                            public void accept(MovieResponse response, Throwable throwable) throws Exception {

                                if (response != null && response.getSearch() != null && throwable == null) {

                                    if (response.getSearch().size() == 0){
                                        updateState(LoadState.NODATA);
                                    } else {
                                        Log.d("RESPONSE_CONSUME_LIA",  response + "");
                                        updateState(LoadState.DONE);
                                        callback.onResult(response.getSearch(), null, startPage);
                                    }
                                }
                                else {
                                    updateState(LoadState.NODATA);
                                    setRetry(new Action() {
                                        @Override
                                        public void run() throws Exception {
                                            Log.d("RESPONSE_CONSUME_LIE", response + "");
                                            loadInitial(params, callback);
                                        }
                                    });
                                }

                            }
                        }));


    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Search> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Search> callback) {
        updateState(LoadState.LOADING);

        startPage = startPage + 1;

        Log.d("PAGING", params.requestedLoadSize + ":" + params.key);

        compositeDisposable.add(
                movieService.searchMovies(Cache.getInstance().getKeyword(), API_KEY, startPage)
                        .subscribe(new BiConsumer<MovieResponse, Throwable>() {
                            @Override
                            public void accept(MovieResponse response, Throwable throwable) throws Exception {
                                if (response != null && response.getSearch() != null) {
                                    Log.d("RESPONSE_CONSUME_LAA", params.key + "");
                                    updateState(LoadState.DONE);
                                    callback.onResult(response.getSearch(), startPage);
                                }else{
                                    updateState(LoadState.ERROR);
                                    setRetry(new Action() {
                                        @Override
                                        public void run() throws Exception {
                                            Log.d("RESPONSE_CONSUME_LAE", params.key + "");
                                            loadAfter(params, callback);
                                        }
                                    });
                                }

                            }
                        }));

    }

    private void updateState(LoadState loadState) {
        this.state.postValue(loadState);
        Log.d("EVENT_BUS_UPDATE_STATUS",   startPage + " " + loadState);

    }

    public void retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }
    }

    private void setRetry(Action action) {
        if (action == null) {
            retryCompletable = null;
        } else {
            retryCompletable = Completable.fromAction(action);
        }

    }

    //the IDE does not recognize this method, but the EventBus IS actually using it.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String keyword) {

        invalidate();
        compositeDisposable.dispose();
        Log.d("EVENT_BUS_AFTER",    compositeDisposable.size() + " : INVALIDATE AND DISPOSE EXISTING CALLBACKS, NOW LISTENING for " + keyword + " VolumeResponses from api...");
    }

}
