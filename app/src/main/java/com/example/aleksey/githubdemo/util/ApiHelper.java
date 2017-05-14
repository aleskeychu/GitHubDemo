package com.example.aleksey.githubdemo.util;

import com.example.aleksey.githubdemo.data.GitHubService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by aleksey on 13.05.17.
 */

public class ApiHelper {

    public static final String BASE_URL = "https://api.github.com";

    public static GitHubService apiService;

    public static GitHubService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            apiService = retrofit.create(GitHubService.class);
        }
        return apiService;
    }
}
