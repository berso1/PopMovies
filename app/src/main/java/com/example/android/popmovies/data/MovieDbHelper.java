package com.example.android.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popmovies.data.MovieContract.MovieEntry;

// Created by berso on 7/31/17.

class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "Movies.db";

    private static final String CREATE_MOVIES_TABLE =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                    MovieEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL," +
                    MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                    MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL," +
                    MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                    MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL,"+
                    MovieEntry.COLUMN_FAVORITE + " INT NOT NULL DEFAULT 0);";

    private static final String CREATE_MOVIE_EXTRAS_TABLE =
            "CREATE TABLE " + MovieContract.MovieExtrasEntry.TABLE_NAME + " (" +
                    MovieContract.MovieExtrasEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MovieContract.MovieExtrasEntry.COLUMN_ITEM_TYPE + " TEXT NOT NULL," +
                    MovieContract.MovieExtrasEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    MovieContract.MovieExtrasEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL);";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOVIES_TABLE);
        db.execSQL(CREATE_MOVIE_EXTRAS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieExtrasEntry.TABLE_NAME);
        onCreate(db);
    }
}
