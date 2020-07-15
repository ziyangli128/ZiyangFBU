package com.example.outfit.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;

@ParseClassName("User")
public class User extends ParseUser {

    public static final String KEY_FAVORITES = "favorites";

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
