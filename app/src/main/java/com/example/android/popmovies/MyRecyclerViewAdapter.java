package com.example.android.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class MyRecyclerViewAdapter  extends
        RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>{

    private String[] mMovies;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final Context context;
    private ImageView movieIv;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, String[] movies) {
        this.mInflater = LayoutInflater.from(context);
        this.mMovies = movies;
        this.context = context;
    }

    void swapData(List<Movie> movies) {
        String [] movieList = null;
        if(movies!=null) {
            movieList = new String[movies.size()];
            for (int i = 0; i < movies.size(); i++) {
                movieList[i]=movies.get(i).getUrl();
            }
        }
        this.mMovies = movieList;
        notifyDataSetChanged();
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        movieIv = (ImageView) view.findViewById(R.id.movie);
        return new ViewHolder(view);
    }

    // binds the data to the text_view in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        String movieUrl = mMovies[position];
        Picasso
                .with(context)
                .load(movieUrl)
                .fit()
                .into(movieIv);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        if(mMovies != null){
            return mMovies.length;
        }else{
            return 0;
        }
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position);
    }



}
