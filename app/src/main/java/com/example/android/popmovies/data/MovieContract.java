package com.example.android.popmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

// Created by berso on 7/31/17.


public class MovieContract {

    private MovieContract() {//do not remove
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.popmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_MOVIE_EXTRAS = "movie_extras";


    public static class MovieEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        public static final String TABLE_NAME = "Movies";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_FAVORITE = "favorite";

    }

    public static class MovieExtrasEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_EXTRAS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_EXTRAS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIE_EXTRAS);

        public static final String TABLE_NAME = "MovieExtrasEntry";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_TYPE = "item_type";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_VIDEO_ID = "video_id";
    }

}
