package com.example.appnews;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.TabletTransformer;
import com.example.appnews.model.Common;
import com.example.appnews.activity.FavoriteActivity;
import com.example.appnews.activity.SignIn;
import com.example.appnews.adapter.CategoryFragmentPagerAdapter;
import com.example.appnews.model.Channel;
import com.example.appnews.sharePref.SharePreference;
import com.example.appnews.utils.Constants;

import java.util.List;

import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity  implements  NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ViewPager viewPager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView tvName;
    Button btnSignout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init Paper
        Paper.init(this);
        //set DrawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //Find id to allow the user to swipe between fragments
        viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);

        //add TabLayout and  set gravity
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        btnSignout = (Button) header.findViewById(R.id.btnSignout);
        btnSignout.setOnClickListener(this);
        String name = "";
        name = Common.currentUser.toString();
        if(name == null) {
            return;
        }
        else {
            tvName = (TextView) header.findViewById(R.id.tvName);
            tvName.setText("Xin chào: " + Common.currentUser.getName());
        }

        // Set the default fragment
        CategoryFragmentPagerAdapter pagerAdapter =
                new CategoryFragmentPagerAdapter(this
                        , getSupportFragmentManager());
        // Set the pager adapter onto the view pager
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new TabletTransformer());
        onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Switch Fragments in a ViewPager on clicking items in Navigation Drawer
        if (id == R.id.nav_word) {
            viewPager.setCurrentItem(Constants.WORLD);
        } else if (id == R.id.nav_entertainment) {
            viewPager.setCurrentItem(Constants.ENTERTAINMENT);
        } else if (id == R.id.nav_balance) {
            viewPager.setCurrentItem(Constants.BALANCE);
        } else if (id == R.id.nav_business) {
            viewPager.setCurrentItem(Constants.BUSINESS);
        } else if (id == R.id.nav_education) {
            viewPager.setCurrentItem(Constants.EDUCATION);
        } else if (id == R.id.nav_sport) {
            viewPager.setCurrentItem(Constants.SPORT);
        } else if (id == R.id.nav_technology) {
            viewPager.setCurrentItem(Constants.TECHNOLOGY);
        } else if (id == R.id.nav_social) {
            viewPager.setCurrentItem(Constants.SOCIAL);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.favorite,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_favorites:
                Intent intent = new Intent(this, FavoriteActivity.class);
                startActivity(intent);
            case R.id.menu_setting:
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Paper.book().destroy();
        SharePreference sharePreference = new SharePreference();
        List<Channel> channels = sharePreference.getFavorites(this);
        sharePreference.ClearAll(this, channels);
        finish();
        Intent i = new Intent(this, SignIn.class);
        startActivity(i);
    }
}
