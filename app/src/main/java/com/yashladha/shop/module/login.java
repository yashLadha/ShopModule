package com.yashladha.shop.module;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class login extends Fragment {

    private String LOG_TAG = getClass().getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean status;

    public login() {
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
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        checkUser();

        final EditText etEmail = (EditText) v.findViewById(R.id.et_login_email);
        final EditText etPassword = (EditText) v.findViewById(R.id.et_login_password);

        Button register = (Button) v.findViewById(R.id.bt_login_register);
        // Button to register
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment intentFragment = new Register();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, intentFragment);
                fragmentTransaction.commit();
            }
        });

        Button Login = (Button) v.findViewById(R.id.bt_login);
        // Button for login
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (email.length() > 0 && password.length() > 0) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(LOG_TAG, "signInWithEmailAndPassword: " + task.isSuccessful());
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                        Log.d(LOG_TAG, "Login Successful");

                                        status = true;
                                        Fragment transitionFragment = new user();
                                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.replace(R.id.content_frame, transitionFragment);
                                        fragmentTransaction.commit();
                                    } else {
                                        Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                                        status = false;
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Email and Password are not valid", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
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
