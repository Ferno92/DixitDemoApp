package com.ludic.nearbysolution.dixitdemoapp;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ludic.nearbysolution.dixitdemoapp.interfaces.SlideListener;

/**
 * Created by lucas on 23/09/2017.
 */

public class PagerAdapterFragment extends Fragment implements View.OnLongClickListener, View.OnClickListener{
    public static final String TAG = "PagerAdapterFragment";
    private SlideListener mSlideListener;
    private int mCardId, mPosition;
    private Button mSendCardButton;

    public static PagerAdapterFragment newInstance(int id, int position, SlideListener listener){
        PagerAdapterFragment f = new PagerAdapterFragment();
        f.mCardId = id;
        f.mSlideListener = listener;
        f.mPosition = position;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.pager_adapter_fragment, container, false);
        ImageView cardImageView = (ImageView)rootView.findViewById(R.id.card_in_adapter);
        Drawable cardDrawable = getResources().getDrawable(mCardId);
        cardImageView.setImageDrawable(cardDrawable);
        mSendCardButton = (Button)rootView.findViewById(R.id.send_card);
        mSendCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideListener.sendCard(mPosition);
            }
        });

        return rootView;
    }


    @Override
    public boolean onLongClick(View v) {
        Log.w(TAG, "long click triggered");
        mSendCardButton.setVisibility(View.VISIBLE);

        return true;
    }

    @Override
    public void onClick(View v) {
        mSendCardButton.setVisibility(View.GONE);
    }
}
