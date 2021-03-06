package com.lubanjianye.biaoxuntong.sign;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyco.tablayout.SlidingTabLayout;
import com.igexin.sdk.PushManager;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.app.BiaoXunTong;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.database.DatabaseManager;
import com.lubanjianye.biaoxuntong.database.UserProfile;
import com.lubanjianye.biaoxuntong.eventbus.EventMessage;
import com.lubanjianye.biaoxuntong.api.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.util.dialog.PromptDialog;
import com.lubanjianye.biaoxuntong.util.sp.AppSharePreferenceMgr;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.shaohui.shareutil.LoginUtil;
import me.shaohui.shareutil.login.LoginListener;
import me.shaohui.shareutil.login.LoginPlatform;
import me.shaohui.shareutil.login.LoginResult;
import me.shaohui.shareutil.login.result.QQToken;
import me.shaohui.shareutil.login.result.QQUser;
import me.shaohui.shareutil.login.result.WxToken;
import me.shaohui.shareutil.login.result.WxUser;

/**
 * 项目名:   AppLunious
 * 包名:     com.lubanjianye.biaoxuntong.sign
 * 文件名:   SignInFragment
 * 创建者:   lunious
 * 创建时间: 2017/12/13  0:40
 * 描述:     TODO
 */

public class SignInFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout llIvBack = null;
    private AppCompatTextView mainBarName = null;
    private AppCompatTextView tvLoginForgetPwd = null;
    private AppCompatTextView btLoginRegister = null;
    private LinearLayout ivLoginWx = null;
    private LinearLayout ivLoginQq = null;
    private SlidingTabLayout resulttStlTab = null;
    private ViewPager resultVp = null;

    private LoginListener mLoginListener = null;
    private PromptDialog promptDialog = null;

    private long id = 0;
    private String mobile = "";
    private String nickName = "";
    private String token = "";
    private String comid = "";
    private String imageUrl = "";
    private String companyName = "";

    private String clientID = PushManager.getInstance().getClientid(BiaoXunTong.getApplicationContext());

    private final List<String> mList = new ArrayList<String>();
    private LoginMethodFragmentAdapter mAdapter;

    @Override
    public Object setLayout() {
        return R.layout.fragment_sign_in;
    }

    @Override
    public void initView() {

        llIvBack = getView().findViewById(R.id.ll_iv_back);
        mainBarName = getView().findViewById(R.id.main_bar_name);
        tvLoginForgetPwd = getView().findViewById(R.id.tv_login_forget_pwd);
        btLoginRegister = getView().findViewById(R.id.bt_login_register);
        ivLoginWx = getView().findViewById(R.id.ll_login_wx);
        ivLoginQq = getView().findViewById(R.id.ll_login_qq);
        resulttStlTab = getView().findViewById(R.id.login_stl_tab);
        resultVp = getView().findViewById(R.id.result_vp);

        llIvBack.setOnClickListener(this);
        tvLoginForgetPwd.setOnClickListener(this);
        btLoginRegister.setOnClickListener(this);
        ivLoginWx.setOnClickListener(this);
        ivLoginQq.setOnClickListener(this);


    }

    @Override
    public void initData() {
        llIvBack.setVisibility(View.VISIBLE);
        mainBarName.setText("");

        mList.add("密码登陆");
        mList.add("验证码登陆");

        mAdapter = new LoginMethodFragmentAdapter(mList, getFragmentManager());
        resultVp.setAdapter(mAdapter);
        resulttStlTab.setViewPager(resultVp);

        //创建对象
        promptDialog = new PromptDialog(getActivity());
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);


    }

    @Override
    public void initEvent() {
        mLoginListener = new LoginListener() {

            @Override
            public void loginSuccess(LoginResult result) {
                promptDialog.dismissImmediately();
                promptDialog.showLoading("正在登录...");

                // 处理result
                switch (result.getPlatform()) {
                    case LoginPlatform.QQ:
                        QQUser user = (QQUser) result.getUserInfo();
                        QQToken mToken = (QQToken) result.getToken();
                        nickName = user.getNickname();
                        String openid = mToken.getOpenid();
                        imageUrl = user.getqZoneHeadImageLarge();

                        OkGo.<String>post(BiaoXunTongApi.URL_QQLOGIN)
                                .params("Source", "3")
                                .params("qq", openid)
                                .params("clientId", clientID)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        final JSONObject profileJson = JSON.parseObject(response.body());
                                        final String status = profileJson.getString("status");
                                        final String message = profileJson.getString("message");

                                        if ("200".equals(status)) {
                                            promptDialog.dismissImmediately();
                                            final JSONObject userInfo = JSON.parseObject(response.body()).getJSONObject("data");
                                            id = userInfo.getLong("id");
                                            token = response.headers().get("token");
                                            mobile = userInfo.getString("mobile");
                                            comid = userInfo.getString("comid");

                                            Log.d("IUGASUIDGUISADUIGYS", id + "");

                                            if (!"0".equals(comid)) {

                                                OkGo.<String>post(BiaoXunTongApi.URL_GETCOMPANYNAME)
                                                        .params("userId", id)
                                                        .params("comId", comid)
                                                        .execute(new StringCallback() {
                                                            @Override
                                                            public void onSuccess(Response<String> response) {
                                                                final JSONObject profileCompany = JSON.parseObject(response.body());
                                                                final String status = profileCompany.getString("status");
                                                                final JSONObject data = profileCompany.getJSONObject("data");
                                                                if ("200".equals(status)) {
                                                                    companyName = data.getString("qy");
                                                                } else {
                                                                    companyName = null;
                                                                }
                                                                final UserProfile profile = new UserProfile(id, mobile, nickName, token, comid, imageUrl, companyName);
                                                                DatabaseManager.getInstance().getDao().insert(profile);
                                                                AppSharePreferenceMgr.put(getContext(), EventMessage.LOGIN_SUCCSS, true);
                                                                EventBus.getDefault().post(new EventMessage(EventMessage.LOGIN_SUCCSS));
                                                                getActivity().onBackPressed();
                                                                ToastUtil.shortBottonToast(getContext(), "登陆成功");
                                                            }
                                                        });


                                            } else {
                                                final UserProfile profile = new UserProfile(id, mobile, nickName, token, comid, imageUrl, companyName);
                                                DatabaseManager.getInstance().getDao().insert(profile);
                                                AppSharePreferenceMgr.put(getContext(), EventMessage.LOGIN_SUCCSS, true);
                                                EventBus.getDefault().post(new EventMessage(EventMessage.LOGIN_SUCCSS));
                                                getActivity().onBackPressed();
                                                ToastUtil.shortBottonToast(getContext(), "登陆成功");
                                            }


                                        } else {
                                            promptDialog.dismissImmediately();
                                            ToastUtil.shortToast(getContext(), message);
                                        }
                                    }
                                });
                        break;
                    case LoginPlatform.WX:
                        WxUser wxUser = (WxUser) result.getUserInfo();
                        WxToken wxToken = (WxToken) result.getToken();

                        nickName = wxUser.getNickname();
                        String Oppenid = wxUser.getOpenId();
                        imageUrl = wxUser.getHeadImageUrl();

                        OkGo.<String>post(BiaoXunTongApi.URL_WEIXINLOGIN)
                                .params("Source", 1)
                                .params("Oppenid", Oppenid)
                                .params("clientId", clientID)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        final JSONObject profileJson = JSON.parseObject(response.body());
                                        final String status = profileJson.getString("status");
                                        final String message = profileJson.getString("message");
                                        if ("200".equals(status)) {
                                            promptDialog.dismissImmediately();
                                            final JSONObject userInfo = JSON.parseObject(response.body()).getJSONObject("data");
                                            id = userInfo.getLong("id");
                                            token = response.headers().get("token");
                                            comid = userInfo.getString("comid");
                                            mobile = userInfo.getString("mobile");
                                            companyName = null;

                                            if (!"0".equals(comid)) {
                                                OkGo.<String>post(BiaoXunTongApi.URL_GETCOMPANYNAME)
                                                        .params("userId", id)
                                                        .params("comId", comid)
                                                        .execute(new StringCallback() {
                                                            @Override
                                                            public void onSuccess(Response<String> response) {
                                                                final JSONObject profileCompany = JSON.parseObject(response.body());
                                                                final String status = profileCompany.getString("status");
                                                                final JSONObject data = profileCompany.getJSONObject("data");

                                                                if ("200".equals(status)) {
                                                                    companyName = data.getString("qy");
                                                                } else {
                                                                    companyName = null;
                                                                }
                                                                final UserProfile profile = new UserProfile(id, mobile, nickName, token, comid, imageUrl, companyName);
                                                                DatabaseManager.getInstance().getDao().insert(profile);
                                                                AppSharePreferenceMgr.put(getContext(), EventMessage.LOGIN_SUCCSS, true);
                                                                EventBus.getDefault().post(new EventMessage(EventMessage.LOGIN_SUCCSS));
                                                                getActivity().onBackPressed();
                                                                ToastUtil.shortBottonToast(getContext(), "登陆成功");
                                                            }
                                                        });

                                            } else {
                                                final UserProfile profile = new UserProfile(id, mobile, nickName, token, comid, imageUrl, companyName);
                                                DatabaseManager.getInstance().getDao().insert(profile);
                                                AppSharePreferenceMgr.put(getContext(), EventMessage.LOGIN_SUCCSS, true);
                                                EventBus.getDefault().post(new EventMessage(EventMessage.LOGIN_SUCCSS));
                                                getActivity().onBackPressed();
                                                ToastUtil.shortBottonToast(getContext(), "登陆成功");

                                            }

                                        } else {
                                            promptDialog.dismissImmediately();
                                            ToastUtil.shortToast(getContext(), message);
                                        }
                                    }
                                });

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void loginFailure(Exception e) {
                promptDialog.dismissImmediately();
                ToastUtil.shortBottonToast(getContext(), "登录失败");

            }

            @Override
            public void loginCancel() {
                promptDialog.dismissImmediately();
                ToastUtil.shortBottonToast(getContext(), "登录取消");
            }
        };
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_iv_back:
                if (promptDialog != null) {
                    promptDialog.dismissImmediately();
                }
                getActivity().onBackPressed();
                break;
            case R.id.bt_login_register:
                //用户注册
                startActivity(new Intent(getActivity(), SignUpActivity.class));
                break;
            case R.id.tv_login_forget_pwd:
                //重置密码
                startActivity(new Intent(getActivity(), SignForgetPwdActivity.class));
                break;
            case R.id.ll_login_wx:
                //微信登陆
                promptDialog.showLoading("请稍后...");
                LoginUtil.login(getActivity(), LoginPlatform.WX, mLoginListener);
                break;
            case R.id.ll_login_qq:
                //QQ登陆
                promptDialog.showLoading("请稍后...");
                LoginUtil.login(getActivity(), LoginPlatform.QQ, mLoginListener);
                break;
            default:
                break;
        }

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (resulttStlTab != null) {
            resulttStlTab.setCurrentTab(0);
            resulttStlTab.setViewPager(resultVp);
            resulttStlTab.notifyDataSetChanged();
        }
    }
}
