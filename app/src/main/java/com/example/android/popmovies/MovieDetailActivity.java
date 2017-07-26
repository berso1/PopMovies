package com.example.android.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ImageView imageView = (ImageView)findViewById(R.id.image_view);
        TextView title_text_view = (TextView) findViewById(R.id.title);
        TextView overview_text_view = (TextView) findViewById(R.id.overview);
        TextView vote_average_text_view = (TextView) findViewById(R.id.vote_average);
        TextView release_date_text_view = (TextView) findViewById(R.id.release_date);


        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String url = intent.getStringExtra("url");
        String overview = intent.getStringExtra("overview");
        String voteAverage = intent.getStringExtra("voteAverage");
        String releaseDate = intent.getStringExtra("releaseDate");

        title_text_view.setText(title);
        overview_text_view.setText(overview);
        vote_average_text_view.setText(getString(R.string.vote_average,voteAverage));
        release_date_text_view.setText(getString(R.string.release_date,releaseDate));

        Picasso
                .with(this)
                .load(url)
                .fit()
                .into(imageView);
    }

}
