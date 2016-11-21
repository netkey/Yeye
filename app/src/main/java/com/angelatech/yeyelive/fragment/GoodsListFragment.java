package com.angelatech.yeyelive.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angelatech.yeyelive.CommonUrlConfig;
import com.angelatech.yeyelive.R;
import com.angelatech.yeyelive.activity.function.MainEnter;
import com.angelatech.yeyelive.adapter.CommonAdapter;
import com.angelatech.yeyelive.adapter.ViewHolder;
import com.angelatech.yeyelive.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive.model.CommonListResult;
import com.angelatech.yeyelive.model.RankModel;
import com.angelatech.yeyelive.util.CacheDataManager;
import com.angelatech.yeyelive.util.JsonUtil;
import com.angelatech.yeyelive.view.LoadingDialog;
import com.angelatech.yeyelive.web.HttpFunction;
import com.google.gson.reflect.TypeToken;
import com.will.web.handle.HttpBusinessCallback;
import com.xj.frescolib.View.FrescoDrawee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 　　┏┓　　　　┏┓
 * 　┏┛┻━━━━┛┻┓
 * 　┃　　　　　　　　┃
 * 　┃　　　━　　　　┃
 * 　┃　┳┛　┗┳　　┃
 * 　┃　　　　　　　　┃
 * 　┃　　　┻　　　　┃
 * 　┃　　　　　　　　┃
 * 　┗━━┓　　　┏━┛
 * 　　　　┃　　　┃　　　神兽保佑
 * 　　　　┃　　　┃　　　代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * <p>
 * <p>
 * 作者: Created by: xujian on Date: 2016/11/21.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.fragment
 */

public class GoodsListFragment extends BaseFragment {
    private View view;
    private BasicUserInfoDBModel userInfo;
    private FrescoDrawee commodity;
    private RelativeLayout details;
    private TextView title, commodity_price, numText, Coupons;
    private Button purchase;
    private ListView googs_list;
    private List<RankModel> rankModels = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_googs_list, container, false);
        initView();
        initData();
        return view;
    }

    private void initView() {
        googs_list = (ListView) view.findViewById(R.id.googs_list);
        details = (RelativeLayout) view.findViewById(R.id.details);
        commodity = (FrescoDrawee) view.findViewById(R.id.commodity);
        title = (TextView) view.findViewById(R.id.title);
        numText = (TextView) view.findViewById(R.id.numText);
        Coupons = (TextView) view.findViewById(R.id.Coupons);
        ImageView goodsnum_more = (ImageView) view.findViewById(R.id.goodsnum_more);
        ImageView goodsnum_less = (ImageView) view.findViewById(R.id.goodsnum_less);
        purchase = (Button) view.findViewById(R.id.purchase);
        commodity_price = (TextView) view.findViewById(R.id.commodity_price);
        goodsnum_more.setOnClickListener(this);
        goodsnum_less.setOnClickListener(this);
    }

    private void initData() {
        CommonAdapter<RankModel> adapter = new CommonAdapter<RankModel>(getActivity(), rankModels, R.layout.item_goods_list) {
            @Override
            public void convert(ViewHolder helper, final RankModel item, int position) {
                helper.setImageURI(R.id.commodity, item.imageurl);
            }
        };
        googs_list.setAdapter(adapter);
        googs_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                details.setVisibility(View.VISIBLE);
                setDetails(rankModels.get(position));
            }
        });
        userInfo = CacheDataManager.getInstance().loadUser();
        MainEnter mainEnter = new MainEnter(getActivity());
        mainEnter.loadRank(CommonUrlConfig.RankingList, userInfo.userid, userInfo.token, callback);
    }

    private HttpBusinessCallback callback = new HttpBusinessCallback() {
        @Override
        public void onFailure(Map<String, ?> errorMap) {
            LoadingDialog.cancelLoadingDialog();
        }

        @Override
        public void onSuccess(final String response) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.cancelLoadingDialog();
                    CommonListResult<RankModel> datas = JsonUtil.fromJson(response, new TypeToken<CommonListResult<RankModel>>() {
                    }.getType());
                    if (datas == null) {
                        return;
                    }
                    if (HttpFunction.isSuc(datas.code)) {
                        rankModels.clear();
                        rankModels.addAll(datas.data);
                    } else {
                        onBusinessFaild(datas.code);
                    }
                }
            });
        }
    };

    private void setDetails(RankModel model) {
        numText.setText("1");
        commodity.setImageURI(model.imageurl);
        title.setText(model.name);
        commodity_price.setText(model.number);
        Coupons.setText(getString(R.string.goods_coupons) + 1231231);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.goodsnum_more:
                numText.setText(String.valueOf(Integer.valueOf(numText.getText().toString()) + 1));
                break;
            case R.id.goodsnum_less:
                if (Integer.valueOf(numText.getText().toString()) > 1) {
                    numText.setText(String.valueOf(Integer.valueOf(numText.getText().toString()) - 1));
                }
                break;
        }
    }
}
