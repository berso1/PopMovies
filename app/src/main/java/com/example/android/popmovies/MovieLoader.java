package com.example.android.popmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;
// Created by berso on 7/23/17.

class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String LOG_TAG = MovieLoader.class.getName();

    private final String mUrl;
    private final String mLoadType;
    private final Context mContext;

    public MovieLoader(Context context, String Url,String loadType) {
        super(context);
        mUrl = Url;
        mLoadType = loadType;
        mContext = context;
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
         Log.v(LOG_TAG,mLoadType);
        if(mLoadType.equals("favorites")){
            return MovieUtils.fetchFavoritesData(mContext);
        }else {
            return MovieUtils.fetchMovieData(mUrl,mContext);
        }
    }
}
