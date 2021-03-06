package com.angelatech.yeyelive1.activity.function;

import android.content.Context;

import com.angelatech.yeyelive1.CommonUrlConfig;
import com.angelatech.yeyelive1.db.model.BasicUserInfoDBModel;
import com.angelatech.yeyelive1.web.HttpFunction;
import com.will.common.string.Encryption;
import com.will.common.string.security.Md5;
import com.will.web.handle.HttpBusinessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jjfly on 16-3-21.
 * 房间接口
 */
public class MainEnter extends HttpFunction {
    public MainEnter(Context context) {
        super(context);
    }

    /**
     * 获取所有的房间列表
     */
    public void loadRoomList(String url, BasicUserInfoDBModel userInfo, int pageIndex, int pagesize, long time, int type, HttpBusinessCallback callback) {
        if (userInfo == null || callback == null) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        params.put("type", String.valueOf(type));
        if (time > 0) {
            params.put("datesort", "" + time);
        }
        params.put("pageindex", pageIndex + "");
        params.put("pagesize", pagesize + "");
        httpGet(url, params, callback);
    }

    /**
     * 获取所有的房间列表
     */
    public void loadRoomList(String url, BasicUserInfoDBModel userInfo, int pageIndex, int pagesize, long time, HttpBusinessCallback callback) {
        if (userInfo == null || callback == null) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userid", userInfo.userid);
        params.put("token", userInfo.token);
        if (time > 0) {
            params.put("datesort", "" + time);
        }
        params.put("pageindex", pageIndex + "");
        params.put("pagesize", pagesize + "");
        httpGet(url, params, callback);
    }

    /**
     * 获取个人资料
     */
    public void createqrcode(String url, String userid, String touserid, String token, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("toUserId", touserid);
        params.put("token", token);
        httpGet(url, params, callback);
    }

    /**
     * 获取个人资料
     */
    public void loadUserInfo(String url, String userid, String touserid, String token, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        if (!userid.equals(touserid)) {
            params.put("touserid", touserid);
        }
        params.put("token", token);
        httpGet(url, params, callback);
    }

    /**
     * 排行榜
     */
    public void loadSevenRank(String url, String userid, String token, String roomid, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("roomid", roomid);
        httpGet(url, params, callback);
    }

    /**
     * 排行榜
     */
    public void loadSevenUserRank(String url, String userid, String token, String touserid, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("touserid", touserid);
        httpGet(url, params, callback);
    }

    //排行榜总榜
    public void loadRank(String url, String userid, String token, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("str", "perGet");
        httpGet(url, params, callback);
    }

    public void ScanRecharge(String url, String userid, String token, String key, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("key", key);
        httpGet(url, params, callback);
    }


    //上传商品接口
    public void UserMallIns(String url, String userid, String token, String tradename, String tradeurl, String price, String describe, String contact, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("tradename", Encryption.utf8ToUnicode(tradename));//商品名称
        params.put("tradeurl", tradeurl);//商品地址
        params.put("price", price);//商品价格
        params.put("describe", Encryption.utf8ToUnicode(describe));//商品描述
        params.put("contact", contact);//联系方式
        httpGet(url, params, callback);
    }


    //获取主播商品接口
    public void LiveUserMallList(String url, String userid, String token, String liveuserid, String pageindex, String pagesize, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("liveuserid", liveuserid);//主笔id
        params.put("tradeurl", pageindex);//商品地址
        params.put("price", pagesize);//商品价格
        httpGet(url, params, callback);
    }

    //小金屋查询接口
    public void UserMallList(String url, String userid, String token, String pageindex, String pagesize, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("pageindex", pageindex);
        params.put("pagesize", pagesize);
        httpGet(url, params, callback);
    }

    //修改商品
    public void UserMallUpt(String url, String userid, String token, String tradename, String tradeurl, String price, String describe, String contact, String mallid, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("tradename", Encryption.utf8ToUnicode(tradename));//商品名称
        params.put("tradeurl", tradeurl);//商品图片
        params.put("price", price);//商品价格
        params.put("describe", Encryption.utf8ToUnicode(describe));//商品描述
        params.put("contact", contact);//联系方式
        params.put("mallid", mallid);//商品id
        httpGet(url, params, callback);
    }

    //下单,
    public void VoucherMallExg(String url, String userid, String token, String mallid, String num, String paypassword, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("mallid", mallid);//商品名称
        params.put("num", num);//商品图片
        params.put("paypassword", paypassword);//密码
        httpGet(url, params, callback);
    }

    //我的商品列表
    public void UesrMallOrderList(String url, String userid, String token, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        httpGet(url, params, callback);
    }

    //我添加地址
    public void UserOrderEidt(String url, String userid, String token, String oid, String address, String username, String tel, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("oid", oid);
        params.put("address", Encryption.utf8ToUnicode(address));
        params.put("username", Encryption.utf8ToUnicode(username));
        params.put("tel", tel);
        httpGet(url, params, callback);
    }

    //我确认订单
    public void UserConfirmOrder(String url, String userid, String token, String oid, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("confirm", "1");
        params.put("oid", oid);
        httpGet(url, params, callback);
    }

    //交易管理
    public void LiveUesrMallOrderList(String url, String userid, String token, int pageindex, int pagesize, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("pageindex", String.valueOf(pageindex));
        params.put("pagesize", String.valueOf(pagesize));
        httpGet(url, params, callback);
    }

    //填写快递地址
    public void liveUserOrderEidt(String url, String userid, String token, String kudi, String oid, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("kuidi", Encryption.utf8ToUnicode(kudi));
        params.put("oid", oid);
        httpGet(url, params, callback);
    }

    //主播发货接口
    public void liveUserOrderEidt(String url, String userid, String token, String oid, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("delivery", "1");
        params.put("oid", oid);
        httpGet(url, params, callback);
    }

    //金币兑换券
    public void money2ticket(String url, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", "");
        params.put("token", "");
        httpGet(url, params, callback);
    }


    //给用户添加券
    public void voucherAdd(String url, String userid, String num, String sign, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("num", num);
        params.put("sign", sign);
        httpPost(url, params, callback);
    }

    //获取录像的接口
    public void videoTime(String url, String userid, String pid, String sign, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", userid);
        params.put("pid", pid);
        params.put("sign", sign);
        httpPost(url, params, callback);
    }

    //获取金币兑换列表
    public void gold2ticket(String url, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", "");
        params.put("token", "");
        httpGet(url, params, callback);
    }

    //券的下单接口
    public void doorder(String url, String userid, String amount, String money, String iden, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", userid);
        params.put("amount", amount);
        params.put("money", money);
        params.put("unit", "1");
        params.put("appid", "");
        params.put("iden", iden);
        String sign = Md5.md5(Md5.formatUrlMap(params, true, true) + CommonUrlConfig.Sign_key);
        params.put("sign", sign);
        httpPost(url, params, callback);
    }

    //兑换接口
    public void voucher2gold(String url, String uid, String voucher, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("voucher", voucher);
        params.put("uid", uid);
        String sign = Md5.md5(Md5.formatUrlMap(params, true, true) + CommonUrlConfig.Sign_key);
        params.put("sign", sign);
        httpPost(url, params, callback);
    }

    //最新的券和金币
    public void userMoney(String url, String userid, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", userid);
        String sign = Md5.md5(Md5.formatUrlMap(params, true, true) + CommonUrlConfig.Sign_key);
        params.put("sign", sign);
        httpPost(url, params, callback);
    }

    //开关
    public void configOnoff(String url, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", "");
        params.put("token", "");
        httpGet(url, params, callback);
    }

    //获取首页按钮图片
    //开关
    public void configImage(String url, HttpBusinessCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", "");
        params.put("token", "");
        httpGet(url, params, callback);
    }
}