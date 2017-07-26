package com.example.android.popmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;
// Created by berso on 7/23/17.

class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private final String mUrl;

    public MovieLoader(Context context, String Url) {
        super(context);
        mUrl = Url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        return MovieUtils.fetchMovieData(mUrl);
    }
}
