package com.angelatech.yeyelive1.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.GlobalDef;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.Qiniupush.PLVideoTextureUtils;
import com.angelatech.yeyelive1.activity.base.BaseActivity;
import com.angelatech.yeyelive1.activity.function.ChatRoom;
import com.angelatech.yeyelive1.activity.function.MainEnter;
import com.angelatech.yeyelive1.activity.function.UserControl;
import com.angelatech.yeyelive1.application.App;
import com.angelatech.yeyelive1.db.BaseKey;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.fragment.LockChooseDialogFragment;
import com.angelatech.yeyelive1.fragment.TicketsDialogFragment;
import com.angelatech.yeyelive1.handler.CommonHandler;
import com.angelatech.yeyelive1.mediaplayer.SurfaceViewHolderCallback;
import com.angelatech.yeyelive1.mediaplayer.VideoPlayer;
import com.angelatech.yeyelive1.mediaplayer.util.PlayerUtil;
import com.angelatech.yeyelive1.model.BasicUserInfoModel;
import com.angelatech.yeyelive1.model.CommonParseModel;
import com.angelatech.yeyelive1.model.VideoModel;
import com.angelatech.yeyelive1.model.WebTransportModel;
import com.angelatech.yeyelive1.thirdShare.FbShare;
import com.angelatech.yeyelive1.thirdShare.ShareListener;
import com.angelatech.yeyelive1.thirdShare.SinaShare;
import com.angelatech.yeyelive1.thirdShare.ThirdShareDialog;
import com.angelatech.yeyelive1.thirdShare.WxShare;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.ErrorHelper;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.ScreenUtils;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.angelatech.yeyelive1.util.VerificationUtil;
import com.angelatech.yeyelive1.view.CommDialog;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.pili.pldroid.player.PLMediaPlayer;
import com.will.common.log.DebugLogs;
import com.will.common.string.security.Md5;
import com.will.view.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoDrawee;
import com.xj.frescolib.View.FrescoRoundView;

import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.util.Cocos2dxGift;
import org.cocos2dx.lib.util.Cocos2dxView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by:      sandy
 * Version          ${version}
 * Date:            16/6/15
 * Description(描述):
 * Modification  History(历史修改): 播放器页面
 * Date              Author          Version
 * ---------------------------------------------------------
 */
public class PlayActivity extends BaseActivity implements PLVideoTextureUtils.PLVideoCallBack {
    private final int MSG_SET_FLLOW = 211221;
    private final int MSG_REPORT_SUCCESS = 1200;
    private final int MSG_REPORT_ERROR = 1201;
    private final int MSG_HIDE_PLAYER_CTL = 1202;
    private final int MSG_INPUT_LIMIT = 100;
    private SurfaceView player_surfaceView;
    private Button player_replay_btn, btn_back;
    private LinearLayout player_ctl_layout, layout_onClick;
    private RelativeLayout ly_playfinish, top_view;
    private SeekBar player_seekBar;
    private TextView player_total_time, player_current_time, tv_report, txt_likenum, player_split_line, txt_barname, str_context;
    private ImageView btn_share, iv_vip, btn_Follow, player_play_btn, backBtn, btn_red, btn_room_exchange;
    private VideoPlayer mVideoPlayer;

    private String path;
    private int isFollow = 0;
    private VideoModel videoModel;
    private BasicUserInfoDBModel userModel;
    private FrescoRoundView img_head;
    private FrescoDrawee default_img;
    private Cocos2dxView cocos2dxView = new Cocos2dxView();
    private Cocos2dxGift cocos2dxGift = new Cocos2dxGift();
    private boolean boolReport = false; //是否举报
    private volatile int time = 5000;
    private MainEnter mainEnter;
    private TextView vodioTime;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (time == 5000) {
                //去隐藏
                uiHandler.obtainMessage(MSG_HIDE_PLAYER_CTL).sendToTarget();
            }
            time = 5000;
            uiHandler.postDelayed(this, 5000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //取消状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
        setView();
        uiHandler.postDelayed(runnable, 5000);
        initCocos2dx();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        scaleVideo(getResources().getConfiguration().orientation);
    }

    //初始化cocos
    private void initCocos2dx() {
        //coco2动画SurfaceView
        cocos2dxView.onCreate(PlayActivity.this, 0);
        Cocos2dxGLSurfaceView.ScaleInfo scaleInfo = new Cocos2dxGLSurfaceView.ScaleInfo();
        scaleInfo.nomal = true;
        scaleInfo.width = ScreenUtils.getScreenWidth(PlayActivity.this);
        scaleInfo.height = ScreenUtils.getScreenHeight(PlayActivity.this);
        cocos2dxView.setScaleInfo(scaleInfo);
        FrameLayout giftLayout = cocos2dxView.getFrameLayout();
        ((LinearLayout) findViewById(R.id.gift_layout)).addView(giftLayout);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        time = 0;
        if (mVideoPlayer != null && mVideoPlayer.getStatus() == VideoPlayer.STATUS_COMPLETE) {
            player_seekBar.setVisibility(View.GONE);
            player_ctl_layout.setVisibility(View.GONE);
        } else {
            player_seekBar.setVisibility(View.VISIBLE);
            player_ctl_layout.setVisibility(View.VISIBLE);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cocos2dxGift != null) {
            cocos2dxGift.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cocos2dxView != null) {
            cocos2dxView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cocos2dxView != null) {
            cocos2dxView.onResume();
        }
    }

    private void initView() {
        mainEnter = new MainEnter(getApplication());
        default_img = (FrescoDrawee) findViewById(R.id.default_img);
        player_seekBar = (SeekBar) findViewById(R.id.player_seekBar);
        player_surfaceView = (SurfaceView) findViewById(R.id.player_surfaceView);
        player_play_btn = (ImageView) findViewById(R.id.player_play_btn);
        btn_red = (ImageView) findViewById(R.id.btn_red);
        btn_room_exchange = (ImageView) findViewById(R.id.btn_room_exchange);
        player_replay_btn = (Button) findViewById(R.id.player_replay_btn);
        top_view = (RelativeLayout) findViewById(R.id.top_view);
        img_head = (FrescoRoundView) findViewById(R.id.img_head);
        iv_vip = (ImageView) findViewById(R.id.iv_vip);
        btn_Follow = (ImageView) findViewById(R.id.btn_Follow);
        btn_share = (ImageView) findViewById(R.id.btn_share);
        btn_back = (Button) findViewById(R.id.btn_back);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        player_ctl_layout = (LinearLayout) findViewById(R.id.player_ctl_layout);
        layout_onClick = (LinearLayout) findViewById(R.id.layout_onClick);

        LoadingDialog.showLoadingDialog(PlayActivity.this, null);
        ly_playfinish = (RelativeLayout) findViewById(R.id.ly_playfinish);
        player_total_time = (TextView) findViewById(R.id.player_total_time);
        vodioTime = (TextView) findViewById(R.id.vodioTime);
        player_current_time = (TextView) findViewById(R.id.player_current_time);
        txt_barname = (TextView) findViewById(R.id.txt_barname);
        tv_report = (TextView) findViewById(R.id.tv_report);
        str_context = (TextView) findViewById(R.id.str_context);
        txt_likenum = (TextView) findViewById(R.id.txt_likenum);
        player_split_line = (TextView) findViewById(R.id.player_split_line);
        default_img.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statusBarHeight = ScreenUtils.getStatusHeight(PlayActivity.this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(15, statusBarHeight, 0, 0);
            top_view.setLayoutParams(layoutParams);
            backBtn.setPadding(15, statusBarHeight, 0, 0);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp1.setMargins(15, statusBarHeight, 0, 0);
            lp1.setMargins(ScreenUtils.dip2px(getApplicationContext(), 10), statusBarHeight + ScreenUtils.dip2px(getApplicationContext(), 52), 15, 0);
            tv_report.setLayoutParams(lp1);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(15, statusBarHeight, 0, 0);
            lp.setMargins(ScreenUtils.dip2px(getApplicationContext(), 148), statusBarHeight + ScreenUtils.dip2px(getApplicationContext(), 10), 0, 30);
            str_context.setLayoutParams(lp);
        }

    }

    private void setView() {
        userModel = CacheDataManager.getInstance().loadUser();
        player_play_btn.setOnClickListener(click);
        player_replay_btn.setOnClickListener(click);
        btn_Follow.setOnClickListener(click);
        btn_red.setOnClickListener(click);
        btn_share.setOnClickListener(click);
        btn_back.setOnClickListener(click);
        tv_report.setOnClickListener(click);
        backBtn.setOnClickListener(click);
        btn_room_exchange.setOnClickListener(click);
        layout_onClick.setOnClickListener(click);
        img_head.setOnClickListener(click);
        // 为进度条添加进度更改事件
        player_seekBar.setOnSeekBarChangeListener(change);


        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            videoModel = (VideoModel) getIntent().getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
            if (videoModel == null) {
                finish();
                return;
            }
            path = videoModel.playaddress;
            default_img.setImageURI(videoModel.barcoverurl);
            if (!videoModel.userid.equals(userModel.userid)) {
                UserIsFollow();
            }

            img_head.setImageURI(VerificationUtil.getImageUrl100(videoModel.headurl));
            txt_barname.setText(videoModel.nickname);
            txt_likenum.setText(videoModel.playnum);
            str_context.setText(videoModel.introduce);
            //0 无 1 v 2 金v 9官
            switch (videoModel.isv) {
                case "1":
                    iv_vip.setImageResource(R.drawable.icon_identity_vip_white);
                    iv_vip.setVisibility(View.VISIBLE);
                    break;
                case "2":
                    iv_vip.setImageResource(R.drawable.icon_identity_vip_gold);
                    iv_vip.setVisibility(View.VISIBLE);
                    break;
                case "9":
                    iv_vip.setImageResource(R.drawable.icon_identity_official);
                    iv_vip.setVisibility(View.VISIBLE);
                    break;
                default:
                    iv_vip.setVisibility(View.GONE);
                    break;
            }
        }
        Map<String, String> params = new HashMap<>();
        params.put("uid", videoModel.userid);
        params.put("pid", videoModel.videoid);
        String sign = Md5.md5(Md5.formatUrlMap(params, true, true) + CommonUrlConfig.Sign_key);
        DebugLogs.d("----------->"+Md5.formatUrlMap(params, true, true) + CommonUrlConfig.Sign_key);
        DebugLogs.d("----------->"+sign);
        mainEnter.videoTime(CommonUrlConfig.videoTime, videoModel.userid, videoModel.videoid, sign, new HttpBusinessCallback() {
            @Override
            public void onSuccess(final String response) {
                super.onSuccess(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DebugLogs.e("------>" + response);
                        CommonParseModel<String> commonParseModel = JsonUtil.fromJson(response, CommonParseModel.class);
                        if (commonParseModel != null && commonParseModel.code.equals("1000")) {
                            vodioTime.setText(commonParseModel.data);
                        }
                    }
                });
            }
        });
        CommonHandler<PlayActivity> mCommonHandler = new CommonHandler<>(this);
        mVideoPlayer = new VideoPlayer(player_surfaceView, mCommonHandler, path);
        player_surfaceView.setVisibility(View.VISIBLE);
        // 为SurfaceHolder添加回调
        player_surfaceView.getHolder().addCallback(new SurfaceViewHolderCallback(mVideoPlayer));
        // 4.0版本之下需要设置的属性
        // 设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到界面
        // player_surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if (!videoModel.userid.equals(userModel.userid) && videoModel.ticketprice != null && !videoModel.ticketprice.isEmpty() && Integer.parseInt(videoModel.ticketprice) > 0) {
            //门票房
            ChatRoom chatRoom = new ChatRoom(this);
            chatRoom.getVideoTickets(userModel.userid, userModel.token,
                    String.valueOf(videoModel.videoid), new HttpBusinessCallback() {
                        @Override
                        public void onSuccess(String response) {
                            DebugLogs.e("response" + response);
                            Map map = JsonUtil.fromJson(response, Map.class);
                            if (map != null) {
                                if (HttpFunction.isSuc(map.get("code").toString())) {
                                    String ticket = map.get("data").toString();
                                    if (!ticket.equals("0")) {//需要门票
                                        TicketsDialogFragment.Callback callback = new TicketsDialogFragment.Callback() {
                                            @Override
                                            public void onCancel() {
                                                finish();
                                            }

                                            @Override
                                            public void onEnter() {
                                                LoadingDialog.cancelLoadingDialog();
                                                mVideoPlayer.prepare();
                                            }
                                        };
                                        TicketsDialogFragment ticketsDialogFragment =
                                                new TicketsDialogFragment(PlayActivity.this, callback, ticket, Integer.parseInt(videoModel.videoid), 1);
                                        ticketsDialogFragment.show(getFragmentManager(), "");
                                    } else {
                                        LoadingDialog.cancelLoadingDialog();
                                        mVideoPlayer.prepare();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Map<String, ?> errorMap) {

                        }
                    });

        } else if (!videoModel.userid.equals(userModel.userid) && videoModel.password != null && videoModel.password.length() == 4) {
            //密码房
            LockChooseDialogFragment.Callback callback = new LockChooseDialogFragment.Callback() {
                @Override
                public void onCancel() {
                    //密码错误或者不想输入了
                    finish();
                }

                @Override
                public void onEnter(String password) {
                    if (videoModel.password.equals(password)) {
                        //密码正确
                        mVideoPlayer.prepare();
                    } else {
                        ToastUtils.showToast(PlayActivity.this, R.string.roompwd_err);
                    }
                }
            };

            LockChooseDialogFragment lockChooseDialogFragment = new LockChooseDialogFragment(PlayActivity.this, callback, App.roompwd, 1);
            lockChooseDialogFragment.show(this.getFragmentManager(), "");

        } else {
            //普通房间
            mVideoPlayer.prepare();
        }
    }

    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            time = 0;
            if (mVideoPlayer != null) {
                if (mVideoPlayer.getStatus() == VideoPlayer.STATUS_INIT) {
                    seekBar.setProgress(0);
                    return;
                }
                int progress = seekBar.getProgress();
//          // 设置当前播放的位置
//            currentPosition = progress;
//            mVideoPlayer.seekTo(progress);
                mVideoPlayer.play();
                mVideoPlayer.seekTo(progress);
            }
            player_play_btn.setImageResource(R.drawable.btn_playback_stop);
            default_img.setVisibility(View.GONE);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
    };

    private View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.player_play_btn:
                    default_img.setVisibility(View.GONE);
                    if (mVideoPlayer != null) {
                        if (mVideoPlayer.getStatus() == VideoPlayer.STATUS_PREPARE) {
                            mVideoPlayer.play();
                            ClickToWatch();
                            player_play_btn.setImageResource(R.drawable.btn_playback_stop);
                        } else {
                            mVideoPlayer.pause();
                        }
                    }
                    break;
                case R.id.player_replay_btn:
                    ly_playfinish.setVisibility(View.GONE);
                    player_play_btn.setImageResource(R.drawable.btn_playback_stop);
                    if (mVideoPlayer != null) {
                        mVideoPlayer.replay();
                    }
                    break;
                case R.id.btn_back:
                    finish();
                    break;
                case R.id.backBtn:
                    ClosePlay();
                    break;
                case R.id.btn_Follow:
                    UserFollow();
                    break;
                case R.id.btn_share:
                    //分享组件
                    ThirdShareDialog.Builder builder = new ThirdShareDialog.Builder(PlayActivity.this, getSupportFragmentManager(), null);
                    builder.setShareContent(videoModel.introduce, getString(R.string.shareDescription),
                            CommonUrlConfig.facebookURL + "?uid=" + videoModel.userid + "&videoid=" + videoModel.videoid,
                            videoModel.barcoverurl);
                    builder.create().show();
                    break;
                case R.id.tv_report:
                    if (!boolReport) {
                        CommDialog dialog = new CommDialog();
                        dialog.CommDialog(PlayActivity.this, getString(R.string.report_prompt), true, new CommDialog.Callback() {
                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onOK() {
                                UserControl userControl = new UserControl(PlayActivity.this);
                                userControl.report(userModel.userid, userModel.token, String.valueOf(UserControl.SOURCE_REPORT_VIDEO),
                                        videoModel.videoid, "", reportBack);
                            }
                        });

                    } else {
                        ToastUtils.showToast(PlayActivity.this, getString(R.string.report_repeat));
                    }
                    break;
                case R.id.btn_red:
                    showRedDialog();
                    break;
                case R.id.layout_onClick:
                    uiHandler.removeCallbacks(runnable);
                    uiHandler.postDelayed(runnable, 5000);
                    player_seekBar.setVisibility(View.VISIBLE);
                    player_ctl_layout.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_room_exchange:
                    WebTransportModel webTransportModel = new WebTransportModel();
                    webTransportModel.url = CommonUrlConfig.MallIndex + "?userid=" + userModel.userid +
                            "&token=" + userModel.token + "&hostid=" + videoModel.userid + "&time=" + System.currentTimeMillis();
                    webTransportModel.title = getString(R.string.gift_center);
                    if (!webTransportModel.url.isEmpty()) {
                        StartActivityHelper.jumpActivity(PlayActivity.this, WebActivity.class, webTransportModel);
                    }
                    break;
                case R.id.img_head:
                    if (videoModel != null) {
                        BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
                        userInfoModel.Userid = videoModel.userid;
                        StartActivityHelper.jumpActivity(PlayActivity.this, FriendUserInfoActivity.class, userInfoModel);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private EditText edit_context;
    private TextView edit_num;
    private TextView coins_low_tips;

    private void showRedDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(PlayActivity.this).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_red, null);
        dialog.setView(layout);
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.CENTER);
        window.setContentView(R.layout.dialog_red);
        userModel = CacheDataManager.getInstance().loadUser();
        final EditText edit_coins = (EditText) window.findViewById(R.id.edit_coins);
        edit_context = (EditText) window.findViewById(R.id.edit_context);
        edit_num = (TextView) window.findViewById(R.id.edit_num);
        TextView recharge_btn = (TextView) window.findViewById(R.id.recharge_btn);
        TextView coins_str = (TextView) window.findViewById(R.id.coins_str);
        edit_context.addTextChangedListener(textWatcher);
        Button btn_send = (Button) window.findViewById(R.id.btn_send);
        coins_str.setText(userModel.diamonds);
        coins_low_tips = (TextView) window.findViewById(R.id.coins_low_tips);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aomunt = edit_coins.getText().toString();
                String context = edit_context.getText().toString();
                if (context.isEmpty()) {
                    context = getString(R.string.red_tips_luck);
                }
                double userDiamonds = Double.valueOf(userModel.diamonds);
                if (!aomunt.isEmpty() && userDiamonds > Double.valueOf(aomunt.trim())) {
                    if (Double.valueOf(aomunt) < 10) {
                        coins_low_tips.setVisibility(View.VISIBLE);
                        coins_low_tips.setText(R.string.system_red_more_coins);
                    } else {
                        coins_low_tips.setVisibility(View.INVISIBLE);
                        sendRed(aomunt, context);
                        dialog.dismiss();
                    }
                } else {
                    coins_low_tips.setVisibility(View.VISIBLE);
                    coins_low_tips.setText(R.string.coins_low);
                }
            }
        });
        recharge_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityHelper.jumpActivityDefault(PlayActivity.this, RechargeActivity.class);
            }
        });
    }

    private void sendRed(String aomunt, String context) {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String data = jsonObject.getString("data");
                            if (code.equals("1000")) {
                                Cocos2dxGift.Cocos2dxGiftModel cocos2dxGiftModel = new Cocos2dxGift.Cocos2dxGiftModel();
                                cocos2dxGiftModel.aniName = "fx_jixiangwu";
                                cocos2dxGiftModel.exportJsonPath = "fx_jixiangwu/fx_jixiangwu.ExportJson";
                                cocos2dxGiftModel.x = ScreenUtils.getScreenWidth(PlayActivity.this) / 2 + 1;
                                cocos2dxGiftModel.y = ScreenUtils.getScreenHeight(PlayActivity.this);
                                cocos2dxGiftModel.speedScale = 0.005f;
                                cocos2dxGiftModel.scale = 0.8f;
                                cocos2dxGift.play(cocos2dxView, cocos2dxGiftModel);
                                CacheDataManager.getInstance().update(BaseKey.USER_DIAMOND, data, userModel.userid);
                            } else {
                                ToastUtils.showToast(PlayActivity.this, ErrorHelper.getErrorHint(PlayActivity.this, code));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        ChatRoom chatRoom = new ChatRoom(PlayActivity.this);
        chatRoom.PayReward(CommonUrlConfig.PayReward, videoModel.videoid, aomunt, context, userModel.userid, userModel.token, callback);
    }

    /*监听输入事件*/
    private TextWatcher textWatcher = new TextWatcher() {
        private int editStart;
        private int editEnd;
        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp = charSequence;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            editStart = edit_context.getSelectionStart();
            editEnd = edit_context.getSelectionEnd();
            if (temp.length() > 50) {
                editable.delete(editStart - 1, editEnd);
            }
            uiHandler.obtainMessage(MSG_INPUT_LIMIT, 0, 0, editable.toString()).sendToTarget();
        }
    };

    /**
     * 退出 关闭 播放
     */
    private void ClosePlay() {
        try {
            uiHandler.removeCallbacksAndMessages(null);
            if (mVideoPlayer != null) {
                mVideoPlayer.stop();
                mVideoPlayer.destroy();
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 举报 回调函数
     */
    private HttpBusinessCallback reportBack = new HttpBusinessCallback() {
        @Override
        public void onSuccess(String response) {
            uiHandler.sendEmptyMessage(MSG_REPORT_SUCCESS);
        }

        @Override
        public void onFailure(Map<String, ?> errorMap) {
            uiHandler.obtainMessage(MSG_REPORT_ERROR, errorMap.get("code")).sendToTarget();
        }
    };

    //接受回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 分享监听
     */
    public ShareListener listener = new ShareListener() {
        @Override
        public void callBackSuccess(int shareType) {
            switch (shareType) {
                case FbShare.SHARE_TYPE_FACEBOOK:
                    ToastUtils.showToast(PlayActivity.this, getString(R.string.success));
                    break;
                case WxShare.SHARE_TYPE_WX:
                    break;
                case SinaShare.SHARE_TYPE_SINA:
                    break;
            }
        }

        @Override
        public void callbackError(int shareType) {
        }

        @Override
        public void callbackCancel(int shareType) {
        }
    };

    //关注/取消关注
    public void UserFollow() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("code") == GlobalDef.SUCCESS_1000) {
                        //操作成功
                        if (isFollow == 0) {
                            isFollow = 1;
                        } else {
                            isFollow = 0;
                        }
                        uiHandler.obtainMessage(MSG_SET_FLLOW).sendToTarget();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ChatRoom chatRoom = new ChatRoom(PlayActivity.this);
        chatRoom.UserFollow(CommonUrlConfig.UserFollow, userModel.token, userModel.userid,
                videoModel.userid, isFollow, callback);
    }

    //检查是否关注
    private void UserIsFollow() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    //是否关注
                    isFollow = json.getJSONObject("data").getInt("isfollow");
                    uiHandler.obtainMessage(MSG_SET_FLLOW).sendToTarget();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ChatRoom chatRoom = new ChatRoom(this);
        chatRoom.UserIsFollow(CommonUrlConfig.UserIsFollow, userModel.token, userModel.userid, videoModel.userid, callback);
    }

    //录播计数
    private void ClickToWatch() {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                DebugLogs.e("response=========err==");
            }

            @Override
            public void onSuccess(String response) {
                DebugLogs.e("response===========" + response);
            }
        };
        ChatRoom chatRoom = new ChatRoom(this);
        chatRoom.ClickToWatch(CommonUrlConfig.ClickToWatch, userModel, videoModel.videoid, callback);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case VideoPlayer.MSG_PLAYER_START:
                player_play_btn.setImageResource(R.drawable.btn_playback_stop);
                break;
            case VideoPlayer.MSG_PLAYER_STOP:
                player_play_btn.setImageResource(R.drawable.btn_playback_play);
                break;
            case VideoPlayer.MSG_PLAYER_PAUSE:
                int arg1 = (Integer) msg.obj;
                if (arg1 == VideoPlayer.STATUS_PAUSE) {
                    player_play_btn.setImageResource(R.drawable.btn_playback_play);
                } else {
                    player_play_btn.setImageResource(R.drawable.btn_playback_stop);
                }
                break;
            case VideoPlayer.MSG_PLAYER_REPLAY:
                ToastUtils.showToast(this, getString(R.string.restart_play));
                player_play_btn.setImageResource(R.drawable.btn_playback_stop);
                break;
            case VideoPlayer.MSG_PLAYER_ONPREPARED:
                //加载完成
                LoadingDialog.cancelLoadingDialog();
                scaleVideo(getResources().getConfiguration().orientation);
                //setVideoSize();
                if (mVideoPlayer != null) {
                    int duration = mVideoPlayer.getDuration();
                    player_seekBar.setMax(duration);
                    player_total_time.setText(PlayerUtil.showTime(duration));
                    mVideoPlayer.play();
                }
                player_split_line.setVisibility(View.VISIBLE);
                player_current_time.setText(PlayerUtil.showTime(0));
                ClickToWatch();
                player_play_btn.setImageResource(R.drawable.btn_playback_stop);
                default_img.setVisibility(View.GONE);
                break;
            case VideoPlayer.MSG_PLAYER_ONPLAYING:
                if (mVideoPlayer != null) {
                    player_seekBar.setProgress(mVideoPlayer.getCurrentPosition());
                    player_current_time.setText(PlayerUtil.showTime(mVideoPlayer.getCurrentPosition()));
                }
                break;
            case VideoPlayer.MSG_PLAYER_ONCOMPLETIION:
                ly_playfinish.setVisibility(View.VISIBLE);
                player_play_btn.setEnabled(true);
                player_play_btn.setImageResource(R.drawable.btn_playback_play);
                break;
            case VideoPlayer.MSG_PLAYER_ONERROR:
                break;
            case MSG_SET_FLLOW:
                switch (isFollow) {
                    case -1:
                        btn_Follow.setVisibility(View.GONE);
                        break;
                    case 0:
                        btn_Follow.setVisibility(View.VISIBLE);
                        btn_Follow.setImageResource(R.drawable.btn_room_concern_n);
                        break;
                    case 1:
                        btn_Follow.setImageResource(R.drawable.btn_room_concern_s);
//                        Animation rotateAnimation = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.free_fall_down);
//                        btn_Follow.startAnimation(rotateAnimation);
                        btn_Follow.setVisibility(View.GONE);
                        break;
                }
                break;
            case MSG_REPORT_SUCCESS:
                boolReport = true;
                ToastUtils.showToast(this, getString(R.string.userinfo_dialog_report_suc));
                break;
            case MSG_REPORT_ERROR:
                ToastUtils.showToast(this, getString(R.string.userinfo_dialog_report_faild));
                break;
            case VideoPlayer.MSG_PLAYER_BUFFER_START:
                //ToastUtils.showToast(PlayActivity.this,"开始缓存");
                LoadingDialog.showLoadingDialog(PlayActivity.this, null);

                break;
            case VideoPlayer.MSG_PLAYER_BUFFER_END:
                LoadingDialog.cancelLoadingDialog();
                break;
            case MSG_HIDE_PLAYER_CTL:
                player_seekBar.setVisibility(View.GONE);
                player_ctl_layout.setVisibility(View.GONE);
                break;
            case MSG_INPUT_LIMIT:
                String inputStr = (String) msg.obj;
                if (inputStr != null && inputStr.length() > 0) {
                    String str = inputStr.length() + "/" + 50;
                    edit_num.setText(str);
                }
                break;
        }
    }

    private void scaleVideo(int orientation) {
        if (mVideoPlayer != null) {
            int[] videoSizeAry = mVideoPlayer.getVideoSize();
            if (videoSizeAry == null || videoSizeAry[0] == 0 || videoSizeAry[1] == 0) {
                return;
            }
            int[] calVideoSizeAry = PlayerUtil.scaleVideoSize(orientation, videoSizeAry[0], videoSizeAry[1], ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this));
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) player_surfaceView.getLayoutParams();
            if (lp == null) {
                lp = new RelativeLayout.LayoutParams(calVideoSizeAry[0], calVideoSizeAry[1]);
            }
            lp.width = calVideoSizeAry[0];
            lp.height = calVideoSizeAry[1];
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);

            player_surfaceView.setLayoutParams(lp);
        }
    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        player_play_btn.setImageResource(R.drawable.btn_playback_stop);
        default_img.setVisibility(View.GONE);
        LoadingDialog.cancelLoadingDialog();
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        default_img.setVisibility(View.VISIBLE);
        player_play_btn.setImageResource(R.drawable.btn_playback_play);
        ly_playfinish.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTimeOut() {

    }

    @Override
    public void setCurrentTime(String CurrentTime, String endTime) {
        player_total_time.setText(endTime);
        player_split_line.setVisibility(View.VISIBLE);
        player_current_time.setText(CurrentTime);
    }

    @Override
    public void sendReconnectMessage() {

    }
}
