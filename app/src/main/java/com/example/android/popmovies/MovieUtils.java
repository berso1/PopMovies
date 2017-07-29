package com.example.android.popmovies;

import android.text.TextUtils;
import android.util.Log;

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
         * Query the THE_MOVIE_DB  and return an {@link Movie} object to represent a single movie.
         */
        public static List<Movie> fetchMovieData(String requestUrl) {

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
            return extractFeatureFromJson(jsonResponse);
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
                    Log.v(LOG_TAG,""+urlConnection.getResponseCode());
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
        private static List<Movie> extractFeatureFromJson(String movieJSON) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(movieJSON)) {
                return null;
            }
            List<Movie> movies = new ArrayList<>();
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

                    movies.add(new Movie(title,url,overview,vote_average,release_date));
                }
                return movies;
            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtils....", "Problem parsing the earthquake JSON results", e);
            }
            return null;
        }

    }
