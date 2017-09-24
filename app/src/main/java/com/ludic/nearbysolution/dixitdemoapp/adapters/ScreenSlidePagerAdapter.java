package com.ludic.nearbysolution.dixitdemoapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ludic.nearbysolution.dixitdemoapp.PagerAdapterFragment;
import com.ludic.nearbysolution.dixitdemoapp.R;
import com.ludic.nearbysolution.dixitdemoapp.interfaces.SlideListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lucas on 24/09/2017.
 */


public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 6;
    ArrayList<Integer> mPageArrayList = new ArrayList();
    SlideListener mSlideListener;

    public ScreenSlidePagerAdapter(FragmentManager fm, SlideListener listener) {
        super(fm);

        mSlideListener = listener;
        //mock card list
        for (int i = 0; i < 6; i++) {
            switch (i) {
                case 0:
                    mPageArrayList.add(R.drawable.carta1);
                    break;

                case 1:
                    mPageArrayList.add(R.drawable.carta7);
                    break;

                case 2:
                    mPageArrayList.add(R.drawable.carta8);
                    break;

                case 3:
                    mPageArrayList.add(R.drawable.carta9);
                    break;

                case 4:
                    mPageArrayList.add(R.drawable.carta10);
                    break;

                case 5:
                    mPageArrayList.add(R.drawable.carta2);
                    break;

                default:
                    mPageArrayList.add(R.drawable.retro_card);
                    break;

            }
        }

    }

    @Override
    public Fragment getItem(int position) {
        return PagerAdapterFragment.newInstance(mPageArrayList.get(position), position, mSlideListener);
    }

    @Override
    public int getCount() {
        return mPageArrayList.size();
    }

    public int removeCard(int position){
        int cardId = mPageArrayList.get(position);
        mPageArrayList.remove(position);
        notifyDataSetChanged();
        return cardId;
    };
}
