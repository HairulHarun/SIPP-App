package com.chairul.sipp_app.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.chairul.sipp_app.fragment.Tab1Fragment;
import com.chairul.sipp_app.fragment.Tab2Fragment;

public class TabsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public TabsAdapter(FragmentManager fm, int NoofTabs){
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                Tab1Fragment home = new Tab1Fragment();
                return home;
            case 1:
                Tab2Fragment about = new Tab2Fragment();
                return about;
            default:
                return null;
        }
    }
}
