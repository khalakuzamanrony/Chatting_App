package com.android.personalchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.personalchat.tab_fragments.Blog2Fragment;
import com.android.personalchat.tab_fragments.BlogFragment;
import com.android.personalchat.tab_fragments.FriendsFragment;
import com.android.personalchat.tab_fragments.RequestFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
    }

    private void init() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewpagerAdapter viewpagerAdapter = new ViewpagerAdapter(getSupportFragmentManager());
        
        viewpagerAdapter.AddFra(new Blog2Fragment(), "Blog");
       // viewpagerAdapter.AddFra(new BlogFragment(), "Blogs");
        viewpagerAdapter.AddFra(new RequestFragment(), "Request");
        viewpagerAdapter.AddFra(new FriendsFragment(), "Friends");
        viewPager.setAdapter(viewpagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser == null) {
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
        } else {
            databaseReference.child(firebaseUser.getUid()).child("online_status").setValue("online");
            databaseReference.child(firebaseUser.getUid()).child("online").setValue("online");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseUser != null) {
            databaseReference.child(firebaseUser.getUid()).child("online_status").setValue("offline");
            databaseReference.child(firebaseUser.getUid()).child("online").setValue(String.valueOf(System.currentTimeMillis()));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            databaseReference.child(firebaseUser.getUid()).child("online_status").setValue("offline");
            databaseReference.child(firebaseUser.getUid()).child("online").setValue(String.valueOf(System.currentTimeMillis()));
            firebaseUser=null;
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
        } else if (item.getItemId() == R.id.ac_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        } else if (item.getItemId() == R.id.all_users) {
            startActivity(new Intent(getApplicationContext(), AllUsers.class));
        }
        return true;
    }

    class ViewpagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewpagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void AddFra(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}