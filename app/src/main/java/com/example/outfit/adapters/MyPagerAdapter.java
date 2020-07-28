package com.example.outfit.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.outfit.R;
import com.example.outfit.fragments.FollowingPostsFragment;
import com.example.outfit.fragments.NearbyPostsFragment;
import com.example.outfit.fragments.NewPostsFragment;
import com.example.outfit.fragments.PostsFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new NewPostsFragment();
        } else if (position == 1) {
            return new FollowingPostsFragment();
        } else {
            return new NearbyPostsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "New";
        } else if (position == 1) {
            return "Following";
        } else {
            return "Nearby";
        }
    }
}
