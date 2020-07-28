package com.example.outfit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.outfit.R;
import com.example.outfit.databinding.ActivityMainBinding;
import com.example.outfit.fragments.BaseFragment;
import com.example.outfit.fragments.ComposeFragment;
import com.example.outfit.fragments.FollowingPostsFragment;
import com.example.outfit.fragments.MyProfileFragment;
import com.example.outfit.fragments.NearbyPostsFragment;
import com.example.outfit.fragments.NewPostsFragment;
import com.example.outfit.fragments.PostsFragment;
import com.example.outfit.fragments.ProfileFragment;
import com.example.outfit.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();

    ActivityMainBinding binding;
    Fragment fragment;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        final PostsFragment postsFragment = new PostsFragment();
        final ComposeFragment composeFragment = new ComposeFragment();
        final ProfileFragment myProfileFragment = new MyProfileFragment();


        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        Log.i(TAG, "onNavigationItemSelected: posts" + fragmentManager.getBackStackEntryCount());
                        for (int i = 0; i < fragmentManager.getBackStackEntryCount() - 2; i++) {
                            fragmentManager.popBackStack();
                        }
                        fragmentManager.beginTransaction()
                                .replace(R.id.flContainer, postsFragment).commit();
                        break;
                    case R.id.action_compose:
                        Log.i(TAG, "onNavigationItemSelected: compose" + fragmentManager.getBackStackEntryCount());
                        fragmentManager.beginTransaction().addToBackStack("To compose")
                                .replace(R.id.flContainer, composeFragment).commit();
                        break;
                    case R.id.action_profile:
                    default:
                        Log.i(TAG, "onNavigationItemSelected: my porfile" + fragmentManager.getBackStackEntryCount());
                        fragmentManager.beginTransaction().addToBackStack("To myProfile")
                                .replace(R.id.flContainer, myProfileFragment).commit();
                        break;
                }
                return true;
            }
        });
        // set default selection view
        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK
                && !fragmentManager.findFragmentById(R.id.flContainer).toString().contains("ComposeFragment") ) {
            // Get data from the Intent (update post)
            Post post = Parcels.unwrap(data.getParcelableExtra("post"));
            int position = data.getIntExtra("position", 0);
            Log.i(TAG, "onActivityResult: post updated: " + position);
            // Update the Recycler View with the new post
            // Modify data source of posts
            // Update the adapter
            if (fragmentManager.findFragmentById(R.id.flContainer).toString().contains("ProfileFragment")) {
                ((ProfileFragment) fragmentManager.findFragmentById(R.id.flContainer)).updatePosts(post, position);
            } else {
                if (((PostsFragment)fragmentManager.findFragmentById(R.id.flContainer))
                        .getViewPager().getCurrentItem() == 0) {
                    ((NewPostsFragment) fragmentManager.findFragmentById(R.id.flContainer).getChildFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.materialviewpager_viewpager + ":" + 0)).updatePosts(post, position);
                } else if (((PostsFragment)fragmentManager.findFragmentById(R.id.flContainer))
                        .getViewPager().getCurrentItem() == 1) {
                    ((FollowingPostsFragment) fragmentManager.findFragmentById(R.id.flContainer).getChildFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.materialviewpager_viewpager + ":" + 1)).updatePosts(post, position);
                } else {
                    ((NearbyPostsFragment) fragmentManager.findFragmentById(R.id.flContainer).getChildFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.materialviewpager_viewpager + ":" + 2)).updatePosts(post, position);
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}