package com.ludic.nearbysolution.dixitdemoapp;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by lucas on 19/09/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private static final String TAG = "ImageAdapter";

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);

            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            Log.d(TAG, "height: " + height + " width: " + width);
            imageView.setLayoutParams(new GridView.LayoutParams(parent.getWidth()/3 == 0 ? width/3 : parent.getWidth()/3, (height/2) - 20));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.carta1, R.drawable.carta2,
            R.drawable.carta3, R.drawable.carta4,
            R.drawable.carta5, R.drawable.carta6
    };
}
