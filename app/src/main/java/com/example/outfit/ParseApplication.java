package com.example.outfit;

import android.app.Application;

import com.example.outfit.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

import org.w3c.dom.Comment;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register the parse models
        ParseObject.registerSubclass(Post.class);
//        ParseObject.registerSubclass(Comment.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("ziyang-fbu") // should correspond to APP_ID env variable
                .clientKey("ZiyangFBUapp")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://ziyang-fbu.herokuapp.com/parse/").build());
    }
}
