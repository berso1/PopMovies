package com.example.android.popmovies;

// Created by berso on 7/23/17.


class Movie {
    private final String mTitle;
    private final String mUrl;
    private final String mOverview;
    private final String mVoteAverage;
    private final String mReleaseDate;

    public Movie(String title, String url, String overview, String vote_average, String release_date) {
        mTitle = title;
        mUrl = url;
        mOverview = overview;
        mVoteAverage = vote_average;
        mReleaseDate = release_date;
    }


    public String getTitle() {
        return mTitle;
    }


    public String getUrl() {
        return mUrl;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }


}
