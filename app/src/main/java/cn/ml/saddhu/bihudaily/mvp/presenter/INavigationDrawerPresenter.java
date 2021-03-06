package cn.ml.saddhu.bihudaily.mvp.presenter;

/**
 * Created by sadhu on 2016/11/13.
 * Email static.sadhu@gmail.com
 * Describe:
 */
public interface INavigationDrawerPresenter extends IBasePresenter {

    /**
     * 获取专栏信息
     */
    void getUserInfo();

    /**
     * 订阅主题
     *
     * @param position index
     */
    void RemindTheme(int position);

    /**
     * 离线下载
     */
    void downloadOfflineData();
}
