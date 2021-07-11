package edu.uchicago.fullstack.androidretro.AA_view.views;


import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import edu.uchicago.fullstack.androidretro.CC_model.models.Search;
import edu.uchicago.fullstack.androidretro.R;

public class OMDbListAdapter extends RecyclerView.Adapter<OMDbListAdapter.GoogleSearchResultHolder>{
    //the model for this adapter
    private List<Search> items = new ArrayList<>();

    //we need this for the click - this will be the calling Fragment
    AdapterCallback adapterCallback;

    //pass in the Fragment which IS an AdapterCallback
    public OMDbListAdapter(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

    @NonNull
    @Override
    public GoogleSearchResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.omdb_item, parent, false);

        return new GoogleSearchResultHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull GoogleSearchResultHolder holder, int position) {
        Search item = items.get(position);

        holder.titleTextView.setText(item.getTitle());
        holder.releaseDateTextView.setText(item.getYear());

        if (item.getPoster() != null) {
            String imageUrl = item.getPoster()
                    .replace("http://", "https://");

            Glide.with(holder.itemView)
                    .load(imageUrl)
                    .into(holder.smallThumbnailImageView);
        }

        if (item.getImdbID() != null) {
            String imdb_id = String.join(", ", item.getImdbID());
            holder.imdbIdTextView.setText(imdb_id);
        }

        holder.cardMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (adapterCallback != null) {
                        adapterCallback.onMovieClick(items,position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (items == null){
            return 0;
        }
        return items.size();
    }

    public void setItems(List<Search> items) {

        this.items = items;
        notifyDataSetChanged();
    }


    static class GoogleSearchResultHolder extends RecyclerView.ViewHolder {

        private final TextView titleTextView;
        private final TextView imdbIdTextView;
        private final TextView releaseDateTextView;
        private final ImageView smallThumbnailImageView;
        private final CardView cardMovie;

        public GoogleSearchResultHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.movie_item_title);
            imdbIdTextView = itemView.findViewById(R.id.movie_item_imdb_id);
            releaseDateTextView = itemView.findViewById(R.id.movie_item_year);
            smallThumbnailImageView = itemView.findViewById(R.id.movie_item_smallThumbnail);
            cardMovie = itemView.findViewById(R.id.card_movie);
        }
    }
    //the adapter callback will be the Fragment
    public interface AdapterCallback {
        void onMovieClick(List<Search> volumes, int position);

    }
}
