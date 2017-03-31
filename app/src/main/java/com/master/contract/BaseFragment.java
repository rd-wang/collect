package com.master.contract;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Litao-pc on 2016/9/7.
 */
public abstract class BaseFragment extends Fragment implements MvpView {

    protected View mRootView;
    protected final String sClassName = getClass().getSimpleName();
    private Unbinder unbinder;
    protected AppCompatActivity mContext;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
        }
        return createView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 判断当前fragment是否显示
        bindView(savedInstanceState);
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutRes = getLayoutResId();
        if (layoutRes == 0) {
            throw new IllegalArgumentException("implements getLayoutRes not 0");
        } else {
            mRootView = inflater.inflate(layoutRes, container, false);

            unbinder = ButterKnife.bind(this, mRootView);

            return mRootView;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


}
