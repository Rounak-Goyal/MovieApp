package com.example.movieapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.movieapplication.BuildConfig;
import com.example.movieapplication.R;
import com.example.movieapplication.adapter.MovieAdapter;
import com.example.movieapplication.api.Client;
import com.example.movieapplication.api.Service;
import com.example.movieapplication.model.ResponseNowPlaying;
import com.example.movieapplication.model.Movie;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity extends AppCompatActivity {

    public Button btn_show_recommendations;
    public TextView tv_page;
    public ImageButton ib_next,ib_prev;
    private int page;
    private int total_pages;

    public RecyclerView rvPopularMovie;
    public RecyclerView.Adapter popularMovieAdapter;
    public RecyclerView.LayoutManager popularMovieLayoutManager;
    public List<Movie> popularMovieDataList;

    public RecyclerView rvNowPlaying;
    public RecyclerView.Adapter nowPlayingMovieAdapter;
    public RecyclerView.LayoutManager nowPlayingLayoutManager;
    public List<Movie> nowPlayingDataList;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        popularMovieDataList = new ArrayList<>();
        popularMovieAdapter = new MovieAdapter(popularMovieDataList, this);
        popularMovieLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPopularMovie = findViewById(R.id.recycler_PopularMovie);
        rvPopularMovie.setHasFixedSize(true);
        rvPopularMovie.setLayoutManager(popularMovieLayoutManager);
        rvPopularMovie.setAdapter(popularMovieAdapter);

        nowPlayingDataList = new ArrayList<>();
        nowPlayingMovieAdapter = new MovieAdapter(nowPlayingDataList, this);
        nowPlayingLayoutManager = new GridLayoutManager(this, 3);
        rvNowPlaying = findViewById(R.id.recycler_TopCurrentMovies);
        rvNowPlaying.setHasFixedSize(true);
        rvNowPlaying.setLayoutManager(nowPlayingLayoutManager);
        rvNowPlaying.setAdapter(nowPlayingMovieAdapter);

        tv_page = findViewById(R.id.tv_page);
        page = Integer.parseInt((String) tv_page.getText());
        ib_prev = findViewById(R.id.ib_prev);
        ib_next = findViewById(R.id.ib_next);
        btn_show_recommendations = findViewById(R.id.btn_show_recommendations);

        loadJSON();
        nowPlayingMovieAdapter.notifyDataSetChanged();

        btn_show_recommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RecommendActivity.class);
                startActivity(intent);
            }
        });

        ib_prev.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if(page<=1) page = 1;
                else page--;
                tv_page.setText(Integer.toString(page));
                loadJSON();
                nowPlayingMovieAdapter.notifyDataSetChanged();
            }
        });
        ib_next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if(page>=total_pages) page = total_pages;
                else page++;
                tv_page.setText(Integer.toString(page));
                loadJSON();
                nowPlayingMovieAdapter.notifyDataSetChanged();
            }
        });
    }

    public void loadJSON() {
        try {
            String API_KEY = BuildConfig.API_KEY;
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<ResponseNowPlaying> call_nowPlaying = apiService.getNowPlaying(API_KEY,page);
            call_nowPlaying.enqueue(new Callback<ResponseNowPlaying>() {
                @Override
                public void onResponse(@NonNull Call<ResponseNowPlaying> call, @NonNull Response<ResponseNowPlaying> response) {
                    List<Movie> movies = response.body().getResults();
                    page = response.body().getPage();
                    total_pages = response.body().getTotal_pages();

                    rvNowPlaying.setAdapter(new MovieAdapter(movies, getApplicationContext()));
                    rvNowPlaying.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseNowPlaying> call, @NonNull Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(getApplicationContext(),"Error in fetching results!!",Toast.LENGTH_SHORT).show();
                }
            });
            Call<ResponseNowPlaying> call_popular = apiService.getPopularMovie(API_KEY,2);
            call_popular.enqueue(new Callback<ResponseNowPlaying>() {
                @Override
                public void onResponse(@NonNull Call<ResponseNowPlaying> call, @NonNull Response<ResponseNowPlaying> response) {
                    assert response.body() != null;
                    List<Movie> movies = response.body().getResults();

                    rvPopularMovie.setAdapter(new MovieAdapter(movies, getApplicationContext()));
                    rvPopularMovie.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseNowPlaying> call, @NonNull Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(getApplicationContext(),"Error in fetching results!!",Toast.LENGTH_SHORT).show();
                }
            });





        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }




    }

}