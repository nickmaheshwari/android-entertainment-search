package edu.uchicago.fullstack.androidretro.AA_view.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import edu.uchicago.fullstack.androidretro.AA_view.adapters.MovieListAdapter;
import edu.uchicago.fullstack.androidretro.AA_view.adapters.RetryCallback;
import edu.uchicago.fullstack.androidretro.BB_viewmodel.viewmodels.MoviesListViewModel;
import edu.uchicago.fullstack.androidretro.CC_model.cache.Cache;
import edu.uchicago.fullstack.androidretro.CC_model.models.Search;
import edu.uchicago.fullstack.androidretro.CC_model.utils.LoadState;
import edu.uchicago.fullstack.androidretro.R;

/**
 * A fragment representing a list of Items.
 */
public class ResultsListFragment extends Fragment {
    private TextView txtError, txtNoData;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private MoviesListViewModel viewModel = null;
    private MovieListAdapter moviesListAdapter = null;

    private TextView headerTitle;
    private Button imgShare, imgNavigation;

    private String keyword;
    public static EditText keywordEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View containerView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        viewModel = new ViewModelProvider(this).get(MoviesListViewModel.class);
        txtError = containerView.findViewById(R.id.txt_error);
        txtNoData = containerView.findViewById(R.id.txt_no_data);

        //toolbar
        headerTitle = containerView.findViewById(R.id.header_title);
        imgShare = containerView.findViewById(R.id.imgShare);
        imgNavigation = containerView.findViewById(R.id.imgNavigation);


        progressBar = containerView.findViewById(R.id.progress_bar);
        recyclerView = containerView.findViewById(R.id.recycler_view);
        keywordEditText = containerView.findViewById(R.id.edittext_moviesearch_keyword);
        keywordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    //consume the ACTION_UP, and only activate upon ACTION_DOWN
                    if (keyEvent.getAction()==KeyEvent.ACTION_UP) return true;

                    recyclerView.stopScroll();
                    recyclerView.getRecycledViewPool().clear();
                    keyword = keywordEditText.getEditableText().toString().trim();
                    Cache.getInstance().setKeyword(keyword);
                    //Emit to the EventBus to invalidate any previous observers to make way for new observers in the MoviesPagedKeyedDataSource
                    EventBus.getDefault().post(keyword);
                    moviesListAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;

            }
        });
        keywordEditText.setText(Cache.getInstance().getKeyword());


        moviesListAdapter = new MovieListAdapter(retryCallback);
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(moviesListAdapter);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,10);
        recyclerView.setItemViewCacheSize(10);

        viewModel.getMoviesList().observe(getActivity(), new Observer<PagedList<Search>>() {
            @Override
            public void onChanged(PagedList<Search> articles) {
                if(articles!=null) {
                    moviesListAdapter.submitList(articles);
                }
            }
        });




        txtError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.retry();
            }
        });

        viewModel.getState().observe(getActivity(), new Observer<LoadState>() {
            @Override
            public void onChanged(LoadState state) {
                MainActivity.hideKeyboardFrom(getContext(), ResultsListFragment.this.getView());
                recyclerView.setVisibility(View.VISIBLE);

                if (viewModel.listIsEmpty() && state == LoadState.LOADING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                if (viewModel.listIsEmpty() && state == LoadState.ERROR) {
                    txtError.setVisibility(View.VISIBLE);
                } else {
                    txtError.setVisibility(View.GONE);
                }


                if (state == LoadState.NODATA) {
                    recyclerView.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                }

                if (!viewModel.listIsEmpty()) {
                    if (state == null) {
                        moviesListAdapter.setLoadState(LoadState.DONE);
                    } else {
                        moviesListAdapter.setLoadState(state);
                    }
                }
                moviesListAdapter.notifyDataSetChanged();

            }

        });

        return containerView;
    }





    RetryCallback retryCallback = new RetryCallback() {
        @Override
        public void retry() {
            viewModel.retry();
        }
    };



    @Override
    public void onResume() {
        super.onResume();

        //make sure to update the toolbar
        headerTitle.setText(getResources().getString(R.string.article_list));
        imgShare.setVisibility(View.GONE);
        imgNavigation.setVisibility(View.GONE);


    }
}
