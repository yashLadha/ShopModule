package com.yashladha.shop.module;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = getClass().getSimpleName();
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to exit from this app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.nav_view_main);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Fragment intentFragment = null;
                switch (id) {
                    case R.id.register:
                        Toast.makeText(getApplicationContext(), "Register is Pressed", Toast.LENGTH_SHORT).show();
                        intentFragment = new Register();
                        break;

                    case R.id.login:
                        Toast.makeText(getApplicationContext(), "Login is Pressed", Toast.LENGTH_SHORT).show();
                        intentFragment = new login();
                        break;
                }
                if (intentFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, intentFragment);
                    transaction.commit();
                }
                navigationView.setCheckedItem(id);
                drawer_layout.closeDrawer(navigationView);
                return true;
            }
        });
        setUpToggle(toolbar, drawer_layout);
        drawer_layout.setDrawerListener(mDrawerToggle);

    }



    private void setUpToggle(Toolbar toolbar, DrawerLayout navigationView) {
        mDrawerToggle = new ActionBarDrawerToggle(this, navigationView, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
    }
}
