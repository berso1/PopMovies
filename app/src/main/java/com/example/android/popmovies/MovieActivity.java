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

    private static final String KEY = "Please add a valid Key";
    private static final int MOVIE_LOADER_ID = 1;
    private String menuSelected;
    private TextView mEmptyStateTextView;
    private ProgressBar mLoadingIndicator;

    private String movieUrls[];
    private List<Movie> movieData;

    private LoaderManager loaderManager;
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter adapter;

    private boolean menuCalled;

    private boolean isConnected;


//Constructor=======================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// set up the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.rvNumbers);
        int numberOfColumns = Utility.calculateNoOfColumns(getApplicationContext());
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        //Set up the RecyclerViewAdapter
        adapter = new MyRecyclerViewAdapter(this, movieUrls);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


        menuSelected = getString(R.string.popular);
        menuCalled = false;
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mLoadingIndicator.setVisibility(View.VISIBLE);


            // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();
        loaderManager.initLoader(MOVIE_LOADER_ID, null, this);

        //Check for connectivity before calling the loader
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
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
//        mLoadingIndicator.setVisibility(View.VISIBLE);
        String movieUri = POPULAR_MOVIE_DB_URL + KEY;
        if (menuSelected.equals(getString(R.string.top_rated))) {
            movieUri = TOP_RATED_MOVIE_DB_URL + KEY;
        }
        return new MovieLoader(this, movieUri);
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Movie>> loader, List<Movie> movies) {

        if (movies == null || movies.size() == 0) {
            movieUrls = new String[0];
            if (isConnected) {
                mEmptyStateTextView.setText(R.string.no_movies);
            }
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

        if(menuCalled){
            menuCalled = false;
            recyclerView.setAdapter(adapter);
            adapter.swapData(movies);
        }else{
            adapter.swapData(movies);
        }
        mLoadingIndicator.setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Movie>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.swapData(null);
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
                menuSelected = getString(R.string.popular);
                menuCalled = true;
                loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
                return true;
            case R.id.top_rated:
                menuSelected = getString(R.string.top_rated);
                menuCalled = true;
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
            int columns = (int) dpWidth / 185;
            if (columns == 1) columns = 2;
            return columns;
        }
    }

//FINISH============================================================================================
}