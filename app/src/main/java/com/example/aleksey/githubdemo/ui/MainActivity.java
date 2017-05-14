package com.example.aleksey.githubdemo.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.aleksey.githubdemo.R;

public class MainActivity extends AppCompatActivity {
    static private final String FRAGMENT_TAG = "search_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment fragment;
        if (savedInstanceState == null) {
            fragment = new SearchFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, fragment, FRAGMENT_TAG);
            ft.commit();
        } else {
            fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }
    }

    //Function for SearchFragment to call to launch another fragment
    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment, fragment.toString());
        ft.addToBackStack(null);
        ft.commit();
    }
}
