package com.dxy.network.okhttp.callback;

/**
 * 网络请求回调
 * 2016/9/28 13:15
 * @author duanxinyuan
 */
public interface RequestCallback {

    /**
     * 网络请求成功
     * @param s 返回的数据
     */
    void success(String s);

    /**
     * 网络请求失败
     * @param s 返回的数据
     */
    void failure(String s);

}
