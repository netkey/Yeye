package com.angelatech.yeyelive1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.Constant;
import com.angelatech.yeyelive1.R;
import com.angelatech.yeyelive1.TransactionValues;
import com.angelatech.yeyelive1.activity.base.WithBroadCastHeaderActivity;
import com.angelatech.yeyelive1.activity.function.ChatRoom;
import com.angelatech.yeyelive1.activity.function.FocusFans;
import com.angelatech.yeyelive1.adapter.CommonAdapter;
import com.angelatech.yeyelive1.adapter.ViewHolder;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.model.BasicUserInfoModel;
import com.angelatech.yeyelive1.model.CommonModel;
import com.angelatech.yeyelive1.model.CommonParseListModel;
import com.angelatech.yeyelive1.model.FocusModel;
import com.angelatech.yeyelive1.model.SearchItemModel;
import com.angelatech.yeyelive1.util.CacheDataManager;
import com.angelatech.yeyelive1.util.JsonUtil;
import com.angelatech.yeyelive1.util.StartActivityHelper;
import com.angelatech.yeyelive1.view.LoadingDialog;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.view.ToastUtils;
import com.will.view.library.SwipyRefreshLayout;
import com.will.view.library.SwipyRefreshLayoutDirection;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: shanli
 * Date: 2016/10/26
 * Time: 16:06
 * 关注/粉丝
 */
public class RelationActivity extends WithBroadCastHeaderActivity implements SwipyRefreshLayout.OnRefreshListener {
    private ListView list_view_focus;
    private CommonAdapter<FocusModel> adapter;
    private BasicUserInfoDBModel model;
    private List<FocusModel> data = new ArrayList<>();
    private int pageIndex = 1;
    private int pageSize = 20;
    private int type = FocusFans.TYPE_FANS;
    private long dateSort = 0;
    private boolean IS_REFRESH = false;  //是否需要刷新
    private FocusFans focusFans;
    private ChatRoom chatRoom;
    private final int MSG_ADAPTER_NOTIFY = 1;
    private final int MSG_SET_FOLLOW = 2;
    private final int MSG_NO_DATA = 3;
    private final int MSG_NO_DATA_MORE = 4;
    private SwipyRefreshLayout swipyRefreshLayout;
    private RelativeLayout noDataLayout;
    private String url = CommonUrlConfig.FriendHelist;
    private String fuserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        getLoginUser();
        initView();
        setView();
        loadData();
    }

    private void initView() {
        list_view_focus = (ListView) findViewById(R.id.list_view_focus);
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        noDataLayout = (RelativeLayout) findViewById(R.id.no_data_layout);

        focusFans = new FocusFans(this);
        chatRoom = new ChatRoom(this);
        adapter = new CommonAdapter<FocusModel>(RelationActivity.this, data, R.layout.item_focus) {
            @Override
            public void convert(ViewHolder helper, final FocusModel item, final int position) {
                helper.setImageUrl(R.id.user_head_photo, item.headurl);
                helper.setText(R.id.tv_name, item.nickname);
                if (item.sex.equals(Constant.SEX_MALE)) {
                    helper.setImageResource(R.id.iv_user_sex, R.drawable.icon_information_boy);
                } else {
                    helper.setImageResource(R.id.iv_user_sex, R.drawable.icon_information_girl);
                }
                if (item.isfollow.equals("0")) {
                    helper.setImageResource(R.id.iv_user_follow_state, R.drawable.btn_focus);
                } else {
                    helper.setImageResource(R.id.iv_user_follow_state, R.drawable.btn_focus_cancel);
                }
                if (item.isv.equals("1")) {
                    helper.showView(R.id.iv_vip);
                } else {
                    helper.hideView(R.id.iv_vip);
                }
                helper.setOnClick(R.id.iv_user_follow_state, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doFocus(data.get(position), position);
                    }
                });
            }
        };
    }

    private void setView() {
        list_view_focus.setAdapter(adapter);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        headerLayout.showTitle(getString(R.string.user_focus));
        headerLayout.showLeftBackButton();
        list_view_focus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FocusModel focusModel = data.get(position);
                BasicUserInfoModel userInfoModel = new BasicUserInfoModel();
                userInfoModel.Userid = focusModel.userid;
//                userInfoModel.isfollow = focusModel.isfollow;
//                userInfoModel.headurl = focusModel.headurl;
//                userInfoModel.nickname = focusModel.nickname;
//                userInfoModel.isv = focusModel.isv;
//                userInfoModel.sex = focusModel.sex;
                StartActivityHelper.jumpActivity(RelationActivity.this, FriendUserInfoActivity.class, userInfoModel);
            }
        });
    }

    /**
     * 获取登录用户
     */
    private void getLoginUser() {
        model = CacheDataManager.getInstance().loadUser();
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_ADAPTER_NOTIFY:
                LoadingDialog.cancelLoadingDialog();
                noDataLayout.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                break;
            case MSG_SET_FOLLOW:
                adapter.notifyDataSetChanged();
                ToastUtils.showToast(this, getString(R.string.success));
                break;
            case MSG_NO_DATA:
                LoadingDialog.cancelLoadingDialog();
                showNodataLayout();
                break;
            case MSG_NO_DATA_MORE:
                LoadingDialog.cancelLoadingDialog();
                ToastUtils.showToast(this, getString(R.string.no_data_more));
                break;
        }
    }

    @Override
    protected void doReceive(String action, Intent intent) {
        SearchItemModel searchItemModel = (SearchItemModel) intent.getSerializableExtra(TransactionValues.UI_2_UI_KEY_OBJECT);
        if (searchItemModel != null) {
            for (FocusModel d : data) {
                if (d.userid.equals(searchItemModel.userid)) {
                    d.isfollow = searchItemModel.isfollow;
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void loadData() {
        LoadingDialog.showLoadingDialog(this, null);
        HttpBusinessCallback httpCallback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                CommonParseListModel<FocusModel> result = JsonUtil.fromJson(response, new TypeToken<CommonParseListModel<FocusModel>>() {
                }.getType());
                if (result != null) {
                    if (HttpFunction.isSuc(result.code)) {
                        if (!result.data.isEmpty()) {
                            dateSort = result.time;
                            if (IS_REFRESH) {
                                data.clear();
                            }
                            data.addAll(result.data);
                            uiHandler.obtainMessage(MSG_ADAPTER_NOTIFY).sendToTarget();
                        } else {
                            if (!IS_REFRESH) {
                                uiHandler.obtainMessage(MSG_NO_DATA_MORE).sendToTarget();
                            }
                        }
                    } else {
                        onBusinessFaild(result.code);
                    }
                }
                if (data.isEmpty()) {
                    uiHandler.obtainMessage(MSG_NO_DATA).sendToTarget();
                }
            }
        };

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            fuserid = bundle.getString("fuserid");
            type = bundle.getInt("type");
            if (type == FocusFans.TYPE_FANS) {
                headerLayout.showTitle(getString(R.string.search_fans));
            } else if (type == FocusFans.TYPE_FOCUS) {
                headerLayout.showTitle(getString(R.string.follow));
            }
        }

        BasicUserInfoDBModel model = CacheDataManager.getInstance().loadUser();
        HashMap<String, String> map = new HashMap<>();
        map.put("token", model.token);
        map.put("userid", model.userid);
        map.put("type", String.valueOf(type));
        map.put("fuserid", fuserid);
        if (dateSort > 0) {
            map.put("datesort", String.valueOf(dateSort));
        }
        map.put("pageindex", String.valueOf(pageIndex));
        map.put("pagesize", String.valueOf(pageSize));
        focusFans.httpGet(url, map, httpCallback);
    }

    private void doFocus(FocusModel userModel, final int position) {
        HttpBusinessCallback callback = new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                //uiHandler.obtainMessage(MSG_ERROR).sendToTarget();
            }

            @Override
            public void onSuccess(String response) {
                CommonModel results = JsonUtil.fromJson(response, CommonModel.class);
                if (results != null) {
                    if (HttpFunction.isSuc(results.code)) {
                        if (data.get(position).isfollow.equals("1")) {
                            data.get(position).isfollow = "0";
                        } else {
                            data.get(position).isfollow = "1";
                        }
                        uiHandler.sendEmptyMessage(MSG_SET_FOLLOW);
                    } else {
                        onBusinessFaild(results.code);
                    }
                }
            }
        };
        chatRoom.UserFollow(CommonUrlConfig.UserFollow, model.token, model.userid,
                userModel.userid, Integer.valueOf(userModel.isfollow), callback);
    }

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    freshLoad();
                } else {
                    moreLoad();
                }
                swipyRefreshLayout.setRefreshing(false);
            }
        });
    }

    //加载更多
    private void moreLoad() {
        IS_REFRESH = false;
        pageIndex++;
        loadData();
    }

    //刷新
    private void freshLoad() {
        IS_REFRESH = true;
        pageIndex = 1;
        loadData();
    }

    private void showNodataLayout() {
        noDataLayout.setVisibility(View.VISIBLE);
        noDataLayout.findViewById(R.id.hint_textview1).setVisibility(View.VISIBLE);
        ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(getString(R.string.no_data_no_follow));
        noDataLayout.findViewById(R.id.hint_textview2).setVisibility(View.GONE);
    }
}
