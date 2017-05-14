package com.example.aleksey.githubdemo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.aleksey.githubdemo.R;
import com.example.aleksey.githubdemo.data.entities.SearchRecord;
import com.example.aleksey.githubdemo.util.RequestQueueSingleton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aleksey on 12.05.17.
 */

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.RecordHolder> {
    static public final String USERNAME_KEY = "search_fragment_user_login";
    static private final String TAG = SearchRecyclerAdapter.class.getSimpleName().toString();
    private List<SearchRecord> mRecords;
    private RequestQueue mRequestQueue;
    private RecyclerView mRecyclerView;
    private Fragment mFragment;
    private Bundle mBundle;
    private Context mContext;

    public SearchRecyclerAdapter(List<SearchRecord> records, Context context) {
        mContext = context;
        mRecords = records;
        mRequestQueue = RequestQueueSingleton.getInstance(context).getRequestQueue();
    }

    @Override
    public SearchRecyclerAdapter.RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "Created ViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_row, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                fragmentJump(view);
            }
        });
        RecordHolder holder = new RecordHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchRecyclerAdapter.RecordHolder holder, int position) {
        Log.d(TAG, "Binding holder at position: " + position);
        SearchRecord item = mRecords.get(position);
        holder.username.setText(item.getLogin());
        String url = item.getAvatarUrl();
        setImage(url, holder.avatar);
    }

    private void setImage(String url, final ImageView v) {
        if (TextUtils.isEmpty(url)) return;

        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                v.setImageBitmap(response);
            }
        }, 0, 0, null, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error - " + error.getMessage());
            }
        });
        mRequestQueue.add(imageRequest);
    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    private void fragmentJump(View view) {
        Log.d(TAG, "FragmentJump!");
        int position = mRecyclerView.getChildAdapterPosition(view);
        SearchRecord record = mRecords.get(position);
        mFragment = new UserFragment();
        mBundle = new Bundle();
        mBundle.putString(USERNAME_KEY, record.getLogin());
        mFragment.setArguments(mBundle);
        switchContent(R.id.fragment_container, mFragment);
    }

    public void switchContent(int id, Fragment fragment) {
        if (mContext == null)
            return;
        if (mContext instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) mContext;
            mainActivity.switchContent(id, fragment);
        }
    }

    public static class RecordHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar_image_view) ImageView avatar;
        @BindView(R.id.search_text_view) TextView username;
        SearchRecord mRecord;

        public RecordHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
