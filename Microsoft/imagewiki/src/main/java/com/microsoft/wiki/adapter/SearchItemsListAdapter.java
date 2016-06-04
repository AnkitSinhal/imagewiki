package com.microsoft.wiki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.wiki.R;
import com.microsoft.wiki.models.PageDetails;
import com.microsoft.wiki.utils.ImageLoader;

import java.util.ArrayList;

public class SearchItemsListAdapter extends BaseAdapter {
    /**
     * Application context
     */
    private final Context mContext;
    /**
     * List of page details
     */
    private ArrayList<PageDetails> mPageDetailsList;
    /**
     * Layout inflater to inflate the list view item
     */
    private LayoutInflater mLayoutInflater;
    /**
     * ImageLoader instance to download and show image in list
     */
    private ImageLoader mImageLoader;

    public SearchItemsListAdapter(Context context, ArrayList<PageDetails> pageDetails) {
        this.mContext = context;
        this.mPageDetailsList = pageDetails;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mImageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return mPageDetailsList != null ? mPageDetailsList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mPageDetailsList != null ? mPageDetailsList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.search_row_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mPageDetailsList != null) {
            PageDetails pageDetails = mPageDetailsList.get(position);
            holder.title.setText(pageDetails.title);

            if (holder.imageView != null) {
                if (pageDetails.thumbnail != null && pageDetails.thumbnail.source != null) {
                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_anim);
                    holder.imageView.startAnimation(animation);
                    //load the images
                    mImageLoader.displayImage(pageDetails.thumbnail.source, holder.imageView);
                } else {
                    holder.imageView.setImageResource(R.mipmap.placeholder);
                }
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        ImageView imageView;
    }

    /**
     * Clear the cache directory
     */
    public void clearCache() {
        mImageLoader.clearCache();
    }
}
