package com.github.clans.daviart.adapters;

import android.content.Context;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.github.clans.daviart.R;
import com.github.clans.daviart.glide.PaletteBitmap;
import com.github.clans.daviart.glide.PaletteBitmapTranscoder;
import com.github.clans.daviart.models.Art;
import com.github.clans.daviart.models.Thumb;

import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.observables.MathObservable;

public class ImageGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_PROGRESS = 1;

    private final BitmapRequestBuilder<String, PaletteBitmap> glideRequest;

    private Context context;
    private List<Art> arts;

    public ImageGridAdapter(Context context) {
        this.context = context;
        this.glideRequest = Glide.with(context)
                .fromString()
                .asBitmap()
                .transcode(new PaletteBitmapTranscoder(context), PaletteBitmap.class)
                .animate(android.R.anim.fade_in)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    public static class ViewHolderItem extends RecyclerView.ViewHolder {

        private ImageView image;
        private LinearLayout captionBackground;
        private TextView title;
        private TextView category;

        public ViewHolderItem(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            captionBackground = (LinearLayout) itemView.findViewById(R.id.captionBackground);
            title = (TextView) itemView.findViewById(R.id.title);
            category = (TextView) itemView.findViewById(R.id.category);
        }
    }

    public static class ViewHolderProgress extends RecyclerView.ViewHolder {

        public ViewHolderProgress(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.grid_item_image, parent, false);
                return new ViewHolderItem(view);
            case TYPE_PROGRESS:
                view = LayoutInflater.from(context).inflate(R.layout.load_more, parent, false);
                return new ViewHolderProgress(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderItem) {
            Art art = arts.get(position);
            if (art != null) {
                final ViewHolderItem vhi = (ViewHolderItem) holder;
                List<Thumb> thumbs = art.getThumbs();
                Thumb thumb = thumbs.get(thumbs.size() - 1);

                glideRequest.load(thumb.getSrc()).into(new ImageViewTarget<PaletteBitmap>(vhi.image) {
                    @Override
                    protected void setResource(PaletteBitmap resource) {
                        super.view.setImageBitmap(resource.bitmap);
                    }

                    @Override
                    public void onResourceReady(PaletteBitmap resource, GlideAnimation<? super PaletteBitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        Palette palette = resource.palette;
                        List<Palette.Swatch> swatches = palette.getSwatches();

                        MathObservable.from(Observable.from(swatches))
                                .max(new Comparator<Palette.Swatch>() {
                                    @Override
                                    public int compare(Palette.Swatch lhs, Palette.Swatch rhs) {
                                        if (lhs.getPopulation() < rhs.getPopulation()) {
                                            return -1;
                                        } else if (lhs.getPopulation() > rhs.getPopulation()) {
                                            return 1;
                                        }
                                        return 0;
                                    }
                                })
                                .subscribe(new Action1<Palette.Swatch>() {
                                    @Override
                                    public void call(Palette.Swatch swatch) {
                                        int rgb = swatch.getRgb();
                                        vhi.title.setTextColor(swatch.getBodyTextColor());
                                        vhi.category.setTextColor(swatch.getBodyTextColor());
                                        vhi.captionBackground.setBackgroundColor(rgb);
                                    }
                                });
                    }
                });

                vhi.title.setText(art.getTitle());
                vhi.category.setText(art.getCategory());
            }
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ViewHolderItem) {
            Glide.clear(((ViewHolderItem) holder).image);
        }
    }

    @Override
    public int getItemCount() {
        return arts != null ? arts.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        }
        return TYPE_ITEM;
    }

    public void setItems(List<Art> arts) {
        this.arts = arts;
        notifyDataSetChanged();
    }
}
