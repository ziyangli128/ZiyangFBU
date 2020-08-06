package com.example.outfit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.outfit.R;
import com.example.outfit.activities.DetailActivity;
import com.example.outfit.fragments.MyProfileFragment;
import com.example.outfit.fragments.ProfileFragment;
import com.example.outfit.helpers.ClickListener;
import com.example.outfit.models.Author;
import com.example.outfit.models.Post;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class FollowListAdapter extends RecyclerView.Adapter<FollowListAdapter.ViewHolder>  {
    public static final String TAG = "FollowListAdapter";

    private Context context;
    private List<Author> users;

    public FollowListAdapter(Context context, List<Author> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public FollowListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_following_list, parent, false);
        return new FollowListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowListAdapter.ViewHolder holder, int position) {
        Author user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Author> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivProfileImage;
        private TextView tvUsername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);

            itemView.setOnClickListener(this);
        }

        public void bind(final Author user) {
            tvUsername.setText(user.getUsername());

            ParseFile profileImage = user.getParseFile("profileImage");
            if (profileImage != null) {
                Glide.with(context).load(profileImage.getUrl())
                        .circleCrop()
                        .into(ivProfileImage);
            }
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: " + TAG);
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the post clicked on
                Author user = users.get(position);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment fragment;
                fragment = new ProfileFragment(user);
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.remove(activity.getSupportFragmentManager().findFragmentByTag("fragment_follow_list"));
                ft.replace(R.id.flContainer, fragment);
                ft.addToBackStack("to profile");
                ft.commit();
            }
        }
    }
}
