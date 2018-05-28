package com.example.y.xhschedule.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.y.xhschedule.fragment.TodayCourseFragment;
import com.example.y.xhschedule.fragment.WeekCourseFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Fragment []fragments=new Fragment[]{new TodayCourseFragment(),new WeekCourseFragment()};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}
