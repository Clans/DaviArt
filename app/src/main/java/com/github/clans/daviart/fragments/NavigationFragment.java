package com.github.clans.daviart.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.daviart.BaseFragment;
import com.github.clans.daviart.PreferenceHelper;
import com.github.clans.daviart.R;
import com.github.clans.daviart.adapters.NavigationAdapter;
import com.github.clans.daviart.api.DeviantArtApi;
import com.github.clans.daviart.models.Category;
import com.github.clans.daviart.models.CategoryTree;
import com.github.clans.daviart.models.Credentials;
import com.github.clans.daviart.util.ObserverImpl;
import com.github.clans.daviart.util.Utils;
import com.google.gson.Gson;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class NavigationFragment extends BaseFragment {

    private final Gson gson = new Gson();

    private CompositeSubscription compositeSubscription;
    private RecyclerView recyclerView;
    private Credentials credentials;
    private NavigationAdapter adapter;
    private OnCategorySelectedListener onCategorySelectedListener;

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onCategorySelectedListener = (OnCategorySelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        compositeSubscription = new CompositeSubscription();

        recyclerView = (RecyclerView) view.findViewById(R.id.navigationView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new NavigationAdapter.CategoryAnimator());

        adapter = new NavigationAdapter(getActivity());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String credentialsStr = PreferenceHelper.getCredentials();
        if (TextUtils.isEmpty(credentialsStr)) return;

        credentials = gson.fromJson(credentialsStr, Credentials.class);
        loadData(null, "/");
    }

    private void loadData(final NavigationAdapter.ViewHolder holder, final String catPath) {
        Subscription subscription = DeviantArtApi.getInstance()
                .getCategoryTree(catPath, false, credentials.getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverImpl<CategoryTree>() {
                    @Override
                    public void onNext(CategoryTree categoryTree) {
                        super.onNext(categoryTree);
                        if (categoryTree != null) {
                            List<Category> categories = categoryTree.getCategories();
                            updateUi(holder, categories);
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }

    private void updateUi(NavigationAdapter.ViewHolder holder, List<Category> categories) {
        adapter.setItems(holder, categories);
        adapter.setOnArrowClickListener(new NavigationAdapter.OnArrowClickListener() {
            @Override
            public void onArrowClick(NavigationAdapter.ViewHolder holder, Category category) {
                loadData(holder, category.getCatpath());
            }
        });

        adapter.setOnItemClickListener(new NavigationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NavigationAdapter.ViewHolder holder, Category category) {
                loadData(holder, category.getCatpath());
                onCategorySelectedListener.onCategorySelected(category);
            }
        });
    }

    public void setNavigationViewInsets(int insetTop, int insetBottom) {
        recyclerView.setPadding(
                recyclerView.getPaddingLeft(),
                recyclerView.getPaddingTop() + insetTop,
                recyclerView.getPaddingRight(),
                recyclerView.getPaddingBottom() + insetBottom
        );
    }

    public void loadCategories() {
        loadData(null, "/");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }
}
