package com.example.outfit.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;


@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_IMAGE = "image";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_FAVORITES = "favorites";
    public static final String KEY_COMMENTS= "comments";
    public static final String KEY_TAGS= "tags";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

    public ArrayList getLikes() {
        return (ArrayList) get(KEY_LIKES);
    }

    public void setLikes(String userId) {
        add(KEY_LIKES, userId);
    }

    public void removeLikes(String userId) {
        removeAll(KEY_LIKES, Collections.singleton(userId));
    }

    public ArrayList getComments() {
        return (ArrayList) get(KEY_COMMENTS);
    }

    public void setComments(String commentId) {
        add(KEY_COMMENTS, commentId);
    }

    public ArrayList getTags() {
        return (ArrayList) get(KEY_TAGS);
    }

    public void setTags(String tag) {
        add(KEY_TAGS, tag);
    }

    public ArrayList getFavorites() {
        return (ArrayList) get(KEY_FAVORITES);
    }

    public void setFavorites(String userId) {
        add(KEY_FAVORITES, userId);
    }

    public void removeFavorites(String userId) {
        removeAll(KEY_FAVORITES, Collections.singleton(userId));
    }
}
