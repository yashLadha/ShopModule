package com.yashladha.shop.module;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class user extends Fragment {

    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    private String LOG_TAG = getClass().getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button multiImagePicker;
    private RadioButton flagMultiImage;
    private EditText addressShop;
    private Button submitButton;
    private Button cateogryButton;

    private String uid;

    public user() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        multiImagePicker = (Button) v.findViewById(R.id.btMultiImagePicker);
        flagMultiImage = (RadioButton) v.findViewById(R.id.rbtFlagMultiImage);
        addressShop = (EditText) v.findViewById(R.id.etAddressShop);
        submitButton = (Button) v.findViewById(R.id.btSubmitExtraInfo);
        cateogryButton = (Button) v.findViewById(R.id.btCateogry);

        cateogryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence cateogries[] = new CharSequence[] {
                        "Eating", "Clothing", "Shopping", "Fun", "Other"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pick a cateogry");
                builder.setItems(cateogries, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Cateogry Selected: " + cateogries[which], Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        boolean status = checkUser();
        if (status) {
            Log.d(LOG_TAG, "User exists: " + uid);
        } else {
            Log.d(LOG_TAG, "Error in login!!! Login Incorrectly");
        }
        multiImagePicker.setClickable(true);
        multiImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImages();
                flagMultiImage.setChecked(true);
            }
        });
        return v;
    }

    private boolean checkUser() {
        final boolean[] flag = {false};
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(LOG_TAG, "User logged in with uid: " + user.getUid());
                    uid = user.getUid();
                    flag[0] = true;
                }
            }
        };
        return flag[0];
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK ) {

            ArrayList<Uri>  image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            for (int i = 0; i < image_uris.size(); i++) {
                Toast.makeText(getContext(), image_uris.get(i).toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getImages() {

        // Config for the Multiple Image Selector library
        Config config = new Config();
        config.setSelectionMin(2);
        config.setSelectionLimit(4);
        ImagePickerActivity.setConfig(config);

        Intent intent  = new Intent(getContext(), ImagePickerActivity.class);
        startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);

    }

}
