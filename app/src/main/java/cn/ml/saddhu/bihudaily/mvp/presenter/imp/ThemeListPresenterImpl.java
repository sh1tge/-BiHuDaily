package cn.ml.saddhu.bihudaily.mvp.presenter.imp;

import java.util.ArrayList;
import java.util.List;

import cn.ml.saddhu.bihudaily.engine.commondListener.OnNetLoadMoreListener;
import cn.ml.saddhu.bihudaily.engine.commondListener.OnNetRefreshListener;
import cn.ml.saddhu.bihudaily.engine.domain.BaseStory;
import cn.ml.saddhu.bihudaily.engine.domain.ThemeInfo;
import cn.ml.saddhu.bihudaily.mvp.model.ThemeListModel;
import cn.ml.saddhu.bihudaily.mvp.model.impl.ThemeListModelImpl;
import cn.ml.saddhu.bihudaily.mvp.presenter.ThemeListPresenter;
import cn.ml.saddhu.bihudaily.mvp.view.IThemeListView;

/**
 * Created by sadhu on 2017/2/11.
 * Email static.sadhu@gmail.com
 * Describe: 主题presenter实现
 */
public class ThemeListPresenterImpl extends BasePresenter<IThemeListView>  implements ThemeListPresenter, OnNetRefreshListener<ThemeInfo>, OnNetLoadMoreListener<List<BaseStory>> {

    private ThemeListModel mModel;
    private ThemeInfo mThemeInfo;

    public ThemeListPresenterImpl(IThemeListView themeListView) {
        super(themeListView);
        this.mModel = new ThemeListModelImpl(this, this);
    }


    @Override
    public void getThemePageList(String themeId) {
        mModel.getThemePageList(themeId);
    }

    @Override
    public void loadMoreThemePageList(String themeId) {
        mModel.loadMoreThemePageList(themeId, mThemeInfo.stories.get(mThemeInfo.stories.size() - 1).id);
    }

    @Override
    public ArrayList<String> getThemeIdList() {
        ArrayList<String> ThemeIds = new ArrayList<>();
        for (BaseStory story : mThemeInfo.stories) {
            ThemeIds.add(story.id);
        }
        return ThemeIds;

    }

    @Override
    public void setItemRead(int position) {
        if (!mThemeInfo.stories.get(position).isRead) {
            mThemeInfo.stories.get(position).isRead = true;
            mModel.setItemRead(mThemeInfo.stories.get(position).id);
            mView.notifyItemChange(position);
        }
    }

    @Override
    public void onRefreshSuccess(ThemeInfo themeInfo) {
        mThemeInfo = themeInfo;
        mView.onRefreshSucces(themeInfo);
    }

    @Override
    public void onRefreshError(int code) {
        mView.onRefreshError(code);
    }

    @Override
    public void onLoadMoreSuccess(List<BaseStory> baseStories) {
        mView.onLoadMoreSuccuess(baseStories);
    }

    @Override
    public void onLoadMoreError(int code) {

    }

    @Override
    public void onDestroy() {
        mView = null;
        mModel.onDestroy();
        mModel = null;
    }
}
