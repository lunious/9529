package com.lubanjianye.biaoxuntong.ui.browser;

import android.content.Intent;

import com.lubanjianye.biaoxuntong.base.BaseActivity;
import com.lubanjianye.biaoxuntong.base.BaseFragment;

/**
 * Created by 11645 on 2018/3/22.
 */

public class BrowserDetailActivity extends BaseActivity {
    private String mApi = "";
    private String mTitle = "";
    private String mEntity = "";
    private int mEntityid = -1;

    @Override
    public BaseFragment setRootFragment() {
        Intent intent = getIntent();
        if (intent != null) {
            mApi = intent.getStringExtra("api");
            mTitle = intent.getStringExtra("title");
            mEntity = intent.getStringExtra("entity");
            mEntityid = intent.getIntExtra("entityid", -1);
        }

        final BrowserDetailFragment fragment = BrowserDetailFragment.create(mApi, mTitle, mEntity, mEntityid);
        return fragment;
    }
}
