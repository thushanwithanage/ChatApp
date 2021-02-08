package com.thushan.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.thushan.app2.Fragments.ChatsFragment;
import com.thushan.app2.Fragments.ProfileFragment;
import com.thushan.app2.Fragments.UsersFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Tab layout and View pager

        TabLayout tabLayout = findViewById( R.id.tabLayout );
        ViewPager viewPager = findViewById( R.id.view_pager );

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter( getSupportFragmentManager() );

        viewPagerAdapter.addFragment( new ChatsFragment(), "Chats" );
        viewPagerAdapter.addFragment( new UsersFragment(), "Users" );
        viewPagerAdapter.addFragment( new ProfileFragment(), "Profile" );

        viewPager.setAdapter( viewPagerAdapter );
        tabLayout.setupWithViewPager( viewPager );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity( new Intent( MainActivity.this, LoginActivity.class ).setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ) );
                return true;
        }
        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
            this.fragments = new ArrayList<>(  );
            this.titles = new ArrayList<>(  );
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get( position );
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            fragments.add( fragment );
            titles.add( title );
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get( position );
        }


    }

    private void checkStatus(String status)
    {
        String time1 = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        ref = FirebaseDatabase.getInstance().getReference("ChatUsers").child( firebaseUser.getUid() );
        HashMap<String, Object> hashMap = new HashMap<>(  );
        hashMap.put( "status", status );
        hashMap.put( "lastseen", date1 + " " + time1 );
        ref.updateChildren( hashMap );
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus( "online" );
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkStatus( "offline" );
    }
}