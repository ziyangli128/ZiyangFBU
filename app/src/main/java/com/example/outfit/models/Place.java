package com.example.outfit.models;

import android.graphics.Movie;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Place {

    // declare some fields for Movie object
    String name;
    String lng;
    String lat;


    // no-arg, empty constructor required for Parceler
    public Place() {}

    // Movie constructor, initialize the fields from JSON
    public Place(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("name");
        lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
        lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
    }

    // turn the object from JSONArray into Place objects and store in a list
    public static List<Place> fromJsonArray(JSONArray placesJsonArray) throws JSONException {
        // create a list of Places
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < placesJsonArray.length(); ++i) {
            // turn each JSONObject into Movie and add to the list
            places.add(new Place(placesJsonArray.getJSONObject(i)));
        }
        return places;
    }

    public String getName() {
        return name;
    }

    public Double getLng() {
        return Double.parseDouble(lng);
    }

    public Double getLat() {
        return Double.parseDouble(lat);
    }
}
