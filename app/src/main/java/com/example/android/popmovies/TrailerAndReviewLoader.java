package com.example.android.popmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

// Created by berso on 7/31/17.
class TrailerAndReviewLoader extends AsyncTaskLoader<ArrayList<MovieExtras>> {

    private final String mTrailerUrl;
    private final String mReviewUrl;


    public TrailerAndReviewLoader(Context context, String trailerUrl,String reviewUrl) {
        super(context);
        this.mTrailerUrl = trailerUrl;
        this.mReviewUrl = reviewUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<MovieExtras> loadInBackground() {
        if (mTrailerUrl == null || mReviewUrl == null ) {
            return null;
        }

        ArrayList<MovieExtras> trailers = (ArrayList<MovieExtras>) MovieUtils.fetchTrailersAndReviewData(mTrailerUrl,mReviewUrl);
        Log.v("loader",""+trailers.size());

        return trailers;
    }
}

