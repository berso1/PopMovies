package com.example.android.popmovies;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.popmovies.data.MovieContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

// Created by berso on 7/23/17.

    /**
     * Helper methods related to requesting and receiving movie info from theMovieDB
     */
    final class MovieUtils {

        private static final String LOG_TAG = MovieUtils.class.getName();
        private static final String URL_PRE_STRING = "http://image.tmdb.org/t/p/w185/";
        private MovieUtils() { // Do not remove
        }

        /**
         * Query the THE_MOVIE_DB  and return List<Movie>.
         */
        public static List<Movie> fetchMovieData(String requestUrl,Context context) {

            // Create URL object
            URL url = createUrl(requestUrl);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error closing input stream", e);
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            // Return the {@link Event}
            return extractFeatureFromJson(jsonResponse,context);
        }


        /**
         * Query the THE_MOVIE_DB  and return an {@link Movie} object to represent a single movie.
         */
        public static List<MovieExtras> fetchTrailersAndReviewData(String trailerUrl, String reviewUrl) {
            // Create URL object
            URL tUrl = createUrl(trailerUrl);
            URL rUrl = createUrl(reviewUrl);
            // Perform HTTP request to the URL and receive a JSON response back
            String jsonTrailers = null;
            String jsonReviews = null;
            try {
                jsonReviews = makeHttpRequest(rUrl);
                jsonTrailers = makeHttpRequest(tUrl);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error closing input stream", e);
            }
            List<MovieExtras> trailersAndReviewList = extractTrailersFromJson(jsonTrailers);
            trailersAndReviewList.addAll(extractReviewsFromJson(jsonReviews));
            return trailersAndReviewList;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private static String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            if( url == null ){
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(15 * 1000 /* milliseconds */);
                urlConnection.setConnectTimeout(15 * 1000 /* milliseconds */);
                urlConnection.connect();
                if( urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }else{
                    Log.v(LOG_TAG,"error:"+urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG+" Invalid Url",e.toString());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }

            return jsonResponse;
        }

        /**
         * Returns new URL object from the given string URL.
         */

        private static URL createUrl(String stringUrl) {
            URL url;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private static String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        /**
         * Return an {@link Movie} object by parsing out information
         * about the first earthquake from the input earthquakeJSON string.
         */
        private static List<Movie> extractFeatureFromJson(String movieJSON,Context context) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(movieJSON)) {
                return null;
            }
            List<Movie> movies = new ArrayList<>();
            List<Movie> favorites = fetchFavoritesData(context);
            try {
                JSONObject baseJsonResponse = new JSONObject(movieJSON);
                JSONArray results = baseJsonResponse.getJSONArray("results");

                for( int i = 0; i < results.length(); i++){
                    JSONObject thisMovie = results.getJSONObject(i);
                    String title = thisMovie.getString("title");
                    String url   =  URL_PRE_STRING + thisMovie.getString("poster_path");
                    String overview = thisMovie.getString("overview");
                    String vote_average = thisMovie.getString("vote_average");
                    String release_date = thisMovie.getString("release_date");
                    String id = thisMovie.getString("id");
                    int favorite = 0;
                    if(isFavorite(favorites,id)){
                        favorite = 1;
                    }
                    movies.add(new Movie(title,url,overview,vote_average,release_date,id,favorite));
                }
                return movies;
            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("MovieUtils....", "Problem parsing the Movies JSON results", e);
            }
            return null;
        }

        private static List<MovieExtras> extractTrailersFromJson(String movieJSON) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(movieJSON)) {
                return null;
            }
            List<MovieExtras> movieExtras = new ArrayList<>();
            try {
                JSONObject baseJsonResponse = new JSONObject(movieJSON);
                JSONArray results = baseJsonResponse.getJSONArray("results");
                for( int i = 0; i < results.length(); i++){
                    JSONObject thisMovie = results.getJSONObject(i);
                    movieExtras.add(new MovieExtras("Trailer", thisMovie.getString("name"), thisMovie.getString("key")));
                }
                return movieExtras;
            } catch (JSONException e) {
                Log.e("MovieUtils....", "Problem parsing the Trailer JSON results", e);
            }
            return null;
        }

        private static List<MovieExtras> extractReviewsFromJson(String movieJSON) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(movieJSON)) {
                return null;
            }

            List<MovieExtras> movieExtras = new ArrayList<>();

            try {
                JSONObject baseJsonResponse = new JSONObject(movieJSON);
                JSONArray results = baseJsonResponse.getJSONArray("results");
                for( int i = 0; i < results.length(); i++){
                    JSONObject thisMovie = results.getJSONObject(i);
                    movieExtras.add(new MovieExtras("Review","Review by : "+thisMovie.getString("author") ,thisMovie.getString("url")));
                }
                return movieExtras;
            } catch (JSONException e) {
                Log.e("MovieUtils....", "Problem parsing the Review JSON results", e);
            }
            return null;
        }

        public static List<Movie> fetchFavoritesData(Context context) {
          //  MovieProvider provider = new MovieProvider();
            List<Movie> movies = new ArrayList<>();
            String[] mProjection =
                    {
                            MovieEntry.COLUMN_TITLE,
                            MovieEntry.COLUMN_IMAGE_URL,
                            MovieEntry.COLUMN_OVERVIEW,
                            MovieEntry.COLUMN_VOTE_AVERAGE,
                            MovieEntry.COLUMN_RELEASE_DATE,
                            MovieEntry.COLUMN_MOVIE_ID,
                            MovieEntry.COLUMN_FAVORITE
                    };
            Cursor cursor = context.getContentResolver().query(MovieEntry.CONTENT_URI,
                    mProjection, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
                    String url = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_IMAGE_URL));
                    String overview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
                    String vote_average = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE));
                    String release_date = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
                    String id = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
                    int favorite = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_FAVORITE));
                    Movie currentMovie = new Movie(title, url, overview, vote_average, release_date, id, favorite);
                    movies.add(currentMovie);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return movies;
        }


         private static boolean isFavorite(List<Movie> favorites, String movieId){
             boolean isFavorite = false;
             for(int i = 0; i < favorites.size(); i++) {
                 Movie movie = favorites.get(i);
                 String mMovieId = movie.getId();
                 if(mMovieId.equals(movieId)) {
                     isFavorite = true;
                     break;
                 }
             }
             return isFavorite;
         }

    }
