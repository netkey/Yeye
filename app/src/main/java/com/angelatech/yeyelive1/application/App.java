package com.angelatech.yeyelive1.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.angelatech.yeyelive1.activity.ChatRoomActivity;
import com.angelatech.yeyelive1.db.DBConfig;
import com.angelatech.yeyelive1.db.DatabaseHelper;
import com.angelatech.yeyelive1.model.ChatLineModel;
import com.angelatech.yeyelive1.model.GiftModel;
import com.angelatech.yeyelive1.model.ProductModel;
import com.angelatech.yeyelive1.model.VoucherModel;
import com.angelatech.yeyelive1.service.IService;
import com.angelatech.yeyelive1.util.SPreferencesTool;
import com.angelatech.yeyelive1.util.ScreenUtils;
import com.qiniu.pili.droid.streaming.StreamingEnv;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 初始化一些全局的控间，及全局变量
 * 例如初始化：
 * 1、activeandroid数据库
 * 2、有盟统计
 */
public class App extends Application {
    private static List<Activity> activityList = new ArrayList<>();
    private AppInterface mAppInterface = new AppInterfaceImpl();

    //常量区
    public static boolean isDebug = false;
    public static boolean isLogin = false;// 判断用户是否登录
    public static boolean isQiNiu = true; // 是否使用七牛服务器

    private static String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String FILEPATH_ROOT = SDCARD_ROOT + File.separator + AppConfig.FILEPATH_ROOT_NAME;
    public static final String FILEPATH_CACHE = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_CACHE_NAME;
    public static final String FILEPATH_VOICE = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_VOICE_NAME;
    public static final String FILEPATH_UPAPK = "yeye" + File.separator + AppConfig.FILEPATH_UPAPK_NAME;
    public static final String FILEPATH_CAMERA = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_CAMERA_NAME;
    public static final String FILEPATH_VOICE_RECORD = FILEPATH_VOICE + File.separator + AppConfig.FILEPATH_VOICE_RECORD_NAME;

    public static final String SERVICE_ACTION = AppConfig.SERVICE_ACTION;

    public static final ExecutorService pool = Executors.newFixedThreadPool(5);

    public static ChatRoomActivity chatRoomApplication = null;                       // 保持ChatRoom存在
    public static ArrayList<ChatLineModel> mChatlines = new ArrayList<>();          // 房间数据存储
    public static List<GiftModel> giftdatas = new ArrayList<>();                    // 礼物数据存储
    public static List<ProductModel> productModels = new ArrayList<>();
    public static List<VoucherModel> configOnOff = new ArrayList<>();
    public static boolean isLiveNotify = true; // 直播提醒开关
    public static boolean isofficialNotify = true; // 官方提醒开关
    public static boolean isredNotify = true; // 红包提醒开关
    public static boolean isfansNotify = true; // 直播提醒开关

    public static String topActivity = "";

    public static int screenWidth = 0;
    public static int screenHeight = 0;
    public static DatabaseHelper sDatabaseHelper;

    private static App instance = null;

    public static final String LIVE_WATCH = "WATCH"; // 观看者
    public static final String LIVE_HOST = "LIVE";   // 直播者
    public static final String LIVE_PREVIEW = "PREVIEW"; //预览

    public static String loginPhone = null;
    public static String price = "";            //门票房门票
    public static String roompwd = "";          //密码房密码
    public static DisplayMetrics screenDpx;
    public static ArrayList<HashMap<String, Object>> marqueeData = new ArrayList<>();

    //test
    @Override
    public void onCreate() {
        super.onCreate();
        List<String> dirs = new ArrayList<>();
        {
            dirs.add(FILEPATH_CACHE);
            dirs.add(FILEPATH_VOICE);
            dirs.add(FILEPATH_UPAPK);
            dirs.add(FILEPATH_CAMERA);
            dirs.add(FILEPATH_VOICE_RECORD);
        }
        instance = this;
        mAppInterface.initDir(dirs);
        mAppInterface.initDB(this, DBConfig.DB_NAME, 1);
        mAppInterface.initService(this, IService.class, SERVICE_ACTION);
        mAppInterface.initThirdPlugin(this);
        screenWidth = ScreenUtils.getScreenWidth(this);
        screenHeight = screenWidth * 16 / 9;
        screenDpx = getResources().getDisplayMetrics(); // 取屏幕分辨率
        StreamingEnv.init(this);
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(AppConfig.PACKAGE_NAME, PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                DebugLogs.d("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        isLiveNotify = SPreferencesTool.getInstance().getBooleanValue(this, SPreferencesTool.LIVENOTIFY);
        isofficialNotify = SPreferencesTool.getInstance().getBooleanValue(this, SPreferencesTool.OFFICIALNOTIFY);
        isredNotify = SPreferencesTool.getInstance().getBooleanValue(this, SPreferencesTool.COINSNOTIFY);
        isfansNotify = SPreferencesTool.getInstance().getBooleanValue(this, SPreferencesTool.FANSNOTIFY);
    }

    public synchronized static void register(Activity activity) {
        activityList.add(activity);
    }

    /**
     * Activity被销毁时，从Activities中移除
     */
    public synchronized static void unregister(Activity activity) {
        if (activityList != null && activityList.size() != 0) {
            activityList.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    // 单例模式获取唯一的MyApplication实例
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mAppInterface.destroy();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}