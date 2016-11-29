package com.huangjiazhong.youlian.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.adapter.JokeAdapter;
import com.huangjiazhong.youlian.entity.Joke;
import com.huangjiazhong.youlian.entity.Result;
import com.huangjiazhong.youlian.http.AsynTaskThread;
import com.huangjiazhong.youlian.view.SimpleFooter;
import com.huangjiazhong.youlian.view.SimpleHeader;
import com.huangjiazhong.youlian.view.ZrcListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class JokeFragment extends Fragment {

    private int currentPage = 1;
    private OnFragmentInteractionListener mListener;

    private Handler mHandler;
    private ImageLoader mImageLoader;
    private List<Joke> mJokes;
    private JokeAdapter mAdapter;
    private ZrcListView mJokeListView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joke, container, false);
        mJokeListView = (ZrcListView) view.findViewById(R.id.joke_listView);
        mHandler = new Handler();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
//        设置默认偏移量，主要用于实现透明标题栏功能。
//        float density = getResources().getDisplayMetrics().density;
//        listView.setFirstTopOffset((int) (50 * density));
        //下啦刷新
        SimpleHeader header = new SimpleHeader(getActivity());
        header.setTextColor(0xff0066aa);
        header.setCircleColor(0xff33bbee);
        mJokeListView.setHeadable(header);

        // 设置加载更多的样式
        SimpleFooter footer = new SimpleFooter(getActivity());
        footer.setCircleColor(0xff33bbee);
        mJokeListView.setFootable(footer);

        // 设置列表项出现动画
        mJokeListView.setItemAnimForTopIn(R.anim.topitem_in);
        mJokeListView.setItemAnimForBottomIn(R.anim.bottomitem_in);
        //下拉刷新回调
        mJokeListView.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                refresh();
            }
        });
        //加载更多回调
        mJokeListView.setOnLoadMoreStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                loadMore();
            }
        });
        // Bug，当ZListView为没有数据时，无法下位刷新。建议当无数据的加载使用loading界面、无数据界面。
        mJokes = new ArrayList<>();
        Joke joke = new Joke();
        mJokes.add(joke);

        mAdapter = new JokeAdapter(getActivity(), mJokes);
        mJokeListView.setAdapter(mAdapter);
        mJokeListView.refresh(); // 主动刷新
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Result result = (Result) msg.obj;
            if (result != null) {
                List<Joke> list = (List<Joke>) result.getItems();
                if (list != null && list.size() > 0) {
                    if (currentPage == 1) {
                        mJokes.clear();
                    }
                    mJokes.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    mJokeListView.setRefreshSuccess("刚刚更新"); // 通知加载成功
                    mJokeListView.startLoadMore(); // 开启LoadingMore功能
                } else {
                    mJokes.add(new Joke());
                    mAdapter.notifyDataSetChanged();
                    mJokeListView.setRefreshFail("没有更新");
                }
            } else {
                mJokes.add(new Joke());
                mAdapter.notifyDataSetChanged();
                mJokeListView.setRefreshFail("更新失败");
            }
        }
    };
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener != null) mListener = null;
        if (mHandler != null) mHandler.removeCallbacksAndMessages(mHandler);
    }

    @Override
    public void onDestroyView() {
        if (mJokes != null) {
            mJokes.clear();
            if (mAdapter != null && mJokeListView != null) {
                mAdapter.notifyDataSetChanged();
                mJokeListView = null;
                mAdapter = null;
            }
        }
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(myHandler);
            myHandler = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(myHandler);
            mHandler = null;
        }
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    /**=============== 下拉刷新 ===============*/
    private void refresh() {
        mJokes.clear();
        currentPage = 1;
        new AsynTaskThread(myHandler,currentPage).execute();
    }
    /**=============== 加载更多 ===============*/
    private void loadMore() {
        new AsynTaskThread(myHandler,++currentPage).execute();
    }
}
