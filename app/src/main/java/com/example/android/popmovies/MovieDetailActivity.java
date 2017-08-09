package com.example.android.popmovies;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.android.popmovies.data.MovieProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<MovieExtras>> {
    private static final String LOG_TAG = MovieDetailActivity.class.getName();
    private static final int MOVIE_LOADER_ID = 1;
    private static final int TRAILER_LOADER_ID = 2;
    private static final String MOVIE_DB_URL = "http://api.themoviedb.org/3/movie/";
    private static final String TRAILERS = "/videos?api_key=";
    private static final String REVIEWS = "/reviews?api_key=";
    private static final String KEY = "53341150078643feca5e10aa6fc35020";
    private String movieId;
    private ArrayList<MovieExtras> trailers;
    private Context context;
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


        //Get intent and set data to layout
        Intent intent = getIntent();
        currentMovie = intent.getExtras().getParcelable("currentMovie");
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
            tag_favorite_text_view.setText(R.string.removeFromfavorites);

        }

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                button.setSelected(!button.isSelected());
                if (button.isSelected()) {
                    favoriteMovie = 1;
                    tag_favorite_text_view.setText(R.string.removeFromfavorites);
                    saveMovie();
                }else{
                    tag_favorite_text_view.setText(R.string.addTofavorites);

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

       // trailers = new ArrayList<>();
        trailers = null;
        LoaderManager loaderManager = getLoaderManager();

        // Check for savedInstanceState key movies, to avoid re load data from internet

        if(savedInstanceState == null || !savedInstanceState.containsKey("trailers")) {
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
            Log.v(LOG_TAG," no hay trailers");
        } else {
            trailers = savedInstanceState.getParcelableArrayList("trailers");
            adapter = new TrailerAdapter();
            trailers_list_view.setAdapter(adapter);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trailers", trailers);
        outState.putInt("favorite",favoriteMovie);
        super.onSaveInstanceState(outState);
    }

    public void deleteMovie(){
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

    public void saveMovie(){

            ContentValues values = new ContentValues();
            Uri newUri = null;
        //Insert Movie

            values.put(MovieEntry.COLUMN_TITLE, currentMovie.getTitle());
            values.put(MovieEntry.COLUMN_IMAGE_URL, currentMovie.getUrl());
            values.put(MovieEntry.COLUMN_OVERVIEW, currentMovie.getOverview());
            values.put(MovieEntry.COLUMN_VOTE_AVERAGE, currentMovie.getVoteAverage());
            values.put(MovieEntry.COLUMN_RELEASE_DATE, currentMovie.getReleaseDate());
            values.put(MovieEntry.COLUMN_MOVIE_ID, currentMovie.getId());
            values.put(MovieEntry.COLUMN_FAVORITE, favoriteMovie);
            newUri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);

            MovieProvider provider = new MovieProvider();

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


    class TrailerAdapter extends ArrayAdapter<MovieExtras>  {
        TrailerAdapter() {
            super(MovieDetailActivity.this, R.layout.trailer_item, trailers);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row;

            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.trailer_item, parent, false);
            } else {
                row = convertView;
            }

            ImageView play = (ImageView) row.findViewById(R.id.play);
            TextView tv = (TextView) row.findViewById(R.id.name);

            MovieExtras movieExtras = trailers.get(position);
         //   Log.v("item ",item+","+position);
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
                    MovieExtras movieExtras = trailers.get(position);
                    final String itemType = movieExtras.getItemType();
                    final String videoId = movieExtras.getUrl();
                    loadTrailerAndReview(videoId,itemType);
                }
            });

            return row;

        }

        void swapData(ArrayList<MovieExtras> trailer_data) {
            trailers = trailer_data;
            notifyDataSetChanged();
        }

    }

    public void loadTrailerAndReview(String id, String itemType) {
        Intent appIntent;
        Intent webIntent;
        Log.v(LOG_TAG,id+","+itemType);
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
        Log.v(LOG_TAG,""+trailers.size());
        adapter = new TrailerAdapter();
        trailers_list_view.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieExtras>> loader) {
        trailers = null;
    }

//cursor loader methods====================================================================================

    private LoaderCallbacks<Cursor> dataResultLoaderListener
            = new LoaderCallbacks<Cursor>() {

        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return null;
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

        }
    };

}


