package com.lubanjianye.biaoxuntong.ui.message;

import android.widget.TextView;

import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.ui.main.result.ResultListFragment;

/**
 * Created by 11645 on 2018/3/21.
 */

public class MessageListFragment extends BaseFragment {


    private TextView tv_message = null;

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
        tv_message = getView().findViewById(R.id.tv_message);

    }

    @Override
    public void initData() {

        tv_message.setText("暂无新消息");

    }

    @Override
    public void initEvent() {

    }
}
