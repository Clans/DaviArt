package com.github.clans.daviart.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.LinearLayout;

import com.github.clans.daviart.BaseActivity;
import com.github.clans.daviart.Intents;
import com.github.clans.daviart.PreferenceHelper;
import com.github.clans.daviart.R;
import com.github.clans.daviart.adapters.ImageGridAdapter;
import com.github.clans.daviart.api.DeviantArtApi;
import com.github.clans.daviart.fragments.NavigationFragment;
import com.github.clans.daviart.models.Art;
import com.github.clans.daviart.models.Category;
import com.github.clans.daviart.models.Credentials;
import com.github.clans.daviart.models.NewestArts;
import com.github.clans.daviart.util.EndlessRecyclerViewScrollListener;
import com.github.clans.daviart.util.ObserverImpl;
import com.github.clans.daviart.util.SpacingItemDecoration;
import com.github.clans.daviart.util.Utils;
import com.google.gson.Gson;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.Timestamped;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements NavigationFragment.OnCategorySelectedListener {

    private CompositeSubscription compositeSubscription;
    private RecyclerView recyclerView;
    private ImageGridAdapter adapter;
    private NewestArts newestArts;
    private Credentials credentials;
    private DrawerLayout drawerLayout;
    private AppBarLayout appBar;
    private int numColumns;
    private Gson gson = new Gson();
    private String categoryPath = "/";
    private View loading;
    private NavigationFragment navFragment;

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
        loading = findViewById(R.id.loading_indicator);

        navFragment = (NavigationFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation);

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

                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) loading.getLayoutParams();
                    lp.bottomMargin += insets.getSystemWindowInsetBottom();
                    loading.setLayoutParams(lp);

                    if (navFragment != null) {
                        navFragment.setNavigationViewInsets(insets.getSystemWindowInsetTop(),
                                insets.getSystemWindowInsetBottom());
                    }

                    // clear this listener so insets aren't re-applied
                    drawerLayout.setOnApplyWindowInsetsListener(null);

                    return insets.consumeSystemWindowInsets();
                }
            });
        }

        setupRecyclerView();

        compositeSubscription = new CompositeSubscription();
        loadData(false);
    }

    private void setupRecyclerView() {
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_item_spacing);
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
                // TODO: add token refresh functionality
                Subscription subscription = getNewestArts(newestArts.getNextOffset())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ObserverImpl<NewestArts>() {
                            @Override
                            public void onNext(NewestArts newestArts) {
                                super.onNext(newestArts);
                                MainActivity.this.newestArts = newestArts;
                                updateUi(false);
                            }
                        });

                compositeSubscription.add(subscription);
            }
        });
    }

    private void loadData(final boolean resetAdapter) {
        Subscription subscription = Observable.just(PreferenceHelper.getCredentials())
                .timestamp()
                .map(new Func1<Timestamped<String>, Credentials>() {
                    @Override
                    public Credentials call(Timestamped<String> stringTimestamped) {
                        long millis = stringTimestamped.getTimestampMillis();
                        String s = stringTimestamped.getValue();
                        if (TextUtils.isEmpty(s)) return null;

                        Credentials credentials = gson.fromJson(s, Credentials.class);
                        return checkTokenNotExpired(credentials, millis) ? credentials : null;
                    }
                })
                .flatMap(new Func1<Credentials, Observable<NewestArts>>() {
                    @Override
                    public Observable<NewestArts> call(Credentials credentials) {
                        if (credentials != null) {
                            MainActivity.this.credentials = credentials;
                            return getNewestArts(0);
                        }
                        return getAccessTokenWithNewestArts();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverImpl<NewestArts>() {
                    @Override
                    public void onNext(NewestArts newestArts) {
                        super.onNext(newestArts);
                        MainActivity.this.newestArts = newestArts;
                        updateUi(resetAdapter);
                    }
                });

        compositeSubscription.add(subscription);
    }

    private rx.Observable<NewestArts> getAccessTokenWithNewestArts() {
        return DeviantArtApi.getInstance().getAccessToken()
                .timestamp()
                .flatMap(new Func1<Timestamped<Credentials>, Observable<NewestArts>>() {
                    @Override
                    public Observable<NewestArts> call(Timestamped<Credentials> credentialsTimestamped) {
                        long millis = credentialsTimestamped.getTimestampMillis();
                        Credentials credentials = credentialsTimestamped.getValue();
                        credentials.setTimestamp(millis);

                        String credentialsStr = gson.toJson(credentials);
                        PreferenceHelper.setCredentials(credentialsStr);
                        MainActivity.this.credentials = credentials;

                        if (navFragment != null) {
                            navFragment.loadCategories();
                        }
                        return getNewestArts(0);
                    }
                });
    }

    private rx.Observable<NewestArts> getNewestArts(int offset) {
        return DeviantArtApi.getInstance().getNewestArts(categoryPath, credentials.getAccessToken(),
                20, offset, false);
    }

    private boolean checkTokenNotExpired(Credentials credentials, long currentMillis) {
        long credentialsTimestamp = credentials.getTimestamp();
        int expiresIn = credentials.getExpiresIn();
        return (currentMillis - credentialsTimestamp) / 1000 <= expiresIn;
    }

    private void updateUi(boolean resetAdapter) {
        List<Art> arts = newestArts.getArts();
        if (resetAdapter) {
            recyclerView.setAdapter(adapter);
            adapter.clear();
        }
        adapter.setHasMore(newestArts.hasMore());
        adapter.setItems(arts);
        hideLoadingIndicator();
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

    @Override
    public void onCategorySelected(Category category) {
        appBar.setExpanded(true, true);
        showLoadingIndicator();
        categoryPath = category.getCatpath();
        loadData(true);
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}
