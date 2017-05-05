package com.yashladha.shop.module;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopDetails extends Fragment {

    private String LOG_TAG = getClass().getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private boolean status = true;
    private String uid;

    private ImageView defaultImage;
    private ImageView image0;
    private ImageView image1;
    private ImageView image2;
    private TextView ShopUserName;
    private TextView ShopName;
    private TextView userEmail;
    private TextView shopDescription;
    private TextView shopAddress;
    private TextView phone;
    private TextView zoneInfo;
    private TextView shopCateogry;


    public ShopDetails() {
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
        View v = inflater.inflate(R.layout.fragment_shop_details, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        checkUser();

        defaultImage = (ImageView) v.findViewById(R.id.ivDefaultImage);
        image0 = (ImageView) v.findViewById(R.id.ivImage0);
        image1 = (ImageView) v.findViewById(R.id.ivImage1);
        image2 = (ImageView) v.findViewById(R.id.ivImage2);
        ShopUserName = (TextView) v.findViewById(R.id.tvUserName);
        ShopName = (TextView) v.findViewById(R.id.tvShopName);
        userEmail = (TextView) v.findViewById(R.id.tvEmailUSer);
        shopDescription = (TextView) v.findViewById(R.id.tvShopDescription);
        shopAddress = (TextView) v.findViewById(R.id.tvAddress);
        phone = (TextView) v.findViewById(R.id.tvPhone);
        zoneInfo = (TextView) v.findViewById(R.id.tvZone);
        shopCateogry = (TextView) v.findViewById(R.id.tvCateogry);

        fetchImages();
        fetchData();
        return v;
    }

    private void fetchData() {
        uid = mAuth.getCurrentUser().getUid();
        DatabaseReference databaseRef = database.getReference();
        databaseRef = databaseRef.child("users").child(uid);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, dataSnapshot.getValue().toString());
                Shop shop = dataSnapshot.getValue(Shop.class);
                ShopUserName.setText(shop.getOwnerFirstName()+" "+shop.getOwnerLastName());
                shopAddress.setText(shop.getAddress());
                ShopName.setText(shop.getShopName());
                userEmail.setText(shop.getEmail());
                shopDescription.setText(shop.getShopDescription());
                phone.setText(shop.getPhoneNumber());
                zoneInfo.setText(shop.getZone());
                shopCateogry.setText(shop.getCateogry());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchImages() {
        uid = mAuth.getCurrentUser().getUid();
        StorageReference defaultImageRef = storage.getReference().child(uid);
        StorageReference defImage = defaultImageRef.child("default/def.jpg");
        defaultImageRef = defaultImageRef.child("ShopImages");
        StorageReference image_0_ref = defaultImageRef.child("0/0.jpg");
        StorageReference image_1_ref = defaultImageRef.child("1/1.jpg");
        StorageReference image_2_ref = defaultImageRef.child("2/2.jpg");
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(image_0_ref).into(image0);
        Log.d(LOG_TAG, "Image 0 cached");
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(image_1_ref).into(image1);
        Log.d(LOG_TAG, "Image 1 cached");
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(image_2_ref).into(image2);
        Log.d(LOG_TAG, "Image 2 cached");
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(defImage).into(defaultImage);
        Log.d(LOG_TAG, "Default Image Cached");
    }

    private void checkUser() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    status = true;
                    uid = user.getUid();
                    Log.d(LOG_TAG, "Uid of the Shop User is: " + uid);
                }
                else
                    status = false;
            }
        };
    }

}
