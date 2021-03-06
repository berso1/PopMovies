package com.example.android.popmovies;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popmovies.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<MovieExtras>> {
    private static final String LOG_TAG = MovieDetailActivity.class.getName();
    private static final int MOVIE_LOADER_ID = 1;
    private static final String MOVIE_DB_URL = "http://api.themoviedb.org/3/movie/";
    private static final String TRAILERS = "/videos?api_key=";
    private static final String REVIEWS = "/reviews?api_key=";
    private static String KEY;
    private String movieId;
    private ArrayList<MovieExtras> trailers;
    private ListView trailers_list_view;
    private TrailerAdapter adapter;
    private Movie currentMovie;
    private int favoriteMovie;

//onCreate==========================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //Get the layout objects references
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        TextView title_text_view = (TextView) findViewById(R.id.title);
        TextView overview_text_view = (TextView) findViewById(R.id.overview);
        TextView vote_average_text_view = (TextView) findViewById(R.id.vote_average);
        TextView release_date_text_view = (TextView) findViewById(R.id.release_date);
        trailers_list_view = (ListView) findViewById(R.id.trailers);
        final TextView tag_favorite_text_view = (TextView) findViewById(R.id.tag_favorites);

        MovieUtils.setListViewHeightBasedOnChildren(trailers_list_view);
        trailers_list_view.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        //Get intent and set data to layout
        Intent intent = getIntent();
        currentMovie = intent.getExtras().getParcelable("currentMovie");
        KEY = intent.getExtras().get("KEY").toString();
        title_text_view.setText(currentMovie.getTitle());
        overview_text_view.setText(currentMovie.getOverview());
        vote_average_text_view.setText(getString(R.string.vote_average, currentMovie.getVoteAverage()));
        release_date_text_view.setText(getString(R.string.release_date, currentMovie.getReleaseDate()));
        movieId = currentMovie.getId();
        favoriteMovie = currentMovie.getFavorite();

        if(savedInstanceState != null && savedInstanceState.containsKey("favorite")) {
            favoriteMovie = savedInstanceState.getInt("favorite");
        }

        ImageButton favorites = (ImageButton) findViewById(R.id.movie_favorite_button);
        if(favoriteMovie == 1){
            favorites.setSelected(true);
            tag_favorite_text_view.setText(R.string.remove_from_favorites);

        }

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                button.setSelected(!button.isSelected());
                if (button.isSelected()) {
                    favoriteMovie = 1;
                    tag_favorite_text_view.setText(R.string.remove_from_favorites);
                    saveMovie();
                }else{
                    tag_favorite_text_view.setText(R.string.add_to_favorites);

                    favoriteMovie = 0;
                    deleteMovie();
                }
            }
        });


        Picasso
                .with(this)
                .load(currentMovie.getUrl())
                .fit()
                .into(imageView);

        adapter = new TrailerAdapter();
        trailers_list_view.setAdapter(adapter);

        LoaderManager loaderManager = getLoaderManager();

        // Check for savedInstanceState key movies, to avoid re load data from internet
        if(savedInstanceState == null || !savedInstanceState.containsKey("trailers")) {
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            trailers = savedInstanceState.getParcelableArrayList("trailers");
            adapter.swapData(trailers);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trailers", trailers);
        outState.putInt("favorite",favoriteMovie);
        super.onSaveInstanceState(outState);
    }

    private void deleteMovie(){
        long id = Long.parseLong(movieId);
        Uri movieUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
        int deletedRows = getContentResolver().delete(movieUri, null, null);
        if (deletedRows == 0 ) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.movie_deleted_Item_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.movie_removed_Item_good),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void saveMovie(){

            ContentValues values = new ContentValues();
            Uri newUri;
        //Insert Movie

            values.put(MovieEntry.COLUMN_TITLE, currentMovie.getTitle());
            values.put(MovieEntry.COLUMN_IMAGE_URL, currentMovie.getUrl());
            values.put(MovieEntry.COLUMN_OVERVIEW, currentMovie.getOverview());
            values.put(MovieEntry.COLUMN_VOTE_AVERAGE, currentMovie.getVoteAverage());
            values.put(MovieEntry.COLUMN_RELEASE_DATE, currentMovie.getReleaseDate());
            values.put(MovieEntry.COLUMN_MOVIE_ID, currentMovie.getId());
            values.put(MovieEntry.COLUMN_FAVORITE, favoriteMovie);
            newUri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null ) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.movie_insert_Item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.movie_insert_Item_good),
                        Toast.LENGTH_SHORT).show();
                //Close activity
              //  finish();
            }
        }


    private class TrailerAdapter extends ArrayAdapter<MovieExtras>  {

        private ArrayList<MovieExtras> mTrailers;

        TrailerAdapter() {
            super(MovieDetailActivity.this, R.layout.trailer_item, trailers);
            mTrailers = trailers;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View row;

            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.trailer_item, parent, false);
            } else {
                row = convertView;
            }

            ImageView play = (ImageView) row.findViewById(R.id.play);
            TextView tv = (TextView) row.findViewById(R.id.name);

            MovieExtras movieExtras = mTrailers.get(position);
            final String itemType = movieExtras.getItemType();
            final String name = movieExtras.getName();

            switch(itemType){
                case "Trailer":
                    play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    break;
                case "Review":
                    play.setImageResource(R.drawable.ic_border_color_black_24dp);
                    break;
            }

            tv.setText(name);

            trailers_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                        long id) {
                    MovieExtras movieExtras = mTrailers.get(position);
                    final String itemType = movieExtras.getItemType();
                    final String videoId = movieExtras.getUrl();
                    loadTrailerAndReview(videoId,itemType);
                }
            });

            return row;

        }

        void swapData(ArrayList<MovieExtras> trailer_data) {
            mTrailers = trailer_data;
            notifyDataSetChanged();
        }

        // total number of cells
        @Override
        public int getCount() {
            if(mTrailers != null){
                return mTrailers.size();
            }else{
                return 0;
            }
        }


    }

    private void loadTrailerAndReview(String id, String itemType) {
        Intent appIntent;
        Intent webIntent;
        switch (itemType) {
            case "Trailer":
                appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + id));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
                break;
            case "Review":
                webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(id));
                startActivity(webIntent);
                break;
        }
    }


//Loader methods====================================================================================

    @Override
    public Loader<ArrayList<MovieExtras>> onCreateLoader(int loaderId, Bundle args) {
                String movieUrl = MOVIE_DB_URL + movieId + TRAILERS + KEY;
                String reviewUrl = MOVIE_DB_URL + movieId + REVIEWS + KEY;
                return new TrailerAndReviewLoader(this, movieUrl,reviewUrl);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieExtras>> loader, ArrayList<MovieExtras> data) {
        trailers = data;
        adapter.swapData(trailers);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieExtras>> loader) {
        trailers = null;
    }

}


