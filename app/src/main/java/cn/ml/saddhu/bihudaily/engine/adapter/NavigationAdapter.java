package cn.ml.saddhu.bihudaily.engine.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Collections;

import cn.ml.saddhu.bihudaily.R;
import cn.ml.saddhu.bihudaily.engine.constant.SharedPreferenceConstants;
import cn.ml.saddhu.bihudaily.engine.domain.Theme;
import cn.ml.saddhu.bihudaily.engine.domain.UserInfo;

/**
 * Created by sadhu on 2016/11/13.
 * Email static.sadhu@gmail.com
 * Describe: 左侧专栏list
 */
public class NavigationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_USER_INFO = 1;
    private static final int TYPE_MENU_HOME = 2;
    private static final int TYPE_MENU_NORMAL = 3;

    private UserInfo mInfo;
    private int mCurrentSelect = 1;
    private OnNavigationItemClickListener mListener;

    public NavigationAdapter(UserInfo info) {
        this.mInfo = info;
    }

    public void setOnNavigationItemClickListener(OnNavigationItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        switch (viewType) {
            case TYPE_USER_INFO:
                vh = new NavigationUserVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_navigation_drawer_list_header, parent, false));
                break;
            case TYPE_MENU_HOME:
                vh = new NavigationItemHomeVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_navigation_drawer_list_item_home, parent, false));
                break;
            default:
                vh = new NavigationItemVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_navigation_drawer_list_item, parent, false));
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NavigationUserVH) {
            ((NavigationUserVH) holder).bindData();
        } else if (holder instanceof Selectable) {
            ((Selectable) holder).setSelect(position == mCurrentSelect);
            if (position != 1) {
                ((NavigationItemVH) holder).bindData(mInfo.themes.get(position - 2));
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 2;
        if (mInfo != null && mInfo.themes != null) {
            count += mInfo.themes.size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_USER_INFO;
        } else if (position == 1) {
            return TYPE_MENU_HOME;
        } else {
            return TYPE_MENU_NORMAL;
        }
    }

    public void setData(UserInfo info) {
        this.mInfo = info;
    }

    public Theme getCurrentTheme() {
        return mCurrentSelect == 1 ? null : mInfo.themes.get(mCurrentSelect - 2);
    }

    private class NavigationItemHomeVH extends RecyclerView.ViewHolder implements View.OnClickListener, Selectable {


        NavigationItemHomeVH(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void setSelect(boolean select) {
            itemView.setSelected(select);
        }


        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (mCurrentSelect != position) {
                    mCurrentSelect = position;
                    notifyDataSetChanged();
                    mListener.onItemClick(mCurrentSelect == 1 ? null : mInfo.themes.get(mCurrentSelect - 2), mCurrentSelect, true);
                } else {
                    mListener.onItemClick(mCurrentSelect == 1 ? null : mInfo.themes.get(mCurrentSelect - 2), mCurrentSelect, false);
                }
            }
        }
    }

    private class NavigationItemVH extends RecyclerView.ViewHolder implements View.OnClickListener, Selectable {

        ImageView drawer_item_remind;
        ImageView drawer_item_subscribe_button;
        TextView drawer_item_title;

        NavigationItemVH(View itemView) {
            super(itemView);
            drawer_item_remind = (ImageView) itemView.findViewById(R.id.drawer_item_remind);
            drawer_item_subscribe_button = (ImageView) itemView.findViewById(R.id.drawer_item_subscribe_button);
            drawer_item_title = (TextView) itemView.findViewById(R.id.drawer_item_title);
            drawer_item_remind.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        void bindData(Theme info) {
            drawer_item_remind.setVisibility(info.isSubscribe ? View.GONE : View.VISIBLE);
            drawer_item_subscribe_button.setVisibility(info.isSubscribe ? View.VISIBLE : View.GONE);
            drawer_item_title.setText(info.name);
        }


        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                switch (v.getId()) {
                    case R.id.drawer_item_remind:
                        Theme clickTheme = mInfo.themes.get(position - 2);

                        if (!clickTheme.isSubscribe) {
                            clickTheme.isSubscribe = true;
                            Theme selectTheme = null;
                            if (mCurrentSelect != 1) {
                                selectTheme = mInfo.themes.get(mCurrentSelect - 2);
                            }
                            Collections.sort(mInfo.themes);
                            if (mCurrentSelect != 1) {
                                mCurrentSelect = mInfo.themes.indexOf(selectTheme) + 2;
                            }
                            mListener.onRemindClick(mInfo.themes.indexOf(clickTheme));
                            drawer_item_remind.setVisibility(View.GONE);
                            drawer_item_subscribe_button.setVisibility(View.VISIBLE);
                            notifyDataSetChanged();
                        }
                        break;
                    default:
                        if (mCurrentSelect != position) {
                            mCurrentSelect = position;
                            notifyDataSetChanged();
                            mListener.onItemClick(mCurrentSelect == 1 ? null : mInfo.themes.get(mCurrentSelect - 2), mCurrentSelect, true);
                        } else {
                            mListener.onItemClick(mCurrentSelect == 1 ? null : mInfo.themes.get(mCurrentSelect - 2), mCurrentSelect, false);
                        }
                        break;
                }
            }
        }

        @Override
        public void setSelect(boolean select) {
            itemView.setSelected(select);
        }
    }

    public class NavigationUserVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        SimpleDraweeView drawer_avatar;
        TextView drawer_nick_name;
        LinearLayout drawer_favorite;
        LinearLayout drawer_offline;
        RelativeLayout drawer_user;
        public TextView drawer_offline_progress;

        NavigationUserVH(View itemView) {
            super(itemView);
            drawer_avatar = itemView.findViewById(R.id.drawer_avatar);
            drawer_nick_name = itemView.findViewById(R.id.drawer_nick_name);
            drawer_favorite = itemView.findViewById(R.id.drawer_favorite);
            drawer_offline = itemView.findViewById(R.id.drawer_offline);
            drawer_user = itemView.findViewById(R.id.drawer_user);
            drawer_offline_progress = itemView.findViewById(R.id.drawer_offline_progress);
            drawer_user.setOnClickListener(this);
            drawer_favorite.setOnClickListener(this);
            drawer_offline.setOnClickListener(this);
        }

        void bindData() {
            if (mInfo != null && mInfo.uid != SharedPreferenceConstants.DEFAULT_UID) {
                drawer_avatar.setImageURI(mInfo.avatar);
                drawer_nick_name.setText(mInfo.name);
            }
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                switch (v.getId()) {
                    case R.id.drawer_user:
                        mListener.onUserClick();
                        break;
                    case R.id.drawer_favorite:
                        mListener.onFavoriteClick();
                        break;
                    case R.id.drawer_offline:
                        mListener.onOfflineClick();
                        break;
                }
            }
        }
    }

    public interface OnNavigationItemClickListener {

        void onRemindClick(int position);

        /**
         * drawer item clicked
         *
         * @param theme     item's data
         * @param position  item's position
         * @param isDifItem is clicking different Item
         */
        void onItemClick(Theme theme, int position, boolean isDifItem);

        void onUserClick();

        void onFavoriteClick();

        void onOfflineClick();
    }

    interface Selectable {
        void setSelect(boolean select);
    }
}
