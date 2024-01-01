/*
 * CachedFragmentPagerAdapter.java
 * classes : com.dubox.drive.ui.adapter.CachedFragmentPagerAdapter
 * @author tianzengming
 * V 1.0.0
 * Create at 2013-12-16 上午11:38:06
 */
package com.moder.compass.ui.widget;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * 缓存每个页面对应的fragment，用于获取当前显示的Fragment com.dubox.drive.ui.adapter.CachedFragmentPagerAdapter
 * 
 * @author tianzengming <br/>
 *         create at 2013-12-16 上午11:38:06
 */
public abstract class CachedFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "CachedFragmentPagerAdapter";
    private SparseArray<Fragment> mFragments;

    /**
     * @param fm
     */
    public CachedFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new SparseArray<Fragment>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        mFragments.put(position, (Fragment) object);
        return object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mFragments.remove(position);
    }

    public Fragment getFragment(int position) {
        return mFragments.get(position);
    }
}
