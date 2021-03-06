package com.angelatech.yeyelive1.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;

import com.android.vending.billing.util.Purchase;
import com.angelatech.yeyelive1.activity.function.GooglePay;
import com.angelatech.yeyelive1.db.BaseKey;
import com.angelatech.yeyelive1.model.CommonParseModel;
import com.angelatech.yeyelive1.model.RoomInfo;
import com.angelatech.yeyelive1.receiver.IServiceReceiver;
import com.angelatech.yeyelive1.receiver.NetworkReceiver;
import com.angelatech.yeyelive1.socket.WillProtocol;
import com.angelatech.yeyelive1.socket.room.RoomConnectManager;
import com.angelatech.yeyelive1.util.BroadCastHelper;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.framework.socket.model.SocketConfig;
import com.google.gson.reflect.TypeToken;
import com.will.common.log.DebugLogs;
import com.will.common.tool.network.NetWorkUtil;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 后台服务类
 * 注：里面的功能通过IServiceInterface来定义，IServiceInterfaceImpl实现
 * 这样作为了更好的混淆代码，以及以后独立成进程
 */
public class IService extends Service {
    private NetworkReceiver mNetworkReceiver;
    private IServiceReceiver mIServiceReceiver;
    private IServiceInterface mIServiceInterface;//service业务逻辑接口
    private Handler mRoomHandler = null;
    private RoomConnectManager roomConnectManager;
    private MyBinder myBinder = new MyBinder();
    private PowerManager.WakeLock wakeLock = null;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireWakeLock();
        {
            mIServiceInterface = new IServiceInterfaceImpl(IService.this);
            mIServiceReceiver = new IServiceReceiver(mIServiceInterface);
            mNetworkReceiver = new NetworkReceiver(new NetworkReceiver.NetWorkHandler() {
                @Override
                public void onActive(int networkType) {
                    mIServiceInterface.handleNetworkActivie(networkType);
                    if (mRoomHandler != null) {
                        mRoomHandler.obtainMessage(IServiceValues.NETWORK_SUCCESS, networkType).sendToTarget();
                    }
                }

                @Override
                public void onInactive() {
                    mIServiceInterface.handleNetworkInactive();
                    if (mRoomHandler != null) {
                        mRoomHandler.obtainMessage(IServiceValues.NETWORK_FAILD).sendToTarget();
                    }
                }
            });
        }

        //命令action
        List<String> iServerActions = new ArrayList<>();
        {
            iServerActions.add(IServiceValues.ACTION_CMD_WAY);
            iServerActions.add(IServiceValues.ACTION_CMD_TEST);

        }
        List<String> netWorkAction = new ArrayList<>();
        {
            netWorkAction.add(NetWorkUtil.ACTION_NETWORK);
        }
        BroadCastHelper.registerBroadCast(IService.this, iServerActions, mIServiceReceiver);
        BroadCastHelper.registerBroadCast(IService.this, netWorkAction, mNetworkReceiver);
    }

    public class MyBinder extends Binder {
        public IService getIService() {
            return IService.this;
        }
    }

    public void setRoomHander(Handler hander) {
        this.mRoomHandler = hander;
    }

    /**
     * 登录房间
     */
    public void startRoomConnection(SocketConfig socketconfig, int BarId, int UserId, String token) {
        roomConnectManager = new RoomConnectManager(mRoomHandler);
        RoomInfo roomInfo = new RoomInfo();
        roomInfo.barid = BarId;
        roomInfo.userid = UserId;
        roomInfo.token = token;
        String jsonString = JsonUtil.toJson(roomInfo);
        DebugLogs.e("----发送登陆房间包--->"+jsonString);
        byte[] bytes = WillProtocol.sendMessage(WillProtocol.ENTER_VOICEROOM_TYPE_VALUE, jsonString);
        roomConnectManager.performConnect(socketconfig, bytes);
    }

    /**
     * 公用拼包
     *
     * @param typeValue 包的操作码
     * @param jsonStr   发送的内容 json字符窜
     */
    public void sendMessage(int typeValue, String jsonStr) {
        byte[] bytes = WillProtocol.sendMessage(typeValue, jsonStr);
        roomConnectManager.sendMessage(bytes);
    }


    /**
     * 停掉bar代理
     */
    public void quitRoom() {
        roomConnectManager.stop();
    }

    /**
     *  订单支付成功请求服务器处理加币
     */
    public void addItem(Context context, Purchase purchase, final String userId, String token, final Handler uiHandler) {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                CommonParseModel<String> results = JsonUtil.fromJson(response, new TypeToken<CommonParseModel<String>>() {
                }.getType());
                if (results != null) {
                    if (HttpFunction.isSuc(results.code)) {
                        //更新金币显示
                        if (results.data != null) {
                            CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, results.data, userId);
                        }
                    } else {
                        onBusinessFaild(results.code);
                    }
                }
            }
        };
        GooglePay mGooglePay = new GooglePay(context);
        mGooglePay.addItem(userId, token, purchase, callback);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
        BroadCastHelper.unregisterBroadCast(IService.this, mNetworkReceiver);
        BroadCastHelper.unregisterBroadCast(IService.this, mIServiceReceiver);
    }

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) IService.this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    //释放设备电源锁
    private void releaseWakeLock() {
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
