package com.adostudio.adohi.adventour;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

public class ParallaxAdapter extends FragmentStatePagerAdapter {

    private static final String LOGTAG = "ParallaxAdapter";

    private ArrayList<ParallaxFragment> parallaxFragments;
    private ViewPager parallaxPager;

    public ParallaxAdapter(FragmentManager fm) {
        super(fm);

        parallaxFragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i) {
        return parallaxFragments.get(i);
    }

    @Override
    public int getCount() {
        return parallaxFragments.size();
    }

    public void add(ParallaxFragment parallaxFragment) {
        parallaxFragment.setAdapter(this);
        parallaxFragments.add(parallaxFragment);
        notifyDataSetChanged();
    }

    public void remove(int i) {
        parallaxFragments.remove(i);
        notifyDataSetChanged();
    }

    public void remove(ParallaxFragment parallaxFragment) {
        parallaxFragments.remove(parallaxFragment);

        int pos = parallaxPager.getCurrentItem();
        notifyDataSetChanged();

        parallaxPager.setAdapter(this);
        if (pos >= this.getCount()) {
            pos = this.getCount() - 1;
        }
        parallaxPager.setCurrentItem(pos, true);

    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setPager(ViewPager pager) {
        parallaxPager = pager;
    }
}
