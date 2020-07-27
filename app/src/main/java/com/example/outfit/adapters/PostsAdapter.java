package com.example.outfit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.outfit.activities.DetailActivity;
import com.example.outfit.helpers.ClickListener;
import com.example.outfit.R;
import com.example.outfit.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";
    private final int REQUEST_CODE = 20;
    public static final int CORNER_RADIUS = 150; // corner radius, higher value = more rounded
    public static final int CROP_MARGIN = 10; // crop margin, set to 0 for corners with no crop

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> posts) {
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivImage;
        private TextView tvTitle;
        protected ImageView ivProfile;
        private TextView tvUsername;
        protected ImageView ivLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivLike = itemView.findViewById(R.id.ivLike);

            itemView.setOnClickListener(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(final Post post) {
            // Bind the post data to the view elements
            tvTitle.setText(post.getTitle());
            tvUsername.setText(post.getAuthorUsername());

            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }

            ParseObject author = post.getAuthor();
            ParseFile profileImage = author.getParseFile("profileImage");
            if (profileImage != null) {
                Glide.with(context).load(profileImage.getUrl())
                        .transform(new RoundedCornersTransformation(CORNER_RADIUS, CROP_MARGIN)).into(ivProfile);
            }

            if (post.getLikes().contains(ParseUser.getCurrentUser().getObjectId())) {
                ivLike.setSelected(true);
            } else {
                ivLike.setSelected(false);
            }
            
            ClickListener.setIvLikeClickListener(post, ivLike, TAG);
            ClickListener.setIvProfileClickListener(post, ivProfile, TAG);
            ClickListener.setDoubleTapListener(ivImage, context, ivLike, post, TAG,
                    getAdapterPosition(), posts, REQUEST_CODE);
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: " + TAG);
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the post clicked on
                Post post = posts.get(position);
                Intent i = new Intent(context, DetailActivity.class);
                // serialize the movie using parceler, use its short name as a key
                i.putExtra("post", Parcels.wrap(post));
                i.putExtra("position", position);
                ((Activity) context).startActivityForResult(i, REQUEST_CODE);
            }
        }
    }
}
