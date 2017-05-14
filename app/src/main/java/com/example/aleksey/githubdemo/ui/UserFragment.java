package com.example.aleksey.githubdemo.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.aleksey.githubdemo.R;
import com.example.aleksey.githubdemo.data.entities.User;
import com.example.aleksey.githubdemo.util.ApiHelper;
import com.example.aleksey.githubdemo.util.RequestQueueSingleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by aleksey on 12.05.17.
 */

public class UserFragment extends Fragment {
    static private final String TAG = UserFragment.class.getSimpleName().toString();
    @BindView(R.id.user_avatar_image) ImageView avatarView;
    @BindView(R.id.user_login) TextView loginView;
    @BindView(R.id.user_email) TextView emailView;
    @BindView(R.id.user_name) TextView nameView;
    @BindView(R.id.user_company) TextView companyView;
    @BindView(R.id.user_location) TextView locationView;
    @BindView(R.id.user_public_gists) TextView gistsView;
    @BindView(R.id.user_public_repos) TextView reposView;
    @BindView(R.id.user_followers) TextView followersView;
    @BindView(R.id.user_following) TextView followingView;
    @BindView(R.id.user_bio) TextView bioView;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        String login = bundle.getString(SearchRecyclerAdapter.USERNAME_KEY);
        Call<User> call = ApiHelper.getApiService().user(login);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "No success with getting info about user.");
                }
                user = response.body();
                setUserInfo();
                Log.d(TAG, "User responsed retrived by the site");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Failure while retrieving user info.");
            }
        });
        return view;
    }

    private void setUserInfo() {
        String url = user.getAvatarUrl();
        Log.d(TAG, "User url: " + url);
        RequestQueue requestQueue = RequestQueueSingleton.getInstance(getActivity()).getRequestQueue();
        if (! TextUtils.isEmpty(url)) {
            Log.d(TAG, "Image request in UserFragment");
            ImageRequest imageRequest = new ImageRequest(url, new com.android.volley.Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    avatarView.setImageBitmap(response);
                    Log.d(TAG, "Bitmap in UserFragment set");
                }
            }, 0, 0, null, null, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error - " + error.getMessage());
                }
            });
            requestQueue.add(imageRequest);
        }
        loginView.setText("Username: " + user.getLogin());
        nameView.setText(user.getName() != null ? "Name: " + user.getName() : "");
        emailView.setText(user.getEmail() != null ? "Email: " + user.getEmail() : "");
        companyView.setText(user.getCompany() != null ? "Company: " + user.getCompany() : "");
        locationView.setText(user.getLocation() != null ? "Location: " + user.getLocation() : "");
        bioView.setText(user.getBio() != null ? "Bio:\n" + user.getBio() : "");
        Resources r = getResources();
        gistsView.setText(r.getString(R.string.gists, user.getPublicGists()));
        reposView.setText(r.getString(R.string.repos, user.getPublicRepos()));
        followersView.setText(r.getString(R.string.followers, user.getFollowers()));
        followingView.setText(r.getString(R.string.following, user.getFollowing()));
    }
}
