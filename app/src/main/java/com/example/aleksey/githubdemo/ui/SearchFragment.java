package com.example.aleksey.githubdemo.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aleksey.githubdemo.R;
import com.example.aleksey.githubdemo.data.GitHubService;
import com.example.aleksey.githubdemo.data.entities.Envelope;
import com.example.aleksey.githubdemo.data.entities.SearchRecord;
import com.example.aleksey.githubdemo.util.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by aleksey on 12.05.17.
 */

public class SearchFragment extends Fragment {
    static private final String TAG = SearchFragment.class.getSimpleName().toString();
    @BindView(R.id.search_users)
    AutoCompleteTextView mSearchField;
    @BindView(R.id.recyclerview_searchfragment)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    LinearLayoutManager mLinearLayoutManager;
    private SearchRecyclerAdapter mAdapter;
    private List<SearchRecord> mRecords;
    private SharedPreferences mSharedPreferences;
    private Set<String> queriesHistory;
    private ArrayAdapter<String> autoCompleteAdapter;
    private Integer pagesCount;
    private Boolean pagesFinished;
    private Boolean imageLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecords = new ArrayList<>();
        mSharedPreferences = getActivity().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);
        queriesHistory = new HashSet<>();
        setRetainInstance(true);
        pagesCount = 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        ButterKnife.bind(this, view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new SearchRecyclerAdapter(mRecords, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.hasFixedSize();
        setRecyclerViewScrollListener();
        //Setting listener to save queris for future autocompletion
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = mSearchField.getText().toString();
                    if (!query.equals("")) {
                        setAdapterFromSP(query);
                        autoCompleteAdapter.notifyDataSetChanged();
                        return true;
                    }
                }
                return false;
            }
        });
        autoCompleteAdapter = new ArrayAdapter<String>(getActivity(), R.layout.dropdown_list_item, new ArrayList<String>(queriesHistory));
        mSearchField.setThreshold(1);
        mSearchField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearchField.showDropDown();;
                return false;
            }
        });
        setAdapterFromSP(null);
        mSearchField.setAdapter(autoCompleteAdapter);
        checkRecyclerEmptiness();
        return view;
    }

    private void checkRecyclerEmptiness() {
        if (mRecords.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    //Saving queris as json array into SharedPreferences, simple and easy to implement
    private void setAdapterFromSP(String newQuery) {
        String queries = mSharedPreferences.getString("QUERIES", "");
        Log.d(TAG, "Got queries from sp: " + queries);
        JSONArray array;
        try {
            array = new JSONArray(queries);
        } catch (org.json.JSONException e) {
            array = new JSONArray();
            Log.e(TAG, "Error parsing sharedpreferences");
        }

        try {
            if (queriesHistory.size() == 0) {
                queriesHistory = new HashSet<>();
                for (int i = 0; i < array.length(); i++)
                    queriesHistory.add(array.getString(i));
            } else {
                if (newQuery != null)
                    queriesHistory.add(newQuery);
            }
            autoCompleteAdapter.clear();
            autoCompleteAdapter.addAll(queriesHistory);
        } catch (JSONException e) {
            Log.e(TAG, "Error while adding to queriesHistory");
        }
        if (newQuery != null) {
            array.put(newQuery);
            Log.d(TAG, "queriesHistory size = " + queriesHistory.size());
            queries = array.toString();
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("QUERIES", queries);
            editor.apply();
            View current = getActivity().getCurrentFocus();
            if (current != null)
                current.clearFocus();
            makeQuery(newQuery, true);
        }
    }


    private synchronized void makeQuery(String query, final Boolean isNew) {
        imageLoading = true;
        if (isNew) {
            pagesFinished = false;
            pagesCount = 1;
        }
        if (pagesFinished) {
            imageLoading = false;
            return;
        }
        GitHubService service = ApiHelper.getApiService();
        Call<Envelope> call = service.search(query, pagesCount++);
        call.enqueue(new Callback<Envelope>() {
            @Override
            public void onResponse(Call<Envelope> call, Response<Envelope> response) {
                if (!response.isSuccessful()) {
                    pagesFinished = true;
                    Log.d(TAG, "No success with first search request");
                    imageLoading = false;
                } else {
                    if (isNew) {
                        mRecords.clear();
                    }
                    mRecords.addAll(response.body().getItems());
                    mAdapter.notifyDataSetChanged();
                    checkRecyclerEmptiness();
                    Log.d(TAG, "Response returned with " + mRecords.size() + " users");
                    imageLoading = false;
                }

            }
            @Override
            public void onFailure(Call<Envelope> call, Throwable t) {
                mRecords.clear();
                checkRecyclerEmptiness();
                if (mRecords.isEmpty()) {
                    Toast.makeText(getActivity(), "Couldn't load data", Toast.LENGTH_SHORT);
                }
                emptyView.setText(getResources().getString(R.string.error_search));
                imageLoading = false;
            }
        });
    }

    private int getLastVisibleItemPosition() {
        return mLinearLayoutManager.findLastVisibleItemPosition();
    }

    private void setRecyclerViewScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
                if (totalItemCount == getLastVisibleItemPosition() + 1 )
                    makeQuery(mSearchField.getText().toString(), false);
            }
        });
    }
}
