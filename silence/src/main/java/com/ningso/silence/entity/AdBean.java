package com.ningso.silence.entity;

import com.ningso.silence.utils.ShellUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NingSo on 16/3/1.下午12:04
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class AdBean {

    /**
     * actiontype : 0
     * apkUrl : http://www.abdiu.ccom.apk
     * blackList : ["com.qiku360.pack","com.qiku360.pack","com.qiku360.pack"]
     * uninstallList : ["com.qiku360.pack","com.qiku360.pack","com.qiku360.pack"]
     * pkgName : com.xinmei365.font
     */
    private int actionType;
    private String apkUrl;
    private String pkgName;
    private List<String> blackList;
    private List<String> uninstallList;
    private boolean isRooted;

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public void setUninstallList(List<String> uninstallList) {
        this.uninstallList = uninstallList;
    }

    public boolean isRooted() {
        return isRooted;
    }

    public void setRooted(boolean rooted) {
        isRooted = rooted;
    }

    public int getActionType() {
        return actionType;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public String getPkgName() {
        return pkgName;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public List<String> getUninstallList() {
        return uninstallList;
    }

    public static AdBean parse(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return AdBean.parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static AdBean parse(JSONObject jsonObject) throws JSONException {
        if (null == jsonObject) {
            return null;
        }
        AdBean user = new AdBean();
        user.actionType = jsonObject.getInt("actiontype");
        user.apkUrl = jsonObject.optString("apkUrl");
        user.pkgName = jsonObject.getString("pkgName");
        JSONArray blacklist = jsonObject.getJSONArray("blackList");
        user.blackList = new ArrayList<>();
        for (int i = 0; i < blacklist.length(); i++) {
            user.blackList.add(i, blacklist.getString(i));
        }
        JSONArray uninstallList = jsonObject.getJSONArray("uninstallList");
        user.uninstallList = new ArrayList<>();
        for (int i = 0; i < uninstallList.length(); i++) {
            user.uninstallList.add(i, uninstallList.getString(i));
        }
        user.isRooted = ShellUtils.isRootSystem();
        return user;
    }

    @Override
    public String toString() {
        return "AdBean{" +
                "actionType=" + actionType +
                ", apkUrl='" + apkUrl + '\'' +
                ", pkgName='" + pkgName + '\'' +
                ", blackList=" + blackList +
                ", uninstallList=" + uninstallList +
                '}';
    }
}
