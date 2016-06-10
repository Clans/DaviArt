package com.github.clans.daviart;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class BaseActivity extends AppCompatActivity {

    protected void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected void showLoadingIndicator() {
        View loading = findViewById(R.id.loading_indicator);
        if (loading != null) {
            loading.setVisibility(View.VISIBLE);
        }

        View recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    protected void hideLoadingIndicator() {
        View loading = findViewById(R.id.loading_indicator);
        if (loading != null) {
            loading.setVisibility(View.GONE);
        }

        View recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
        }

        /*View empty = findViewById(R.id.empty);
        if (empty != null) {
            empty.setVisibility(View.GONE);
        }*/
    }
}
