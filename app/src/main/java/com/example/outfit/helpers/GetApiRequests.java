package com.example.outfit.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.outfit.models.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class GetApiRequests {
    static List<Place> places;

    public static void getPlacesRequest(final GoogleMap map, final String TAG, Activity activity) {
        ArrayList brands = activity.getIntent().getStringArrayListExtra("brand");
        String brand;
        places = new ArrayList<>();
        // create an AsyncHttpClient to send request
        AsyncHttpClient client = new AsyncHttpClient();

        for (int i = 0; i < brands.size(); i++) {
            brand = brands.get(i).toString();
            Log.i(TAG, "getPlacesRequest: " + brand);

            // send the request from client
            String url = "https://maps.googleapis.com/maps/api/place/textsearch/"
                    + "json?query=" + brand + "&key=AIzaSyCDL3qjAsZGFncXeN7PogP4nC-tY3xLZJ8";

            client.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    // called when response HTTP status is "200 OK"
                    Log.d(TAG, "onSuccess");
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        JSONArray results = jsonObject.getJSONArray("results");
                        places.addAll(Place.fromJsonArray(results));
                        Log.i(TAG, "Places: " + places.size());
                    } catch (JSONException e) {
                        Log.e(TAG, "Hit json exception", e);
                        e.printStackTrace();
                    }
                    for (int i = 0; i < places.size(); i++) {
                        Log.i(TAG, "onMapReady: " + places.get(i).getName());
                        LatLng position = new LatLng(places.get(i).getLat(), places.get(i).getLng());
                        map.addMarker(new MarkerOptions().position(position));
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.d(TAG, "onFailure: Issue with requesting api.");
                }
            });
        }
    }


    static Place markedPlace;
    public static Place getPlacesDetailRequest(LatLng position, final String TAG, final Context context) {
        String placeId = null;

        for (Place place: places) {
            if (place.getLat() == position.latitude && place.getLng() == position.longitude) {
                placeId = place.getPlaceId();
                markedPlace = place;
                Log.i(TAG, "getPlacesDetailRequest: " + placeId);
            }
        }

        // send the request from client
        String url = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "place_id=" + placeId + "&key=AIzaSyCDL3qjAsZGFncXeN7PogP4nC-tY3xLZJ8";

        // create an AsyncHttpClient to send request
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "onSuccess: getting api request");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONObject result = jsonObject.getJSONObject("result");

                    markedPlace.addFormattedAddress(result
                            .getString("formatted_address"));
                    markedPlace.addFormattedPhoneNumber(result
                            .getString("formatted_phone_number"));
                    markedPlace.addRating(result
                            .getDouble("rating"));
                    markedPlace.addWebsite(result
                            .getString("website"));
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                    e.printStackTrace();
                }
                Toast.makeText(context, markedPlace.getName() + "\n"
                                + markedPlace.getFormattedAddress() + "\n"
                                + "Phone: " + markedPlace.getFormattedPhoneNumber() + "\n"
                                + "Rating: " + markedPlace.getRating() + "\n"
                                + "Website: " + markedPlace.getWebsite() + "\n",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
        return markedPlace;
    }
}
