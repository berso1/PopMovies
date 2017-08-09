package com.example.android.popmovies;

// Created by berso on 7/23/17.


import android.os.Parcel;
import android.os.Parcelable;

class Movie implements Parcelable{

    private final String mTitle;
    private final String mUrl;
    private final String mOverview;
    private final String mVoteAverage;
    private final String mReleaseDate;
    private final String mId;
    private final int mFavorite;

    public Movie(String title, String url, String overview, String vote_average, String release_date,String id,int favorite) {
        mTitle = title;
        mUrl = url;
        mOverview = overview;
        mVoteAverage = vote_average;
        mReleaseDate = release_date;
        mId = id;
        mFavorite = favorite;
    }


    private Movie(Parcel in) {
        mTitle = in.readString();
        mUrl = in.readString();
        mOverview = in.readString();
        mVoteAverage = in.readString();
        mReleaseDate = in.readString();
        mId = in.readString();
        mFavorite = in.readInt();
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

    public String getId() {return  mId;}

    public int getFavorite() { return mFavorite;}




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mUrl);
        dest.writeString(mOverview);
        dest.writeString(mVoteAverage);
        dest.writeString(mReleaseDate);
        dest.writeString(mId);
        dest.writeInt(mFavorite);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
