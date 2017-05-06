package com.yashladha.shop.module;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("VisibleForTests")
public class Register extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String LOG_TAG = getClass().getSimpleName();
    private boolean status;
    private boolean images_set;
    private Uri imageDefault;
    private String userId;
    Bitmap imageBitmap = null;
    private FirebaseStorage storage;
    private ProgressBar progressBar;
    private LinearLayout layout;

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
            Log.d(LOG_TAG, "ContentUri Image: " + data.getData());
            Uri selectedImage = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), selectedImage);
                ImageView imageView = (ImageView) getActivity().findViewById(R.id.shop_image);
                imageView.setImageURI(selectedImage);
                RadioButton image_checker = (RadioButton) getActivity().findViewById(R.id.image_selected);
                image_checker.setChecked(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(LOG_TAG, "Image Path: " + getRealPathFromURI(getContext(), selectedImage));
            imageDefault = Uri.fromFile(new File(getRealPathFromURI(getContext(), selectedImage)));
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String proj[] = {MediaStore.Images.Media.DATA};
            cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
            int columnIndex = 0;
            if (cursor != null) {
                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(columnIndex);
            } else {
                Toast.makeText(getContext(), "No Images in Database", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return "No Path Found";
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
        layout = (LinearLayout) v.findViewById(R.id.registerLinearLayout);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        images_checked.setEnabled(false); // enabled only when user selects some images

        Button imagePicker = (Button) v.findViewById(R.id.register_image_chooser);
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
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
                if (email_u.length() == 0 || pwd.length() == 0) {
                    Toast.makeText(getContext(), "Email or password field is empty", Toast.LENGTH_SHORT).show();
                } else {
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
                                        if (uploadData(getContext(), shop))
                                            Log.d(LOG_TAG, "Data Uploaded Successfully");
                                        else
                                            Log.d(LOG_TAG, "Data not uploaded successfully");
                                        status = true;
                                        // TODO : go back to previous fragment
                                    } else {
                                        Toast.makeText(getContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        return v;
    }

    // Uses Firebase Storage Options
    private boolean uploadData(Context context, Shop shopObj) {
        boolean uploadFlag = false;
        if (status) {
            String uid = mAuth.getCurrentUser().getUid();
            Log.d(LOG_TAG, "User registered is(Upload Data): " + uid);
            StorageReference storageRef = storage.getReference();
            StorageReference userRef = storageRef.child(uid);
            progressBar.setVisibility(View.VISIBLE);
            layout.setVisibility(View.INVISIBLE);
            uploadFromFile(userRef);
            uploadFlag = true;
        }
        return uploadFlag;
    }

    private void uploadFromFile(StorageReference userRef) {
        StorageReference userDefaultImage = userRef.child("default/def.jpg");
        Log.d(LOG_TAG, "File name: " + imageDefault.getLastPathSegment());
        UploadTask uploadTask = userDefaultImage.putFile(imageDefault);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_TAG, "Default Image cannot be uploaded succesfully");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                layout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(LOG_TAG, "Upload is " + progress + "% done");
                Toast.makeText(getContext(), "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void database_create(Shop shop, String uid) {
        if (status) {
            userId = uid;
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
