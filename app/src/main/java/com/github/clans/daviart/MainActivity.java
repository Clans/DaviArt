package com.github.clans.daviart;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.clans.daviart.adapters.ImageGridAdapter;
import com.github.clans.daviart.api.DeviantArtApi;
import com.github.clans.daviart.models.Art;
import com.github.clans.daviart.models.Credentials;
import com.github.clans.daviart.models.NewestArts;
import com.github.clans.daviart.util.SpacingItemDecoration;

import java.util.List;

import rx.Observable;
import rx.Observer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        int spacing = getResources().getDimensionPixelSize(R.dimen.gridItemSpacing);
        recyclerView.addItemDecoration(new SpacingItemDecoration(spacing, true));
        adapter = new ImageGridAdapter(this);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case ImageGridAdapter.TYPE_PROGRESS:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);

        compositeSubscription = new CompositeSubscription();
        requestAccessToken();
    }

    private void requestAccessToken() {
        final DeviantArtApi api = DeviantArtApi.getInstance();
        Subscription subscription = api.getAccessToken()
                .flatMap(new Func1<Credentials, Observable<NewestArts>>() {
                    @Override
                    public Observable<NewestArts> call(Credentials credentials) {
                        Timber.d("Access token: %s", credentials.getAccessToken());
                        return api.getNewestArts("/photography/nature/", credentials.getAccessToken());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewestArts>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("Access token received");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error requesting access token");
                    }

                    @Override
                    public void onNext(NewestArts newestArts) {
                        MainActivity.this.newestArts = newestArts;
                        updateUi();
                    }
                });

        compositeSubscription.add(subscription);
    }

    private void updateUi() {
        List<Art> arts = newestArts.getArts();
        adapter.setItems(arts);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }
}
