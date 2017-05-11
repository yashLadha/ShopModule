package com.yashladha.shop.module;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("VisibleForTests")
public class user extends Fragment {

    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    private String LOG_TAG = getClass().getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseStorage storage;
    private FirebaseDatabase database;

    private Button multiImagePicker;
    private RadioButton flagMultiImage;
    private EditText addressShop;
    private Button submitButton;
    private Button cateogryButton;
    private Button zoneButton;
    private EditText phoneNumber;
    private ProgressBar progressBar;
    private LinearLayout layout;
    private boolean status;

    private String uid;
    private String shopCateogry;
    private String shopZone;

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
        zoneButton = (Button) v.findViewById(R.id.btZone);
        phoneNumber = (EditText) v.findViewById(R.id.etPhoneNumber);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBarMultiImage);
        layout = (LinearLayout) v.findViewById(R.id.user_linear_layout);
        progressBar.setVisibility(View.INVISIBLE);

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
                        shopCateogry = (String) cateogries[which];
                    }
                });
                builder.show();
            }
        });

        zoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence zones[] = new CharSequence[] {
                        "North", "East", "West", "South", "Center"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pick a Zone");
                builder.setItems(zones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Zone Selected: " + zones[which], Toast.LENGTH_SHORT).show();
                        shopZone = (String) zones[which];
                    }
                });
                builder.show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        checkUser();
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
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToDatabase();
                Log.d(LOG_TAG, "Images and address data uploaded successfully");
                Fragment transactionFragment = new ShopDetails();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, transactionFragment);
                transaction.commit();
            }
        });
        return v;
    }

    private void uploadToDatabase() {
        DatabaseReference databaseRef = database.getReference();
        String shopAddress = addressShop.getText().toString();
        String phone = phoneNumber.getText().toString();
        DatabaseReference userInfo = databaseRef.child("users").child(uid);
        HashMap<String, Object> shopValue = new HashMap<>();
        shopValue.put("Address", shopAddress);
        shopValue.put("Cateogry", shopCateogry);
        shopValue.put("Zone", shopZone);
        shopValue.put("phoneNumber", phone);
        Log.d(LOG_TAG, "Key to Node: " + userInfo.push().getKey());
        userInfo.updateChildren(shopValue).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(LOG_TAG, "Address and Phone Data Uploaded Successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, "Phone and Address data not uploaded successfully");
            }
        });
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
                    status = true;
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
            progressBar.setVisibility(View.VISIBLE);
            layout.setVisibility(View.INVISIBLE);
            uploadImages(image_uris);
            layout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            flagMultiImage.setChecked(true);
        }
    }

    private void uploadImages(ArrayList<Uri> image_uris) {
        StorageReference storageRef = storage.getReference();
        StorageReference userRef = storageRef.child(uid).child("ShopImages");
        for (int i = 0; i < image_uris.size(); i++) {
            uploadImage(i, image_uris.get(i), userRef);
        }
    }

    private void uploadImage(int pos, Uri imageUri, StorageReference userRef) {
        StorageReference shopImage = userRef.child(String.valueOf(pos) + "/" + String.valueOf(pos) + ".jpg");
        Log.d(LOG_TAG, "Image Name: " + imageUri.getLastPathSegment());
        final int val = pos;
        imageUri = Uri.fromFile(new File(imageUri.toString()));
        Log.d(LOG_TAG, "Image Uri: " + pos + " " + imageUri.toString());
        UploadTask uploadTask = shopImage.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, "Shop Image no: " + String.valueOf(val) + " Not Uploaded Successfully");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                Log.d(LOG_TAG, "Uploaded Successfully");
                Toast.makeText(getContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Toast.makeText(getContext(), "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Log.d(LOG_TAG, "Task Completed");
            }
        });
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
