package com.example.outfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.outfit.R;
import com.example.outfit.adapters.MyPagerAdapter;
import com.example.outfit.models.Post;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PostsFragment extends BaseFragment {

    public static final String TAG = "PostsFragment";
    protected static List<Post> allPosts;

    MyPagerAdapter pagerAdapter;
    //ViewPager viewPager;
    MaterialViewPager viewPager;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        pagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        //viewPager = view.findViewById(R.id.pager);
        viewPager = view.findViewById(R.id.materialViewPager);
        ViewPager pager = viewPager.getViewPager();
        pager.setAdapter(pagerAdapter);
        viewPager.getToolbar().setVisibility(View.GONE);


//        TabLayout tabLayout = view.findViewById(R.id.tabTimeline);
//        tabLayout.setupWithViewPager(pager);

        viewPager.getPagerTitleStrip().setViewPager(viewPager.getViewPager());

//        viewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
//            @Override
//            public HeaderDesign getHeaderDesign(int page) {
//                switch (page) {
//                    case 0:
//                        return HeaderDesign.fromColorResAndDrawable(
//                                R.color.quantum_pink50, getResources().getDrawable(R.drawable.cover_home_1));
//                    case 1:
//                        return HeaderDesign.fromColorResAndDrawable(
//                                R.color.lime, getResources().getDrawable(R.drawable.cover_home_2));
//                    case 2:
//                        return HeaderDesign.fromColorResAndDrawable(
//                                R.color.cyan,getResources().getDrawable(R.drawable.cover_home_3));
//                }
//                return null;
//            }
//        });

    }

    @Override
    public void queryMyPosts() {
    }

    public ViewPager getViewPager() {
        return viewPager.getViewPager();
    }

}