package com.example.outfit.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.outfit.R;
import com.example.outfit.databinding.FragmentAddTagBinding;
import com.example.outfit.databinding.FragmentMapDetailBinding;
import com.example.outfit.models.Place;
import com.google.android.material.chip.Chip;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapDetailFragment extends DialogFragment {

    public static final String TAG = "AddTagFragment";

    FragmentMapDetailBinding binding;
    Place place;

    public MapDetailFragment() {
        // Empty constructor is required for DialogFragment
    }

    public MapDetailFragment(Place place) {
        this.place = place;
    }

    public static MapDetailFragment newInstance(String title, Place place) {
        MapDetailFragment frag = new MapDetailFragment(place);
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapDetailBinding.inflate(getLayoutInflater(), container, false);

        // layout of fragment is stored in a special property called root
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvPlaceName.setText(place.getName());
        binding.tvPlaceAddress.setText("Address: " + place.getFormattedAddress());
        binding.tvPlacePhone.setText("Phone number: " + place.getFormattedPhoneNumber());
        binding.tvPlaceRating.setText("Rating: " + place.getRating());
        binding.tvPlaceWebsite.setText("Website: " + place.getWebsite());
    }
}