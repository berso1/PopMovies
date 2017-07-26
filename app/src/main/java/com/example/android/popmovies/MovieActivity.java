package com.example.android.popmovies;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class MovieActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener,
        LoaderManager.LoaderCallbacks<List<Movie>> {

//Global Variables===================================================================================

    private static final String POPULAR_MOVIE_DB_URL = "http://api.themoviedb.org/3/movie/popular?api_key=";
    private static final String TOP_RATED_MOVIE_DB_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=";

    private static final String KEY = "53341150078643feca5e10aa6fc35020";
    private static final int MOVIE_LOADER_ID = 1;
    private RecyclerView recyclerView;
    private String menu_selected;
    private TextView mEmptyStateTextView;
    private ProgressBar mLoadingIndicator;

    private String movieUrls[];
    private List<Movie> movieData;
    private LoaderManager loaderManager;


//Constructor=======================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// set up the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.rvNumbers);
        int numberOfColumns = Utility.calculateNoOfColumns(getApplicationContext());
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        menu_selected = getString(R.string.popular);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mLoadingIndicator.setVisibility(View.VISIBLE);



        //Check for connectivity before calling the loader
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loaderManager = getLoaderManager();
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            mLoadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_connection);
        }
    }

//ON CLICK CALL MovieDetailActivity=================================================================

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MovieActivity.this, MovieDetailActivity.class);
        //set the clicked movie info to pass to MovieDetailActivity
        Movie currentMovie = movieData.get(position);
        intent.putExtra("title", currentMovie.getTitle());
        intent.putExtra("url", currentMovie.getUrl());
        intent.putExtra("overview", currentMovie.getOverview());
        intent.putExtra("voteAverage", currentMovie.getVoteAverage());
        intent.putExtra("releaseDate", currentMovie.getReleaseDate());

        startActivity(intent);
    }

//LOADER METHODS====================================================================================


    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        mLoadingIndicator.setVisibility(View.VISIBLE);

        String movieUri = POPULAR_MOVIE_DB_URL + KEY;
        if (menu_selected.equals(getString(R.string.top_rated))) {
            movieUri = TOP_RATED_MOVIE_DB_URL + KEY;
        }
        return new MovieLoader(this, movieUri);
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Movie>> loader, List<Movie> movies) {

        if (movies == null || movies.size() == 0) {
            movieUrls = new String[0];
            mEmptyStateTextView.setText(R.string.no_movies);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.GONE);
        } else {
            movieData = movies;
            movieUrls = new String[movies.size()];
            for (int i = 0; i < movies.size(); i++) {
                movieUrls[i] = movies.get(i).getUrl();
            }
            mEmptyStateTextView.setVisibility(View.GONE);
        }

        //Set up the RecyclerViewAdapter
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, movieUrls);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        mLoadingIndicator.setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Movie>> loader) {
        // Loader reset, so we can clear out our existing data.
        movieUrls = null;
    }


//MENU METHODS======================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.popular:
                menu_selected = getString(R.string.popular);
                loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
                return true;
            case R.id.top_rated:
                menu_selected = getString(R.string.top_rated);
                loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //SCREEN COLUMN CALCULATOR==========================================================================
    public static class Utility {
        public static int calculateNoOfColumns(Context context) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            return (int) (dpWidth / 185);
        }
    }

//FINISH============================================================================================
}