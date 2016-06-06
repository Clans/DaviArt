package com.github.clans.daviart.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.LinearLayout;

import com.github.clans.daviart.BaseActivity;
import com.github.clans.daviart.R;
import com.github.clans.daviart.adapters.ImageGridAdapter;
import com.github.clans.daviart.api.DeviantArtApi;
import com.github.clans.daviart.models.Art;
import com.github.clans.daviart.models.Credentials;
import com.github.clans.daviart.models.NewestArts;
import com.github.clans.daviart.util.EndlessRecyclerViewScrollListener;
import com.github.clans.daviart.util.ObserverImpl;
import com.github.clans.daviart.util.SpacingItemDecoration;
import com.github.clans.daviart.util.Utils;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    private CompositeSubscription compositeSubscription;
    private RecyclerView recyclerView;
    private ImageGridAdapter adapter;
    private NewestArts newestArts;
    private Credentials credentials;
    private DrawerLayout drawerLayout;
    private AppBarLayout appBar;
    private int numColumns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        numColumns = getResources().getInteger(R.integer.num_columns);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        appBar = (AppBarLayout) findViewById(R.id.appbar);

        if (Utils.hasLollipop()) {
            drawerLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @SuppressLint("NewApi")
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    LinearLayout.MarginLayoutParams lpToolbar = (LinearLayout.MarginLayoutParams)
                            appBar.getLayoutParams();
                    lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
                    appBar.setLayoutParams(lpToolbar);

                    recyclerView.setPadding(
                            recyclerView.getPaddingLeft(),
                            recyclerView.getPaddingTop(),
                            recyclerView.getPaddingRight() + insets.getSystemWindowInsetRight(),
                            recyclerView.getPaddingBottom() + insets.getSystemWindowInsetBottom()
                    );



                    // clear this listener so insets aren't re-applied
                    drawerLayout.setOnApplyWindowInsetsListener(null);

                    return insets.consumeSystemWindowInsets();
                }
            });
        }

        setupRecyclerView();

        compositeSubscription = new CompositeSubscription();
        loadData();
    }

    private void setupRecyclerView() {
        int spacing = getResources().getDimensionPixelSize(R.dimen.gridItemSpacing);
        recyclerView.addItemDecoration(new SpacingItemDecoration(spacing, true));
        recyclerView.setHasFixedSize(true);
        adapter = new ImageGridAdapter(this, numColumns);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numColumns);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemColumnSpan(position);
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager, 8) {
            @Override
            public void onLoadMore() {
                Timber.d("onLoadMore()");
                Subscription subscription = getNewestArts(newestArts.getNextOffset())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ObserverImpl<NewestArts>() {
                            @Override
                            public void onNext(NewestArts newestArts) {
                                super.onNext(newestArts);
                                MainActivity.this.newestArts = newestArts;
                                updateUi();
                            }
                        });

                compositeSubscription.add(subscription);
            }
        });
    }

    private void loadData() {
        Subscription subscription = requestAccessToken()
                .flatMap(new Func1<Credentials, Observable<NewestArts>>() {
                    @Override
                    public Observable<NewestArts> call(Credentials credentials) {
                        Timber.d("Access token: %s", credentials.getAccessToken());
                        MainActivity.this.credentials = credentials;
                        return getNewestArts(0);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverImpl<NewestArts>() {
                    @Override
                    public void onNext(NewestArts newestArts) {
                        super.onNext(newestArts);
                        MainActivity.this.newestArts = newestArts;
                        updateUi();
                    }
                });

        compositeSubscription.add(subscription);
    }

    private rx.Observable<Credentials> requestAccessToken() {
        return DeviantArtApi.getInstance().getAccessToken();
    }

    private rx.Observable<NewestArts> getNewestArts(int offset) {
        return DeviantArtApi.getInstance().getNewestArts("/photography/nature/", credentials.getAccessToken(), offset);
    }

    private void updateUi() {
        List<Art> arts = newestArts.getArts();
        adapter.setHasMore(newestArts.hasMore());
        adapter.setItems(arts);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
