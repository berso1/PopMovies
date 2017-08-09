package com.example.android.popmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by berso on 8/1/17.
 */

public class TrailerAndReviewLoader extends AsyncTaskLoader<ArrayList<MovieExtras>> {

    private final String mTrailereUrl;
    private final String mReviewUrl;


    public TrailerAndReviewLoader(Context context, String trailerUrl,String reviewUrl) {
        super(context);
        this.mTrailereUrl = trailerUrl;
        this.mReviewUrl = reviewUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<MovieExtras> loadInBackground() {
        if (mTrailereUrl == null || mReviewUrl == null ) {
            return null;
        }

        ArrayList<MovieExtras> trailers = (ArrayList<MovieExtras>) MovieUtils.fetchTrailersAndReviewData(mTrailereUrl,mReviewUrl);
        Log.v("loader",""+trailers.size());

        return trailers;
    }
}

