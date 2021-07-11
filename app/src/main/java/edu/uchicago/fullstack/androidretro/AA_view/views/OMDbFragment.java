package edu.uchicago.fullstack.androidretro.AA_view.views;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import edu.uchicago.fullstack.androidretro.BB_viewmodel.viewmodels.ResultsListViewModel;
import edu.uchicago.fullstack.androidretro.CC_model.models.MovieResponse;
import edu.uchicago.fullstack.androidretro.CC_model.models.Search;
import edu.uchicago.fullstack.androidretro.R;

public class OMDbFragment extends Fragment implements OMDbListAdapter.AdapterCallback{
    private EditText editQuery;
    private ProgressBar progressBar;

    private OMDbListAdapter OMDbListAdapter;
    private ResultsListViewModel resultsListViewModel;
    private final String API_KEY = "dc077f32";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        OMDbListAdapter = new OMDbListAdapter(this);
        resultsListViewModel = ViewModelProviders.of(this).get(ResultsListViewModel.class);

        resultsListViewModel.getMovieResponseLiveData().observe(getViewLifecycleOwner(), new Observer<MovieResponse>(){
            @Override
            public void onChanged(MovieResponse movieResponse) {
                System.out.println(movieResponse);
                if (movieResponse != null) {
                    progressBar.setVisibility(View.GONE);

                    OMDbListAdapter.setItems(movieResponse.getSearch());

                    OMDbListAdapter.notifyDataSetChanged();
                }
            }
        });
        // Inflate the layout for this fragment
        View containerView = inflater.inflate(R.layout.fragment_omdb, container, false);
        editQuery = containerView.findViewById(R.id.editQuery);
        progressBar = containerView.findViewById(R.id.progressBar);



        editQuery.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    MainActivity.hideKeyboardFrom(getContext(), OMDbFragment.this.getView());
                    resultsListViewModel.searchMovies(editQuery.getText().toString(), API_KEY);
                    progressBar.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        progressBar = containerView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //set the adapter to the recycler-view
        RecyclerView recyclerView = containerView.findViewById(R.id.recyclerMovies);
        recyclerView.setAdapter(OMDbListAdapter);

        return containerView;
    }

    @Override
    public void onMovieClick(List<Search> items, int position) {

        try {
            String title = items.get(position).getTitle();
            String year = items.get(position).getYear();
            String imageUrlLarge = items.get(position).getPoster();
            String imdbID = items.get(position).getImdbID();
            String type = items.get(position).getType();

            //create bundle and set values that will be used in the DetailFragment
            Bundle bundle = new Bundle();
            bundle.putString("imdbID", imdbID);
            bundle.putString("title", title);
            bundle.putString("year", year);
            bundle.putString("posterUrl", imageUrlLarge);
            bundle.putString("type", type);

            //pass in the bundle aka "arguments".
            Navigation.findNavController(getView()).navigate(R.id.action_OMDbFragment_to_detailFragment2, bundle);

        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(getView(), "Insufficient details", Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", null)
                    .setActionTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_light))
                    .show();
        }
    }
}
