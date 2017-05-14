package com.example.aleksey.githubdemo.data.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by aleksey on 12.05.17.
 */

public class Envelope {

    @SerializedName("total_count")
    @Expose
    private long totalCount;

    @SerializedName("incomplete_results")
    @Expose
    private boolean incompleteResults;

    private ArrayList<SearchRecord> items;

    public long getTotalCount() {
        return totalCount;
    }

    public boolean isIncompleteResults() {
        return incompleteResults;
    }

    public ArrayList<SearchRecord> getItems() {
        return items;
    }
}
