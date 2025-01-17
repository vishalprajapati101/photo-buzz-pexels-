package com.imagevault.wallpapersinfinity.adpter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.imagevault.wallpapersinfinity.fragment.CategoryFragment;
import com.imagevault.wallpapersinfinity.fragment.DailyFragment;
import com.imagevault.wallpapersinfinity.fragment.LatestFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DailyFragment();
            case 1:
                return new LatestFragment();
            case 2:
                return new CategoryFragment();
            default:
                return new DailyFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Three tabs
    }
}
