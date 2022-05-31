package com.example.movieapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapplication.R;
import com.example.movieapplication.adapter.MovieAdapter;
import com.example.movieapplication.api.Client;
import com.example.movieapplication.api.Service;
import com.example.movieapplication.model.ResponseNowPlaying;
import com.example.movieapplication.model.Movie;

import java.util.ArrayList;
import java.util.List;
import static com.example.movieapplication.api.Service.TMDb_API_KEY;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    public TextView tv_page;
    public ImageButton ib_next,ib_prev;
    private int page;
    private int total_pages;

    public RecyclerView rvNowPlaying;
    public RecyclerView.Adapter nowPlayingMovieAdapter;
    public RecyclerView.LayoutManager nowPlayingLayoutManager;
    public List<Movie> nowPlayingDataList;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nowPlayingDataList = new ArrayList<>();
        nowPlayingMovieAdapter = new MovieAdapter(nowPlayingDataList, this);
        nowPlayingLayoutManager = new GridLayoutManager(this, 2);


        tv_page = findViewById(R.id.tv_page);
        page = Integer.parseInt((String) tv_page.getText());
        ib_prev = findViewById(R.id.ib_prev);
        ib_next = findViewById(R.id.ib_next);

        rvNowPlaying = findViewById(R.id.recycler_TopCurrentMovies);
        rvNowPlaying.setHasFixedSize(true);
        rvNowPlaying.setLayoutManager(nowPlayingLayoutManager);
        rvNowPlaying.setItemAnimator(new DefaultItemAnimator());
        rvNowPlaying.setAdapter(nowPlayingMovieAdapter);

        loadJSON();
        nowPlayingMovieAdapter.notifyDataSetChanged();

        ib_prev.setOnClickListener(new View.OnClickListener() {
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
            if(TMDb_API_KEY.isEmpty()){
                Toast.makeText(getApplicationContext(),"Invalid Api key",Toast.LENGTH_SHORT).show();
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<ResponseNowPlaying> call = apiService.getNowPlaying(TMDb_API_KEY,page);
            call.enqueue(new Callback<ResponseNowPlaying>() {
                @Override
                public void onResponse(Call<ResponseNowPlaying> call, Response<ResponseNowPlaying> response) {
                    List<Movie> movies = response.body().getResults();
                    page = response.body().getPage();
                    total_pages = response.body().getTotal_pages();

                    rvNowPlaying.setAdapter(new MovieAdapter(movies, getApplicationContext()));
                    rvNowPlaying.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<ResponseNowPlaying> call, Throwable t) {
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