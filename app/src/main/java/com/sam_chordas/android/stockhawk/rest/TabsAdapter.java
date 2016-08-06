package com.sam_chordas.android.stockhawk.rest;

/**
 * Created by relferreira on 8/5/16.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sam_chordas.android.stockhawk.ui.StockDetailFragment;

import java.util.ArrayList;
import java.util.List;


public class TabsAdapter extends FragmentPagerAdapter {

    private List<StockDetailFragment> listFragments = new ArrayList<>();
    private List<String> listFragmentsTitle =  new ArrayList<>();

    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    public void add(StockDetailFragment frag, String title){
        this.listFragments.add(frag);
        this.listFragmentsTitle.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getCount() {
        return listFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return listFragmentsTitle.get(position);
    }
}
