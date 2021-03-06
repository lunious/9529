package com.lubanjianye.biaoxuntong.ui.message;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.bean.MessageListBean;
import java.util.List;

/**
 * 项目名:   AppLunious
 * 包名:     com.lubanjianye.biaoxuntong.ui.main.fragment.result
 * 文件名:   ResultListAdapter
 * 创建者:   lunious
 * 创建时间: 2017/12/12  0:33
 * 描述:     TODO
 */

public class MessageListAdapter extends BaseQuickAdapter<MessageListBean,BaseViewHolder> {

    public MessageListAdapter(int layoutResId, @Nullable List<MessageListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageListBean item) {
        helper.setText(R.id.tv_item_title, item.getEntityName());
//        helper.setText(R.id.tv_item_time, item.getSysTime().substring(0, 10));
        helper.setText(R.id.tv_item_time, item.getCreateTime());

    }
}
