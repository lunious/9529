package com.lubanjianye.biaoxuntong.ui.message;

import android.support.v7.widget.RecyclerView;

import com.classic.common.MultipleStatusView;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by 11645 on 2018/3/21.
 */

public class MessageListFragment extends BaseFragment {


    private RecyclerView messageRecycler = null;
    private SmartRefreshLayout messageRefresh = null;
    private MultipleStatusView loadingStatus = null;

    private String mTitle = null;


    public static MessageListFragment getInstance(String title) {
        MessageListFragment sf = new MessageListFragment();
        sf.mTitle = title;
        return sf;
    }


    @Override
    public Object setLayout() {
        return R.layout.fragment_list_message;
    }

    @Override
    public void initView() {
        messageRecycler = getView().findViewById(R.id.message_recycler);
        messageRefresh = getView().findViewById(R.id.message_refresh);
        loadingStatus = getView().findViewById(R.id.message_list_status_view);
    }

    @Override
    public void initData() {


    }

    @Override
    public void initEvent() {

    }
}
