package edu.uchicago.fullstack.androidretro.AA_view.adapters;

import edu.uchicago.fullstack.androidretro.CC_model.models.Search;
import edu.uchicago.fullstack.androidretro.CC_model.utils.Constants;
import edu.uchicago.fullstack.androidretro.CC_model.utils.LoadState;
import edu.uchicago.fullstack.androidretro.R;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class MovieListAdapter extends PagedListAdapter<Search, RecyclerView.ViewHolder>{
    private int DATA_VIEW_TYPE = 1;
    private int FOOTER_VIEW_TYPE = 2;

    private RetryCallback retry;

    private LoadState loadState = LoadState.LOADING;

    public MovieListAdapter(RetryCallback retryCallback) {
        super(NewsDiffCallback);
        this.retry = retryCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DATA_VIEW_TYPE)
            return MoviesViewHolder.create(parent);
        else
            return ListFooterViewHolder.create(parent);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < super.getItemCount())
            return DATA_VIEW_TYPE;
        else
            return FOOTER_VIEW_TYPE;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == DATA_VIEW_TYPE)
            ((MoviesViewHolder) holder).bind(getItem(position));
        else
            ((ListFooterViewHolder) holder).bind(loadState, retry);
    }

    @Override
    public int getItemCount() {
        if (hasFooter()) {
            return super.getItemCount() + 1;
        } else {
            return super.getItemCount();
        }
    }

    Boolean hasFooter() {
        return super.getItemCount() != 0 && (loadState == LoadState.LOADING || loadState == LoadState.ERROR);
    }

    public void setLoadState(LoadState loadState) {
        this.loadState = loadState;
        notifyItemChanged(super.getItemCount());
    }

    public static final DiffUtil.ItemCallback<Search> NewsDiffCallback =
            new DiffUtil.ItemCallback<Search>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Search oldArticle, @NonNull Search newArticle) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    if(oldArticle!=null)
                        return oldArticle.getTitle().equals(newArticle.getTitle());
                    else
                        return true;
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(
                        @NonNull Search oldArticle, @NonNull Search newArticle) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    if(oldArticle!=null && newArticle!=null)
                        return oldArticle.equals(newArticle);
                    else
                        return true;
                }
            };
}


//inner class
class MoviesViewHolder extends RecyclerView.ViewHolder {
    ImageView newsBanner;
    TextView movieItemTitle;
    TextView movieItemImdbId, movieItemYear;

    //need to reference to the containerView to communicate with the operating system
    public static View containerView;


    MoviesViewHolder(View itemView) {
        super(itemView);

        newsBanner =  itemView.findViewById(R.id.img_news_banner);
        movieItemTitle =  itemView.findViewById(R.id.movie_item_title);
        movieItemImdbId =  itemView.findViewById(R.id.movie_item_imdb_id);
        movieItemYear =  itemView.findViewById(R.id.movie_item_year);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void bind(Search item) {
        if (item != null) {
            movieItemTitle.setText(item.getTitle());
            if (null != item.getPoster()) {
                try {
                    Glide.with(containerView.getContext())
                            .load(item.getPoster())
                            .into(newsBanner);
                } catch (Exception e) {
                    e.getMessage();
                }

            }

            if (item.getImdbID() != null) {
                String imdbId = String.join(", ", item.getImdbID());
                movieItemImdbId.setText(imdbId);
            }

            movieItemYear.setText(item.getYear());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle movieData = new Bundle();
                    //refactor
                    if (null != item.getImdbID())
                        movieData.putString(Constants.imdbId, item.getImdbID());

                    if (null != item.getTitle())
                        movieData.putString(Constants.title, item.getTitle());

                    if (null != item.getYear())
                        movieData.putString(Constants.year, item.getYear());

                    if (null != item.getType())
                        movieData.putString(Constants.type, item.getType());

                    if (null != item.getPoster() )
                        movieData.putString(Constants.imageUrl, item.getPoster());

                    //pass in a walmart address to allow testing navigation
                    String[] walmarts = containerView.getContext().getResources().getStringArray(R.array.walmarts);
                    movieData.putString(Constants.address, walmarts[new Random().nextInt(walmarts.length)]);
                    Navigation.findNavController(view).navigate(R.id.action_resultsListFragment_to_detailFragment,movieData);



                }
            });
        }
    }



    public static MoviesViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        containerView = parent;
        return new MoviesViewHolder(view);
    }


}


//inner class
class ListFooterViewHolder extends RecyclerView.ViewHolder {

    ProgressBar progressBar;
    TextView errorText;
    RetryCallback retry;

    ListFooterViewHolder(View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progress_bar);
        errorText = itemView.findViewById(R.id.txt_error);

    }

    public void bind(LoadState status, RetryCallback retryCallback) {
        this.retry = retryCallback;
        if (status == LoadState.LOADING)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.INVISIBLE);
        if (status == LoadState.ERROR) {
            errorText.setVisibility(View.VISIBLE);
        } else {
            errorText.setVisibility(View.INVISIBLE);
        }

        errorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry.retry();
            }
        });
    }

    public static ListFooterViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_footer, parent, false);
        return new ListFooterViewHolder(view);
    }

}
