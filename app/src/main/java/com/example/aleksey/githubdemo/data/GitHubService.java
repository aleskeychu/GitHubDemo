package com.example.aleksey.githubdemo.data;

import com.example.aleksey.githubdemo.data.entities.Envelope;
import com.example.aleksey.githubdemo.data.entities.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by aleksey on 12.05.17.
 */

public interface GitHubService {

    @GET("/search/users?per_page=10")
    Call<Envelope> search(@Query("q") String query, @Query("page") int page_number);

    @GET("/users/{username}")
    Call<User> user(@Path("username") String username);

}
