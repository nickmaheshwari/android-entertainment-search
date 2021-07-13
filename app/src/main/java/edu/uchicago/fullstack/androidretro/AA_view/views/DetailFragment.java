package edu.uchicago.fullstack.androidretro.AA_view.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import edu.uchicago.fullstack.androidretro.CC_model.apis.MovieService;
import edu.uchicago.fullstack.androidretro.CC_model.utils.Constants;
import edu.uchicago.fullstack.androidretro.R;


public class DetailFragment extends Fragment {


    private TextView title;
    private TextView year;
    private TextView type;
    private TextView imdbId;
    private ImageView imgMovie;

    private Button btnPrevious;
    private String link, address;
    private TextView headerTitle;
    private Button imgShare, imgNavigation;
    MovieService movieService;
    private final String API_KEY = "dc077f32";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View containerView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (null == getArguments()) return containerView;

        //get refs to textViews
        title = containerView.findViewById(R.id.txt_title);
        imdbId = containerView.findViewById(R.id.txt_imdb_id);
        year = containerView.findViewById(R.id.txt_year);
        type = containerView.findViewById(R.id.txt_type);
        btnPrevious = containerView.findViewById(R.id.btnReturnPrevious);

        //get ref to imageView
        imgMovie = containerView.findViewById(R.id.img_movie);

        //title bar elements
        headerTitle = containerView.findViewById(R.id.header_title);
        imgShare = containerView.findViewById(R.id.imgShare);
        imgNavigation = containerView.findViewById(R.id.imgNavigation);

        //set title bar elements
        updateTheToolbar();

        //get the data from the args and set to the fields of this frag
        fetchDataFromBundleArguments(getArguments());

        //add behavior
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //use Android to manage the fragment backstack for you. This is preferred to calling Navigation
                getActivity().onBackPressed();

            }
        });

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, title.getText().toString().trim() + "\n \n" + type.getText().toString().trim());
                startActivity(sendIntent);
            }
        });

        imgNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + address));
                startActivity(intent);
            }
        });

        return containerView;
    }

    private void fetchDataFromBundleArguments(Bundle bundle) {
        if (bundle != null) {
            title.setText("Title: "+bundle.getString(Constants.title));
            year.setText("Release Year: "+ bundle.getString(Constants.year));
            imdbId.setText("IMdb ID: "+bundle.getString(Constants.imdbId));
            type.setText("Entertainment Type: "+bundle.getString(Constants.type));
            link = bundle.getString(Constants.imageUrl);
            address = bundle.getString(Constants.address);

            Glide.with(getActivity())
                    .load(link)
                    .into(imgMovie);
        }
    }




    @Override
    public void onResume() {
        super.onResume();

        //update the toolbar
        updateTheToolbar();
    }

    private void updateTheToolbar() {
        headerTitle.setText(getResources().getString(R.string.article_detail));
        imgShare.setVisibility(View.VISIBLE);
        imgNavigation.setVisibility(View.VISIBLE);
    }
}