package com.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lx on 2017/4/6.
 */

public abstract class BaseFragment extends Fragment {
    /**
     * Fragment被创建
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
          init();
        super.onCreate(savedInstanceState);
    }

    /**
     * 返回Fragment所需要的布局
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView();
    }
    /**
     * 宿主Activity被创建的时候
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initData();
        initEVent();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }

    protected void initEVent(){};

    /**
     * @des 进行相关初始化操作
     * @des 在BaseFragmentCommon中, 不知道进行什么样的初始化操作, 交给子类, 子类是选择性实现
     * @called Fragment被创建
     */
    public void init() {

    }

    /**
     * @return
     * @des 初始化对应的视图, 返回给Fragment进行展示
     * @des 在BaseFragmentCommon中, 不知道如何具体初始化对应的视图, 交给子类, 子类是必须实现
     * @des 针对initView方法,必须实现,但是不知道具体实现,所以定义成为抽象方法,交给子类具体实现
     * @called Fragemnt需要一个布局的时候
     */
    public abstract View initView();
    /**
     * @des 初始化Fragment里面的数据加载
     * @des 在BaseFragmentCommon中, 不知道如何具体进行数据加载,交给子类,子类是选择性实现
     * @called 宿主Activity被创建的时候
     */
    public abstract void initData();

    /**
     * @des 初始化Fragment里面相关的监听
     * @des 在BaseFragmentCommon中, 不知道如何具体添加事件的监听,交给子类,子类选择性的实现
     * @called 宿主Activity被创建的时候
     */
    public void initListener() {
    }
}
