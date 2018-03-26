package com.lubanjianye.biaoxuntong.ui.main.index;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:   LBBXT
 * 包名:     com.lubanjianye.biaoxuntong.ui.main.index
 * 文件名:   IndexFragmentAdapter
 * 创建者:   lunious
 * 创建时间: 2017/12/16  9:37
 * 描述:     TODO
 */

public class IndexFragmentAdapter extends FragmentPagerAdapter {

    private List<String> titles = new ArrayList<>();
    private ArrayList<Fragment> mFragment = new ArrayList<>();



    public IndexFragmentAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;

        for (int i = 0; i < titles.size(); i++) {

            if ("行业资讯".equals(titles.get(i))) {
                mFragment.add(new IndexHyzxListFragment());
            } else {
                mFragment.add(IndexListFragment.getInstance(titles.get(i)));
            }
        }

    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Fragment getItem(int position) {


        return mFragment.get(position);


    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }


}

