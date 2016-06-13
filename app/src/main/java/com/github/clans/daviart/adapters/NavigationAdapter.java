package com.github.clans.daviart.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.daviart.R;
import com.github.clans.daviart.models.Category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder> {

    private final ArrayList<Category> categories = new ArrayList<>();
    private final LinkedList<Category> expandedCategories = new LinkedList<>();
    private final Context context;
    private final int indention;

    private OnArrowClickListener onArrowClickListener;
    private OnItemClickListener onItemClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnArrowClickListener {
        void onArrowClick(ViewHolder holder, Category category);
    }

    public interface OnItemClickListener {
        void onItemClick(ViewHolder holder, Category category);
    }

    public NavigationAdapter(Context context) {
        this.context = context;
        this.indention = context.getResources()
                .getDimensionPixelSize(R.dimen.subcategory_indention);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View arrowLayout;
        private ImageView arrow;
        private TextView title;
        private ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);
            arrowLayout = itemView.findViewById(R.id.arrow_layout);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            title = (TextView) itemView.findViewById(R.id.title);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }

    @Override
    public NavigationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_navigation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NavigationAdapter.ViewHolder holder, final int position) {
        final Category category = categories.get(position);
        if (category != null) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            lp.leftMargin = indention * category.getIndention();
            holder.itemView.setLayoutParams(lp);

            holder.arrow.setVisibility(category.hasSubcategory() ? View.VISIBLE : View.INVISIBLE);
            if (category.isExpanded()) {
                holder.arrow.setRotation(90);
            } else {
                holder.arrow.setRotation(0);
            }
            holder.title.setText(category.getTitle());

            holder.arrowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onArrowClickListener != null) {
                        if (!category.isExpanded()) {
                            prepareForLoadingSubcategories(holder, category);
                            onArrowClickListener.onArrowClick(holder, category);
                        } else {
                            collapse(category);
                        }
                    }
                }
            });

            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        // TODO: set the selected state inside the Category object
                        notifyItemChanged(selectedPosition); // update previously selected item
                        selectedPosition = position;
                        prepareForLoadingSubcategories(holder, category);

                        onItemClickListener.onItemClick(holder, category);
                        notifyItemChanged(position); // update currently selected item
                    }
                }
            });
        }

        holder.itemView.setActivated(position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private void prepareForLoadingSubcategories(ViewHolder holder, Category category) {
        if (expandedCategories.size() > 0) {
            collapse(category);
        }

        toggleProgress(holder,  true);
        category.setExpanded(true);
        expandedCategories.addLast(category);
        holder.arrowLayout.setEnabled(false);
    }

    public void setItems(ViewHolder holder, List<Category> categories) {
        int position = holder == null ? RecyclerView.NO_POSITION : holder.getAdapterPosition();
        if (position >= 0) {
            Category category = this.categories.get(position);
            for (int i = 0; i < categories.size(); i++) {
                Category subCategory = categories.get(i);
                subCategory.setIndention(category.getIndention() + 1);
            }
            category.setSubCategories(categories);
        }

        int newPosition = position + 1;
        this.categories.addAll(newPosition, categories);
        notifyItemRangeInserted(newPosition, categories.size());

        if (holder != null) {
            toggleProgress(holder, false);
            holder.arrowLayout.setEnabled(true);
            notifyItemChanged(position, CategoryAnimator.EXPAND_CATEGORY);
        }
    }

    private void collapse(Category category) {
        Iterator<Category> iterator = expandedCategories.descendingIterator();
        while (iterator.hasNext()) {
            Category expandedCategory = iterator.next();
            if (expandedCategory.getIndention() >= category.getIndention()) {
                int position = categories.indexOf(expandedCategory);
                categories.removeAll(expandedCategory.getSubCategories());
                expandedCategory.setExpanded(false);
                iterator.remove();
                notifyItemChanged(position, CategoryAnimator.COLLAPSE_CATEGORY);
                notifyItemRangeRemoved(position + 1, expandedCategory.getSubCategories().size());
            }
        }
    }

    private void toggleProgress(NavigationAdapter.ViewHolder holder, boolean show) {
        if (show) {
            holder.arrow.setVisibility(View.GONE);
            holder.progress.setVisibility(View.VISIBLE);
        } else {
            holder.arrow.setVisibility(View.VISIBLE);
            holder.progress.setVisibility(View.GONE);
        }
    }

    public void setOnArrowClickListener(OnArrowClickListener onArrowClickListener) {
        this.onArrowClickListener = onArrowClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class CategoryAnimator extends DefaultItemAnimator {

        public static final int EXPAND_CATEGORY = 1;
        public static final int COLLAPSE_CATEGORY = 2;

        private static final long ANIM_DURATION = 400;

        @Override
        public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @NonNull
        @Override
        public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
            CategoryItemHolderInfo info = (CategoryItemHolderInfo)
                    super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
            info.doExpand = payloads.contains(EXPAND_CATEGORY);
            info.doCollapse = payloads.contains(COLLAPSE_CATEGORY);
            return info;
        }

        @Override
        public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
                                     @NonNull final RecyclerView.ViewHolder newHolder,
                                     @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {

            if (newHolder instanceof ViewHolder && preInfo instanceof CategoryItemHolderInfo) {
                final ViewHolder holder = (ViewHolder) newHolder;
                final CategoryItemHolderInfo info = (CategoryItemHolderInfo) preInfo;

                if (info.doExpand) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(holder.arrow, View.ROTATION, 0, 90);
                    animator.setDuration(ANIM_DURATION);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            holder.arrow.setRotation(90);
                            dispatchAnimationFinished(newHolder);
                        }
                    });
                    animator.start();
                } else if (info.doCollapse) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(holder.arrow, View.ROTATION, 90, 0);
                    animator.setDuration(ANIM_DURATION);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            holder.arrow.setRotation(0);
                            dispatchAnimationFinished(newHolder);
                        }
                    });
                    animator.start();
                }
            }

            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
        }

        @Override
        public ItemHolderInfo obtainHolderInfo() {
            return new CategoryItemHolderInfo();
        }

        static class CategoryItemHolderInfo extends ItemHolderInfo {
            boolean doExpand;
            boolean doCollapse;
        }
    }

}
