package com.yashladha.shop.module;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Register extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String LOG_TAG = getClass().getSimpleName();
    private boolean status;

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

    public Register() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        Button Register = (Button) v.findViewById(R.id.bt_register);
        final EditText firstName = (EditText) v.findViewById(R.id.et_first_name);
        final EditText lastName = (EditText) v.findViewById(R.id.et_last_name);
        final EditText shopName = (EditText) v.findViewById(R.id.et_shop_name);
        final EditText email = (EditText) v.findViewById(R.id.et_email);
        final EditText password = (EditText) v.findViewById(R.id.et_password);
        final EditText shopDescription = (EditText) v.findViewById(R.id.et_description);
        final RadioButton images_checked = (RadioButton) v.findViewById(R.id.image_selected);

        images_checked.setEnabled(false); // enabled only when user selects some images

        Button imagePicker = (Button) v.findViewById(R.id.register_image_chooser);
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
                images_checked.setChecked(true);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        checkUser();
        if (status) {
            Log.d(LOG_TAG, "User Logged in: ");
        }
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = firstName.getText().toString();
                String lName = lastName.getText().toString();
                String sName = shopName.getText().toString();
                String email_u = email.getText().toString();
                String pwd = password.getText().toString();
                String description = shopDescription.getText().toString();

                final Shop shop = new Shop(fName, lName, email_u, sName, pwd, description);
                mAuth.createUserWithEmailAndPassword(email_u, pwd)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(LOG_TAG, "createUserWithEmailAndPassword: " + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    String uid = mAuth.getCurrentUser().getUid();
                                    Log.d(LOG_TAG, "User registered: " + uid);
                                    Toast.makeText(getContext(), "Register Successful", Toast.LENGTH_SHORT).show();
                                    database_create(shop, uid);
                                    status = true;
                                    // TODO : go back to previous fragment
                                } else {
                                    Log.d(LOG_TAG, String.valueOf(task.getResult()));
                                    Toast.makeText(getContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        return v;
    }

    private void database_create(Shop shop, String uid) {
        if (status) {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference mUserRef = database.child("users");
            Log.d(LOG_TAG, "creating database of user: " + uid);
            DatabaseReference userRef = mUserRef.child(uid);
            userRef.setValue(shop);
            Log.d(LOG_TAG, "Database entry created");
        }
    }

    private void checkUser() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                status = user != null;
            }
        };
    }

}
