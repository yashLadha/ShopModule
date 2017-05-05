package com.yashladha.shop.module;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Fragment to allow the multiple image picker
public class MultipleImagePicker extends Fragment {


    private String LOG_TAG = getClass().getSimpleName();

    public MultipleImagePicker() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_multiple_image_picker, container, false);
        return v;
    }

}
