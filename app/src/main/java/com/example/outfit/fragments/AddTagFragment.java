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

import com.example.outfit.R;
import com.example.outfit.databinding.FragmentAddTagBinding;
import com.google.android.material.chip.Chip;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTagFragment extends DialogFragment {

    public static final String TAG = "AddTagFragment";

    FragmentAddTagBinding binding;
    ArrayList tags = new ArrayList();

    public AddTagFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static AddTagFragment newInstance(String title) {
        AddTagFragment frag = new AddTagFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddTagBinding.inflate(getLayoutInflater(), container, false);

        // layout of fragment is stored in a special property called root
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        binding.btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: ");
                // Closes the activity, pass the data to parent
                addTag(binding.chipArtsy);
                addTag(binding.chipBohemian);
                addTag(binding.chipCasual);
                addTag(binding.chipChic);
                addTag(binding.chipOffice);
                addTag(binding.chipPreppy);
                addTag(binding.chipRocker);
                addTag(binding.chipSexy);
                addTag(binding.chipSophisticated);
                addTag(binding.chipStreet);
                addTag(binding.chipTomboy);
                addTag(binding.chipVintage);
                sendBackResult();
            }
        });
    }

    private void addTag(Chip chip) {
        if (chip.isChecked()) {
            tags.add(chip.getText().toString().toLowerCase());
        }
    }
    // Defines the listener interface
    public interface AddTagDialogListener {
        void onFinishAddTag(ArrayList tags);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        AddTagDialogListener listener = (AddTagDialogListener) getTargetFragment();
        listener.onFinishAddTag(tags);
        dismiss();
    }
}