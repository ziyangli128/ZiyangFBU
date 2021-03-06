package com.example.outfit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.outfit.R;
import com.example.outfit.adapters.PostsAdapter;
import com.example.outfit.databinding.ActivityDetailBinding;
import com.example.outfit.databinding.ActivityMainBinding;
import com.example.outfit.helpers.ClickListener;
import com.example.outfit.helpers.QueryPosts;
import com.example.outfit.models.Author;
import com.example.outfit.models.Place;
import com.example.outfit.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class DetailActivity extends AppCompatActivity{

    public static final String TAG = "DetailActivity";
    ActivityDetailBinding binding;
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWINGS = "followings";

    Post post;
    Context context;
    List<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        // unwrap the post passed in via intent
        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));
        int position = getIntent().getIntExtra("position", 0);
        Log.d("DetailActivity", String.format("Showing details for '%s'", post.getTitle()));

        bindTopToolBar();
        bindBottomToolBar();
        bindBody();

        // Prepare data intent
        Intent i = new Intent();
        // Pass tweet back as result
        i.putExtra("post", Parcels.wrap(post));
        i.putExtra("position", position);
        // Set result code and bundle data for response
        setResult(RESULT_OK, i);
    }

    public void bindTopToolBar() {
        binding.tvUsername.setText(post.getAuthorUsername());

        ParseObject author = post.getAuthor();
        ParseFile profileImage = author.getParseFile("profileImage");
        if (profileImage != null) {
            Glide.with(this).load(profileImage.getUrl())
                    .circleCrop().into(binding.ivProfileImage);
        }

        ArrayList followers = ((ArrayList) post.getAuthor().get(KEY_FOLLOWERS));
        if (followers != null && followers.contains(ParseUser.getCurrentUser().getParseObject("author").getObjectId())) {
            binding.btnFollow.setText(R.string.unfollow);
        } else {
            binding.btnFollow.setText(R.string.follow);
        }

        //ClickListener.setIvProfileClickListener(post, binding.ivProfileImage, TAG);
        ClickListener.setbtnFollowClickListener((Author) post.getAuthor(), binding.btnFollow, TAG);
    }

    public void bindBottomToolBar() {
        if (post.getLikes().contains(ParseUser.getCurrentUser().getObjectId())) {
            binding.ivLike.setSelected(true);
        } else {
            binding.ivLike.setSelected(false);
        }

        if (post.getFavorites().contains(ParseUser.getCurrentUser().getParseObject("author").getObjectId())) {
            binding.ivFavorite.setSelected(true);
        } else {
            binding.ivFavorite.setSelected(false);
        }

        ClickListener.setIvLikeClickListener(post, binding.ivLike, TAG);
        ClickListener.setIvFavoriteClickListener(post, binding.ivFavorite, TAG);
    }

    public void bindBody() {
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(binding.ivImage);
        }

        final ArrayList brand = post.getBrand();
        final ArrayList tags = post.getTags();

        binding.tvTitle.setText(post.getTitle());
        binding.tvDescription.setText("Description: " + post.getDescription());
        binding.tvCreatedAt.setText(post.getCreatedAt().toString());
        if (brand != null && !brand.get(0).toString().isEmpty()) {
            binding.tvBrand.setText("Brand: " + post.getBrand().get(0).toString());
        } else {
            binding.tvBrand.setVisibility(View.GONE);
            binding.btnMap.setVisibility(View.GONE);
        }

        for (int i = 0; i < tags.size(); ++i) {
            if (i == 0) {
                binding.tvTag1.setText(tags.get(0).toString());
            } else if (i == 1) {
                binding.tvTag2.setVisibility(View.VISIBLE);
                binding.tvTag2.setText(tags.get(1).toString());
            } else if (i == 2) {
                binding.tvTag3.setVisibility(View.VISIBLE);
                binding.tvTag3.setText(tags.get(2).toString());
            } else if (i == 3) {
                binding.tvTag4.setVisibility(View.VISIBLE);
                binding.tvTag4.setText(tags.get(3).toString());
            }
        }

        binding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: btnMap");
                if (brand == null) {
                    Toast.makeText(DetailActivity.this, "No brand added to this post!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(DetailActivity.this, MapsActivity.class);
                    i.putExtra("brand", brand);
                    startActivity(i);
                }
            }
        });
    }
}