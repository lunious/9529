package com.lubanjianye.biaoxuntong.ui.launcher;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.api.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.app.BiaoXunTong;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.base.MainActivity;
import com.lubanjianye.biaoxuntong.database.DatabaseManager;
import com.lubanjianye.biaoxuntong.database.UserProfile;
import com.lubanjianye.biaoxuntong.eventbus.EventMessage;
import com.lubanjianye.biaoxuntong.util.sp.AppSharePreferenceMgr;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.lubanjianye.biaoxuntong.app.BiaoXunTong.getApplicationContext;


/**
 * 项目名:   AppLunious
 * 包名:     com.lubanjianye.biaoxuntong.ui.launcher
 * 文件名:   LauncherFragment
 * 创建者:   lunious
 * 创建时间: 2017/12/9  15:59
 * 描述:     TODO
 */

public class LauncherFragment extends BaseFragment implements BDLocationListener, EasyPermissions.PermissionCallbacks {

    private long userId = 0;
    private String token = null;


    public LocationClient mLocationClient = null;

    private String locationArea = "";

    private String locationCity = "";


    @Override
    public Object setLayout() {
        return R.layout.fragment_launcher;
    }


    @Override
    public void initView() {


        //定位相关
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(this);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);


    }

    @Override
    public void initData() {
        locationTask();
    }

    @Override
    public void initEvent() {

    }


    private void checkToken() {

        //保存定位地区
        AppSharePreferenceMgr.put(getContext(), EventMessage.LOCA_AREA, locationArea);
        AppSharePreferenceMgr.put(getContext(), EventMessage.LOCA_CITY, locationCity);


        //如果登录，检查token是否有效
        if (AppSharePreferenceMgr.contains(getContext(), EventMessage.LOGIN_SUCCSS)) {
            //得到用个户userId
            List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();
            for (int i = 0; i < users.size(); i++) {
                userId = users.get(0).getId();
                token = users.get(0).getToken();
            }
            OkGo.<String>post(BiaoXunTongApi.URL_CHECKTOKEN)
                    .params("userId", userId)
                    .params("token", token)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            if ("200".equals(response.body()) || "400".equals(response.body())) {
                                //token有效

                                BiaoXunTong.getHandler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkIsShowScroll();
                                    }
                                }, 1000);

                            } else {
                                EventBus.getDefault().post(new EventMessage(EventMessage.TOKEN_FALSE));

                                //清除登录信息
                                DatabaseManager.getInstance().getDao().deleteAll();
                                AppSharePreferenceMgr.remove(getContext(), EventMessage.LOGIN_SUCCSS);
                                AppSharePreferenceMgr.put(getContext(), EventMessage.TOKEN_FALSE, true);
                                BiaoXunTong.getHandler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkIsShowScroll();
                                    }
                                }, 1000);
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            BiaoXunTong.getHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    checkIsShowScroll();
                                }
                            }, 1000);
                        }
                    });

        } else {
            BiaoXunTong.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkIsShowScroll();
                }
            }, 1000);
        }


    }


    //判断是否显示滑动启动页
    private void checkIsShowScroll() {

        if (!AppSharePreferenceMgr.contains(getContext(), "first_into_app")) {
            getSupportDelegate().start(new LauncherScrollFragment(), SINGLETASK);

            //进入引导页
            Intent intent = new Intent(getActivity(), LauncherScrollActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else {
            //进入主页
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }

        }


    }


    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

        String province = bdLocation.getProvince();
        String city = bdLocation.getCity();


        locationArea = province.substring(0, province.length() - 1);
        locationCity = city.substring(0, city.length() - 1);


        if (!TextUtils.isEmpty(locationArea)) {
            checkToken();
        } else {
            checkToken();
            ToastUtil.shortToast(getApplicationContext(), "定位失败，请检查定位服务是否开启");
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        checkToken();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }


    private static final int RC_LOCATION_PERM = 123;

    @AfterPermissionGranted(RC_LOCATION_PERM)
    public void locationTask() {
        if (EasyPermissions.hasPermissions(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Have permission, do the thing!
            //开始定位
            mLocationClient.start();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_location),
                    RC_LOCATION_PERM,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);

        }


    }
}
